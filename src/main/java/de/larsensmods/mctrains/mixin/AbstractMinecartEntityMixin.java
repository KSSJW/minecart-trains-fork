package de.larsensmods.mctrains.mixin;

import de.larsensmods.mctrains.interfaces.IChainable;
import de.larsensmods.mctrains.networking.ClientboundSyncMinecartTrainPacket;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;

@Debug(export = true)
@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements IChainable {

    @Unique private @Nullable UUID parentUUID;
    @Unique private @Nullable UUID childUUID;
    @Unique private int parentClientID;
    @Unique private int childClientID;

    public AbstractMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void mctrains$tick(CallbackInfo info) {
        World world = this.getWorld();
        if (!world.isClient()) {
            if (getChainedParent() != null) {
                double distance = getChainedParent().distanceTo((Entity)(Object)this) - 1;

                if (distance <= 4) {
                    Vec3d directionToParent = getChainedParent().getPos().subtract(getPos()).normalize();

                    if (distance > 1) {
                        Vec3d parentVelocity = getChainedParent().getVelocity();

                        if (parentVelocity.length() == 0) {
                            setVelocity(directionToParent.multiply(0.05));
                        } else {
                            setVelocity(directionToParent.multiply(parentVelocity.length()));
                            setVelocity(getVelocity().multiply(distance));
                        }
                    } else if (distance < 0.8) {
                        setVelocity(directionToParent.multiply(-0.05));
                    } else {
                        setVelocity(Vec3d.ZERO);
                    }
                } else {
                    IChainable.unsetChainedParentChild(getChainedParent(), (AbstractMinecartEntity)(Object)this);
                    ((AbstractMinecartEntity)(Object)this).dropStack(new ItemStack(Items.CHAIN));
                    return;
                }

                if (getChainedParent().isRemoved()) {
                    IChainable.unsetChainedParentChild(getChainedParent(), (AbstractMinecartEntity)(Object)this);
                }
            }

            if (getChainedChild() != null && getChainedChild().isRemoved()) {
                IChainable.unsetChainedParentChild((AbstractMinecartEntity)(Object)this, getChainedChild());
            }

            for (Entity otherEntity : world.getOtherEntities((Entity)(Object)this, getBoundingBox().expand(0.1), this::collidesWith)) {
                if (otherEntity instanceof AbstractMinecartEntity otherCart && getChainedParent() != null && !otherCart.equals(getChainedChild())) {
                    otherCart.setVelocity(getVelocity());
                }
            }
        }
    }

    @Override
    public @Nullable AbstractMinecartEntity getChainedParent() {
        World world = this.getWorld();
        Entity entity = (world instanceof ServerWorld sWorld && this.parentUUID != null) ? sWorld.getEntity(this.parentUUID) : (world.getEntityById(this.parentClientID));
        return entity instanceof AbstractMinecartEntity minecart ? minecart : null;
    }

    @Override
    public void setChainedParent(@Nullable AbstractMinecartEntity newParent) {
        if (newParent != null) {
            this.parentUUID = newParent.getUuid();
            this.parentClientID = newParent.getId();
        } else {
            this.parentUUID = null;
            this.parentClientID = -1;
        }
        World world = this.getWorld();
        if (!world.isClient()) {
            PlayerLookup.tracking((Entity)(Object)this).forEach(player -> {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                new ClientboundSyncMinecartTrainPacket(
                    getChainedParent() != null ? getChainedParent().getId() : -1,
                    getId()
                ).write(buf);
                ServerPlayNetworking.send(player, ClientboundSyncMinecartTrainPacket.ID, buf);
            });
        }
    }

    @Override
    public void setClientChainedParent(int entityId) {
        this.parentClientID = entityId;
    }

    @Override
    public @Nullable AbstractMinecartEntity getChainedChild() {
        World world = this.getWorld();
        Entity entity = (world instanceof ServerWorld sWorld && this.childUUID != null) ? sWorld.getEntity(this.childUUID) : (world.getEntityById(this.childClientID));
        return entity instanceof AbstractMinecartEntity minecart ? minecart : null;
    }

    @Override
    public void setChainedChild(@Nullable AbstractMinecartEntity newChild) {
        if (newChild != null) {
            this.childUUID = newChild.getUuid();
            this.childClientID = newChild.getId();
        } else {
            this.childUUID = null;
            this.childClientID = -1;
        }
    }

    @Override
    public void setClientChainedChild(int entityId) {
        this.childClientID = entityId;
    }

    //DATA STORAGE NBT
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void mctrains$writeData(NbtCompound writeView, CallbackInfo ci) {
        writeView.putLong("ParentUUIDMost", parentUUID != null ? parentUUID.getMostSignificantBits() : 0L);
        writeView.putLong("ParentUUIDLeast", parentUUID != null ? parentUUID.getLeastSignificantBits() : 0L);

        writeView.putLong("ChildUUIDMost", childUUID != null ? childUUID.getMostSignificantBits() : 0L);
        writeView.putLong("ChildUUIDLeast", childUUID != null ? childUUID.getLeastSignificantBits() : 0L);

        writeView.putInt("ParentClientID", parentClientID);
        writeView.putInt("ChildClientID", childClientID);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("TAIL"))
    public void mctrains$readData(NbtCompound nbt, CallbackInfo ci) {
        long parentMost = 0L;
        long parentLeast = 0L;
        if (nbt.contains("ParentUUIDMost") && nbt.contains("ParentUUIDLeast")) {
            parentMost = nbt.getLong("ParentUUIDMost");
            parentLeast = nbt.getLong("ParentUUIDLeast");
        }
        this.parentUUID = (parentMost != 0L || parentLeast != 0L) ? new UUID(parentMost, parentLeast) : null;

        long childMost = 0L;
        long childLeast = 0L;
        if (nbt.contains("ChildUUIDMost") && nbt.contains("ChildUUIDLeast")) {
            childMost = nbt.getLong("ChildUUIDMost");
            childLeast = nbt.getLong("ChildUUIDLeast");
        }
        this.childUUID = (childMost != 0L || childLeast != 0L) ? new UUID(childMost, childLeast) : null;

        if (nbt.contains("ParentClientID")) this.parentClientID = nbt.getInt("ParentClientID");
        else this.parentClientID = -1;

        if (nbt.contains("ChildClientID")) this.childClientID = nbt.getInt("ChildClientID");
        else this.childClientID = -1;
    }

}