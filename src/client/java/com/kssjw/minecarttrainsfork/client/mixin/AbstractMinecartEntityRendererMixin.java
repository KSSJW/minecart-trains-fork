package com.kssjw.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ParticleManager;
import com.kssjw.minecarttrainsfork.util.LogUtil;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

@Mixin(AbstractMinecartRenderer.class)
public class AbstractMinecartEntityRendererMixin {

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
            ParticleManager.linkLine(entity);
        } catch (Throwable ex) {
            LogUtil.print("Link line error: " + ex);
        }
    }
}