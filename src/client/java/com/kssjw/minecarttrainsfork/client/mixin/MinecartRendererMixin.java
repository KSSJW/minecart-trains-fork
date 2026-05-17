package com.kssjw.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ParticleManager;
import com.kssjw.minecarttrainsfork.util.LogUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.world.entity.vehicle.AbstractMinecart;

@Mixin(MinecartRenderer.class)
public class MinecartRendererMixin {

    @Inject(method = "render(Lnet/minecraft/world/entity/vehicle/AbstractMinecart;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At("TAIL"))
    private void injectRender(
        AbstractMinecart entity,
        float yaw,
        float tickDelta,
        PoseStack matrices,
        MultiBufferSource vertexConsumers,
        int light,
        CallbackInfo ci
    ) { 
        try {
            ParticleManager.linkParticle(entity);
        } catch (Throwable ex) {
            LogUtil.print("Link particle error: " + ex);
        }

        try {
            ParticleManager.headParticle(entity);
        } catch (Throwable ex) {
            LogUtil.print("Head particle error: " + ex);
        }

        try {
            ParticleManager.linkLine(entity, vertexConsumers);
        } catch (Throwable ex) {
            LogUtil.print("Link line error: " + ex);
        }
    }
}