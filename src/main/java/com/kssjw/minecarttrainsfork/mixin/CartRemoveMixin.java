package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.util.UnLinkUtil;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

@Mixin(Entity.class)
public class CartRemoveMixin {
    @Inject(method = "remove", at = @At("TAIL"))
    private void onRemove(Entity.RemovalReason reason, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;
        if (self instanceof AbstractMinecartEntity) {
            IChainableUtil icu = (IChainableUtil)(Object)this;
            World world = ((Entity)(Object)this).getWorld();
            if (!world.isClient()) {
                ServerWorld serverWorld = (ServerWorld)world;
                UnLinkUtil.unlinkHandle(icu, serverWorld, null);
            }
        }
    }
}
