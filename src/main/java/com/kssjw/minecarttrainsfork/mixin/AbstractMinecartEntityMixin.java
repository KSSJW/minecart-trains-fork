package com.kssjw.minecarttrainsfork.mixin;

import com.kssjw.minecarttrainsfork.manager.TrainManager;
import com.kssjw.minecarttrainsfork.util.LinkUtil;
import com.kssjw.minecarttrainsfork.util.DataUtil;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AbstractMinecartEntity.class)
public class AbstractMinecartEntityMixin implements IChainableUtil {

    @Unique private @Nullable UUID parentUUID;
    @Unique private @Nullable UUID childUUID;
    @Unique private static final TrackedData<Integer> PARENT_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);

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
        TrainManager.tick((AbstractMinecartEntity)(Object)this);
    }

    @Override
    public @Nullable AbstractMinecartEntity getChainedParent() {
        return (AbstractMinecartEntity)(
            (
                (ServerWorld)(
                    (
                        (AbstractMinecartEntity)(Object)this
                    ).getWorld()
                )
            ).getEntity(this.getParentUUID())
        );
    }

    @Override
    public void setChainedParent(@Nullable AbstractMinecartEntity newParent) {
        LinkUtil.setChainedParent(newParent, (IChainableUtil)(Object)this);
    }


    @Override
    public @Nullable AbstractMinecartEntity getChainedChild() {
        return (AbstractMinecartEntity)(
            (ServerWorld)(
                (
                    (AbstractMinecartEntity)(Object)this
                ).getWorld()
            )
        ).getEntity(this.getChildUUID());
    }

    @Override
    public void setChainedChild(@Nullable AbstractMinecartEntity newChild) {
        LinkUtil.setChainedChild(newChild, (IChainableUtil)(Object)this);
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