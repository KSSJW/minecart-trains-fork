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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@Mixin(AbstractMinecart.class)
public class AbstractMinecartMixin implements IChainableUtil {

    @Unique private @Nullable UUID parentUUID;
    @Unique private @Nullable UUID childUUID;

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

    @Override
    public @Nullable AbstractMinecart getChainedParent() {
        return (AbstractMinecart)(
            (
                (ServerLevel)(
                    (
                        (AbstractMinecart)(Object)this
                    ).level()
                )
            ).getEntity(this.getParentUUID())
        );
    }

    @Override
    public void setChainedParent(@Nullable AbstractMinecart newParent) {
        LinkUtil.setChainedParent(newParent, (IChainableUtil)(Object)this);
    }


    @Override
    public @Nullable AbstractMinecart getChainedChild() {
        return (AbstractMinecart)(
            (ServerLevel)(
                (
                    (AbstractMinecart)(Object)this
                ).level()
            )
        ).getEntity(this.getChildUUID());
    }

    @Override
    public void setChainedChild(@Nullable AbstractMinecart newChild) {
        LinkUtil.setChainedChild(newChild, (IChainableUtil)(Object)this);
    }

    // 数据存储与读取
    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void mctrains$writeData(CompoundTag nbt, CallbackInfo ci) {
        DataUtil.writeData(nbt, (IChainableUtil)(Object)this);
    }

    @Inject(method="readAdditionalSaveData", at = @At("TAIL"))
    public void mctrains$readData(CompoundTag nbt, CallbackInfo ci) {
        DataUtil.readData(nbt, (IChainableUtil)(Object)this);
    }
}