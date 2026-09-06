package org.fuseleaf.minecarttrainsfork.mixin;

import org.fuseleaf.minecarttrainsfork.manager.TrainManager;
import org.fuseleaf.minecarttrainsfork.util.DataUtil;
import org.fuseleaf.minecarttrainsfork.util.IChainableUtil;
import org.fuseleaf.minecarttrainsfork.util.LinkUtil;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

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
    private void injectTick(CallbackInfo ci) {
        TrainManager.tick((AbstractMinecart)(Object)this);
    }

    @Override
    public @Nullable AbstractMinecart getChainedParent() {
        UUID parentUUID = getParentUUID();

        if (parentUUID == null) {
            return null;
        }

        return (AbstractMinecart)((AbstractMinecart)(Object)this).level().getEntity(parentUUID);
    }

    @Override
    public void setChainedParent(@Nullable AbstractMinecart newParent) {
        LinkUtil.setChainedParent(newParent, (IChainableUtil)(Object)this);
    }


    @Override
    public @Nullable AbstractMinecart getChainedChild() {
        UUID childUUID = getChildUUID();

        if (childUUID == null) {
            return null;
        }

        return (AbstractMinecart)((AbstractMinecart)(Object)this).level().getEntity(childUUID);
    }

    @Override
    public void setChainedChild(@Nullable AbstractMinecart newChild) {
        LinkUtil.setChainedChild(newChild, (IChainableUtil)(Object)this);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void injectAddAdditionalSaveData(ValueOutput writeView, CallbackInfo ci) {
        DataUtil.writeData(writeView, (IChainableUtil)(Object)this);
    }

    @Inject(method="readAdditionalSaveData", at = @At("TAIL"))
    public void injectReadAdditionalSaveData(ValueInput readView, CallbackInfo ci) {
        DataUtil.readData(readView, (IChainableUtil)(Object)this);
    }
}
