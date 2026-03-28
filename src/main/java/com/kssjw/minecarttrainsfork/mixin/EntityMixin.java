package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.util.UnLinkUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

@Mixin(Entity.class)
public class EntityMixin {
    
    @Inject(method = "remove", at = @At("TAIL"))
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;

        if (self instanceof AbstractMinecart) {
            IChainableUtil icu = (IChainableUtil)(Object)this;
            Level world = ((Entity)(Object)this).level();
            
            if (!world.isClientSide()) {
                ServerLevel serverWorld = (ServerLevel)world;
                UnLinkUtil.unlinkHandle(icu, serverWorld, null);
            }
        }
    }
}