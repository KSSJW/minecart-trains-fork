package com.kssjw.minecarttrainsfork.client.mixin;

import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.MinecraftClient;

@Mixin(AbstractMinecartEntityRenderer.class)
public abstract class AbstractMinecartEntityRendererMixin<T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> extends EntityRenderer<T, S> {

    @Unique
    private AbstractMinecartEntity childCart = null;

    protected AbstractMinecartEntityRendererMixin(EntityRendererFactory.Context context) {super(context);}

    @Inject(method = "Lnet/minecraft/client/render/entity/AbstractMinecartEntityRenderer;updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V", at= @At("TAIL"))
    public void mctrains(T abstractMinecartEntity, S minecartEntityRenderState, float f, CallbackInfo ci){
        childCart = abstractMinecartEntity;
    }

    // 此方法放在已验证可命中的 updateRenderState 注入，矿车之间的粒子渲染
    @Inject(method = "Lnet/minecraft/client/render/entity/AbstractMinecartEntityRenderer;updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V", at = @At("TAIL"))
    public void mctrains$updateRenderState(
        AbstractMinecartEntity entity,
        MinecartEntityRenderState state,
        float tickDelta,
        CallbackInfo ci
    ) {
        try {
            if (entity == null) return;
            AbstractMinecartEntity child = entity;
            AbstractMinecartEntity parent = child.getChainedParent();
            if (parent == null) return;
            if (!(child.getEntityWorld() instanceof ClientWorld)) return;
            ClientWorld world = (ClientWorld) child.getEntityWorld();
            
            // 速度与最大数量
            final int FRAME_SKIP = 40;
            final int MAX_STEPS = 6;
            long ticks = MinecraftClient.getInstance().inGameHud.getTicks();
            if (ticks % FRAME_SKIP != 0) return;

            // 粒子位置
            double sx = parent.getX();
            double sy = parent.getY() + 0.6;
            double sz = parent.getZ();
            double ex = child.getX();
            double ey = child.getY() + 0.6;
            double ez = child.getZ();

            double dx = ex - sx, dy = ey - sy, dz = ez - sz;
            double distSq = dx*dx + dy*dy + dz*dz;
            if (distSq < 1e-6) return;
            double dist = Math.sqrt(distSq);

            double spacing = Math.max(0.25, dist / MAX_STEPS);
            int steps = Math.min(MAX_STEPS, Math.max(1, (int)Math.ceil(dist / spacing)));

            // 使用 MC 自带的 粒子
            for (int i = 0; i <= steps; i++) {
                double t = (double)i / (double)steps;
                double px = sx + dx * t;
                double py = sy + dy * t;
                double pz = sz + dz * t;
                try {
                    world.addParticleClient(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.0, 0.0);
                } catch (Throwable e) {
                    // 退回到 FLAME 以防某些 mappings 签名不匹配
                    try { world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                    catch (Throwable ignored) { break; }
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: updateRenderState particle error: " + ex);
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
            if (entity == null || entity.getChainedParent() != null) return;
            if (!(entity.getEntityWorld() instanceof ClientWorld)) return;
            ClientWorld world = (ClientWorld) entity.getEntityWorld();

            final int FRAME_SKIP_HEAD = 40;         // 每 X 时间刻染一次
            final int MAX_HEAD_PARTICLES = 6;      // 每次最多生成 X 个粒子
            long ticks = MinecraftClient.getInstance().inGameHud.getTicks();
            if (ticks % FRAME_SKIP_HEAD != 0) return;

            // 粒子位置
            double baseX = entity.getX();
            double baseY = entity.getY() + 0.8;
            double baseZ = entity.getZ();

            for (int i = 0; i < MAX_HEAD_PARTICLES; i++) {
                double offsetX = (Math.random() - 0.5) * 0.4;
                double offsetY = (Math.random() - 0.5) * 0.2;
                double offsetZ = (Math.random() - 0.5) * 0.4;
                double px = baseX + offsetX;
                double py = baseY + offsetY;
                double pz = baseZ + offsetZ;

                try {
                    world.addParticleClient(ParticleTypes.COMPOSTER, px, py, pz, 0.0, 0.0, 0.0);
                } catch (Throwable e) {
                    try { world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                    catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: head particle error: " + ex);
        }
    }
}
