package org.kssjw.minecarttrainsfork.client.mixin;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.kssjw.minecarttrainsfork.client.manager.ParticleManager;
import org.kssjw.minecarttrainsfork.util.LogUtil;
import com.mojang.blaze3d.vertex.PoseStack;

@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartRendererMixin {
    private CameraRenderState cachedCamera;
    private PoseStack cachedStack;
    private SubmitNodeCollector collector;

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;" +
            "Lcom/mojang/blaze3d/vertex/PoseStack;" +
            "Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
            "Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD")
    )
    private void injectSubmit(MinecartRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        cachedCamera = camera;
        cachedStack = poseStack;
        collector = submitNodeCollector;
    }

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void injectUpdateRenderState(
        AbstractMinecart entity,
        MinecartRenderState state,
        float tickDelta,
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
            ParticleManager.linkLine(entity, cachedCamera.pos, cachedStack, collector);
        } catch (Throwable ex) {
            LogUtil.print("Link line error: " + ex);
        }
    }
}