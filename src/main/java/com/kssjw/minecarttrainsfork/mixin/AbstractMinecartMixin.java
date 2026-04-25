package com.kssjw.minecarttrainsfork.mixin;

import com.kssjw.minecarttrainsfork.manager.TrainManager;
import com.kssjw.minecarttrainsfork.util.LinkUtil;
import com.kssjw.minecarttrainsfork.util.DataUtil;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin implements IChainableUtil {

    @Unique private @Nullable UUID parentUUID;
    @Unique private @Nullable UUID childUUID;
    @Unique private static final EntityDataAccessor<Integer> PARENT_ID = SynchedEntityData.defineId(AbstractMinecart.class, EntityDataSerializers.INT);

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

    @Inject(method = "tick", at = @At("HEAD"))
    private void mctrains$tick(CallbackInfo ci) {
        TrainManager.tick((AbstractMinecart)(Object)this);
    }

    @Inject(method = "defineSynchedData", at = @At("TAIL"))
    private void initTracker(SynchedEntityData.Builder builder, CallbackInfo ci) {
        builder.define(PARENT_ID, -1);
    }

    @Override
    public @Nullable AbstractMinecart getChainedParent() {
        return (AbstractMinecart)((AbstractMinecart)(Object)this).level().getEntity(this.getParentUUID());
    }

    @Override
    public void setChainedParent(@Nullable AbstractMinecart newParent) {
        LinkUtil.setChainedParent(newParent, (IChainableUtil)(Object)this);
    }


    @Override
    public @Nullable AbstractMinecart getChainedChild() {
        return (AbstractMinecart)((AbstractMinecart)(Object)this).level().getEntity(this.getChildUUID());
    }

    @Override
    public void setChainedChild(@Nullable AbstractMinecart newChild) {
        LinkUtil.setChainedChild(newChild, (IChainableUtil)(Object)this);
    }

    // 数据存储与读取
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void mctrains$writeData(ValueOutput writeView, CallbackInfo ci) {
        DataUtil.writeData(writeView, (IChainableUtil)(Object)this);
    }

    @Inject(method="readAdditionalSaveData", at = @At("TAIL"))
    public void mctrains$readData(ValueInput readView, CallbackInfo ci) {
        DataUtil.readData(readView, (IChainableUtil)(Object)this);
    }
}