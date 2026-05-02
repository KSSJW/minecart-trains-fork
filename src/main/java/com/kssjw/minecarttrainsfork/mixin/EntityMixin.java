package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.util.UnLinkUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(method = "onStartedTrackingBy", at = @At("TAIL"))
    private void injectStartSeenByPlayer(ServerPlayerEntity player, CallbackInfo ci) {
        Entity self = (Entity)(Object)this;

        if (self instanceof AbstractMinecartEntity) NetworkManager.sendRelationshipPayload(self.getUuid(), ((IChainableUtil) self).getParentUUID(), player);
    }
    
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