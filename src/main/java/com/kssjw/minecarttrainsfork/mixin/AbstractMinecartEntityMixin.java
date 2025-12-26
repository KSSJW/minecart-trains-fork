package com.kssjw.minecarttrainsfork.mixin;

import com.kssjw.minecarttrainsfork.manager.TrainManager;
import com.kssjw.minecarttrainsfork.util.LinkUtil;
import com.kssjw.minecarttrainsfork.util.DataUtil;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends VehicleEntity implements IChainableUtil {

    @Unique private @Nullable UUID parentUUID;
    @Unique private @Nullable UUID childUUID;
    @Unique private int parentClientID;
    @Unique private int childClientID;

    @Override
    public UUID getParentUUID() {
        return parentUUID;
    }

    @Override
    public void setParentUUID(UUID uuid) {
        this.parentUUID = uuid;
    }

    @Override
    public UUID getChildUUID() {
        return childUUID;
    }

    @Override
    public void setChildUUID(UUID uuid) {
        this.childUUID = uuid;
    }

    @Override
    public int getParentClientID() {
        return parentClientID;
    }

    @Override
    public void setParentClientID(int id) {
        this.parentClientID = id;
    }

    @Override
    public int getChildClientID() {
        return childClientID;
    }

    @Override
    public void setChildClientID(int id) {
        this.childClientID = id;
    }

    public AbstractMinecartEntityMixin(EntityType<?> entityType, World world) {super(entityType, world);}

    @Inject(method = "tick", at = @At("HEAD"))
    private void mctrains$tick(CallbackInfo ci) {
        TrainManager.tick((AbstractMinecartEntity)(Object)this);
    }

    @Override
    public @Nullable AbstractMinecartEntity getChainedParent() {
        return LinkUtil.getChainedParent((AbstractMinecartEntity)(Object)this, (IChainableUtil)(Object)this);
    }

    @Override
    public void setChainedParent(@Nullable AbstractMinecartEntity newParent) {
        LinkUtil.setChainedParent(newParent, (IChainableUtil)(Object)this, (AbstractMinecartEntity)(Object)this);
    }

    @Override
    public void setClientChainedParent(int entityId) {
        LinkUtil.setClientChainedParent(entityId, (IChainableUtil)(Object)this);
    }

    @Override
    public @Nullable AbstractMinecartEntity getChainedChild() {
        return LinkUtil.getChainedChild((AbstractMinecartEntity)(Object)this, (IChainableUtil)(Object)this);
    }

    @Override
    public void setChainedChild(@Nullable AbstractMinecartEntity newChild) {
        LinkUtil.setChainedChild(newChild, (IChainableUtil)(Object)this);
    }

    @Override
    public void setClientChainedChild(int entityId) {
        LinkUtil.setClientChainedChild(entityId, (IChainableUtil)(Object)this);
    }

    // 数据存储与读取
    @Inject(method = "writeCustomDataToNbt", at = @At("TAIL"))
    public void mctrains$writeData(NbtCompound nbt, CallbackInfo ci) {
        DataUtil.writeData(nbt, (IChainableUtil)(Object)this);
    }

    @Inject(method="readCustomDataFromNbt", at = @At("TAIL"))
    public void mctrains$readData(NbtCompound nbt, CallbackInfo ci) {
        DataUtil.readData(nbt, (IChainableUtil)(Object)this);
    }
}