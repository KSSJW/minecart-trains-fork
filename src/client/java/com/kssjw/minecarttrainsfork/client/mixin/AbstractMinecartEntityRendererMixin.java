package com.kssjw.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ParticleManager;
import com.kssjw.minecarttrainsfork.util.LogUtil;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

@Mixin(AbstractMinecartEntityRenderer.class)
public class AbstractMinecartEntityRendererMixin {
    private CameraRenderState cachedCamera;
    private MatrixStack cachedStack;
    private OrderedRenderCommandQueue collector;

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At("HEAD")
    )
    private void injectRender(MinecartEntityRenderState state, MatrixStack poseStack, OrderedRenderCommandQueue submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
        cachedCamera = camera;
        cachedStack = poseStack;
        collector = submitNodeCollector;
    }

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void injectUpdateRenderState(
        AbstractMinecartEntity entity,
        MinecartEntityRenderState state,
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