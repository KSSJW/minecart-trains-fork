package com.kssjw.minecarttrainsfork.client.mixin;

import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ParticleManager;
import com.kssjw.minecarttrainsfork.util.LogUtil;

@Mixin(AbstractMinecartEntityRenderer.class)
public abstract class AbstractMinecartEntityRendererMixin<T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> extends EntityRenderer<T, S> {

    protected AbstractMinecartEntityRendererMixin(EntityRendererFactory.Context context) {super(context);}

    // 此方法放在已验证可命中的 updateRenderState 注入，矿车之间的粒子渲染
    @Inject(method = "Lnet/minecraft/client/render/entity/AbstractMinecartEntityRenderer;updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V", at = @At("TAIL"))
    public void mctrains$updateRenderState(
        AbstractMinecartEntity entity,
        MinecartEntityRenderState state,
        float tickDelta,
        CallbackInfo ci
    ) {
        try {
            ParticleManager.linkParticle(entity);
        } catch (Throwable ex) {
            LogUtil.print("UpdateRenderState particle error: " + ex);
        }
    }

    // 头车渲染粒子
    @Inject(method = "Lnet/minecraft/client/render/entity/AbstractMinecartEntityRenderer;updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V", at = @At("TAIL"))
    public void mctrains$renderHeadParticles(
        AbstractMinecartEntity entity,
        MinecartEntityRenderState state,
        float tickDelta,
        CallbackInfo ci
    ) {
        try {
            ParticleManager.headParticle(entity);
        } catch (Throwable ex) {
            LogUtil.print("Head particle error: " + ex);
        }
    }
}
