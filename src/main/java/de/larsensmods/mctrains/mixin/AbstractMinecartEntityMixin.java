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
import net.minecraft.network.PacketByteBuf;
import io.netty.buffer.Unpooled;

import java.util.UUID;

@Debug(export = false)
@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements IChainable {

    @Unique private @Nullable UUID parentUUID;
    @Unique private @Nullable UUID childUUID;
    @Unique private int parentClientID = -1;
    @Unique private int childClientID = -1;

    public AbstractMinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    // ------------------------
    // tick logic (uses IChainable at compile-time)
    // ------------------------
    @Inject(method = "tick", at = @At("HEAD"))
    private void mctrains$tick(CallbackInfo info) {
        World world = this.getWorld();
        if (world.isClient()) return;

        // treat this as IChainable at runtime
        IChainable selfChain = (this instanceof IChainable) ? (IChainable) this : null;
        if (selfChain == null) return;

        IChainable parentChain = selfChain.getChainedParent();
        if (parentChain != null) {
            AbstractMinecartEntity parent = parentChain.asMinecart();
            AbstractMinecartEntity selfMinecart = selfChain.asMinecart();
            if (parent == null || selfMinecart == null) return;

            double distance = parent.distanceTo(selfMinecart) - 1;

            if (distance <= 4) {
                Vec3d directionToParent = parent.getPos().subtract(selfMinecart.getPos()).normalize();

                if (distance > 1) {
                    Vec3d parentVelocity = parent.getVelocity();

                    if (parentVelocity.length() == 0) {
                        selfMinecart.setVelocity(directionToParent.multiply(0.05));
                    } else {
                        selfMinecart.setVelocity(directionToParent.multiply(parentVelocity.length()));
                        selfMinecart.setVelocity(selfMinecart.getVelocity().multiply(distance));
                    }
                } else if (distance < 0.8) {
                    selfMinecart.setVelocity(directionToParent.multiply(-0.05));
                } else {
                    selfMinecart.setVelocity(Vec3d.ZERO);
                }
            } else {
                // disconnect and drop chain
                IChainable.unsetChainedParentChild(parentChain, selfChain);
                selfMinecart.dropStack(new ItemStack(Items.CHAIN));
                return;
            }

            Entity parentEntity = parentChain.asEntity();
            if (parentEntity == null || parentEntity.isRemoved()) {
                IChainable.unsetChainedParentChild(parentChain, selfChain);
            }
        }

        IChainable childChain = selfChain.getChainedChild();
        if (childChain != null) {
            Entity childEntity = childChain.asEntity();
            if (childEntity == null || childEntity.isRemoved()) {
                IChainable.unsetChainedParentChild(selfChain, childChain);
            }
        }

        // propagate velocity to nearby carts (use Entity methods; adjust getOtherEntities signature by mappings)
        for (Entity otherEntity : world.getOtherEntities((Entity)(Object)this, getBoundingBox().expand(0.1), this::collidesWith)) {
            if (otherEntity instanceof AbstractMinecartEntity otherCart) {
                IChainable selfParent = selfChain.getChainedParent();
                IChainable selfChild  = selfChain.getChainedChild();
                Entity childEntity = (selfChild != null) ? selfChild.asEntity() : null;
                if (selfParent != null && !otherCart.equals(childEntity)) {
                    otherCart.setVelocity(((Entity)(Object)this).getVelocity());
                }
            }
        }
    }

    // ------------------------
    // IChainable implementations (interface-level types)
    // ------------------------
    @Override
    public @Nullable IChainable getChainedParent() {
        World world = this.getWorld();
        Entity entity = null;
        try {
            if (world instanceof ServerWorld sWorld && this.parentUUID != null) {
                entity = sWorld.getEntity(this.parentUUID);
            } else {
                // defensive: if client or server but uuid absent, try client id
                if (this.parentClientID >= 0) entity = world.getEntityById(this.parentClientID);
            }
        } catch (Throwable ignored) {}
        return (entity instanceof IChainable) ? (IChainable) entity : null;
    }

    @Override
    public void setChainedParent(@Nullable IChainable newParent) {
        if (newParent != null) {
            Entity e = newParent.asEntity();
            if (e != null) {
                this.parentUUID = e.getUuid();
                // use getEntityId/getId depending on mappings; try to call getEntityId if available
                try { this.parentClientID = e.getId(); } catch (Throwable ex) { try { this.parentClientID = e.getId(); } catch (Throwable ignored) { this.parentClientID = -1; } }
            } else {
                this.parentUUID = null;
                this.parentClientID = -1;
            }
        } else {
            this.parentUUID = null;
            this.parentClientID = -1;
        }

        World world = this.getWorld();
        if (!world.isClient()) {
            PlayerLookup.tracking((Entity)(Object)this).forEach(player -> {
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                int parentId = (getChainedParent() != null) ? (getChainedParent().asEntity() != null ? safeGetEntityId(getChainedParent().asEntity()) : -1) : -1;
                int selfId   = safeGetEntityId((Entity)(Object)this);
                new ClientboundSyncMinecartTrainPacket(parentId, selfId).write(buf);
                ServerPlayNetworking.send(player, ClientboundSyncMinecartTrainPacket.ID, buf);
            });
        }
    }

    @Override
    public void setClientChainedParent(int entityId) {
        this.parentClientID = entityId;
    }

    @Override
    public @Nullable IChainable getChainedChild() {
        World world = this.getWorld();
        Entity entity = null;
        try {
            if (world instanceof ServerWorld sWorld && this.childUUID != null) {
                entity = sWorld.getEntity(this.childUUID);
            } else {
                if (this.childClientID >= 0) entity = world.getEntityById(this.childClientID);
            }
        } catch (Throwable ignored) {}
        return (entity instanceof IChainable) ? (IChainable) entity : null;
    }

    @Override
    public void setChainedChild(@Nullable IChainable newChild) {
        if (newChild != null) {
            Entity e = newChild.asEntity();
            if (e != null) {
                this.childUUID = e.getUuid();
                try { this.childClientID = e.getId(); } catch (Throwable ex) { try { this.childClientID = e.getId(); } catch (Throwable ignored) { this.childClientID = -1; } }
            } else {
                this.childUUID = null;
                this.childClientID = -1;
            }
        } else {
            this.childUUID = null;
            this.childClientID = -1;
        }
    }

    @Override
    public void setClientChainedChild(int entityId) {
        this.childClientID = entityId;
    }

    // ------------------------
    // IChainable entity accessors
    // ------------------------
    @Override
    public @Nullable Entity asEntity() {
        return (Entity)(Object)this;
    }

    @Override
    public @Nullable AbstractMinecartEntity asMinecart() {
        Entity e = asEntity();
        return (e instanceof AbstractMinecartEntity) ? (AbstractMinecartEntity) e : null;
    }

    // ------------------------
    // NBT persistence hooks (injects)
    // ------------------------
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

    // ------------------------
    // Utility: safe get entity id across mappings
    // ------------------------
    @Unique
    private static int safeGetEntityId(@Nullable Entity e) {
        if (e == null) return -1;
        try { return e.getId(); } catch (Throwable ex) {
            try { return e.getId(); } catch (Throwable ignored) { return -1; }
        }
    }
}