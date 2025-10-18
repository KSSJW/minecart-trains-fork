package de.larsensmods.mctrains.mixin.client;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.MinecraftClient;

@Debug(export = true)
@Mixin(MinecartEntityRenderer.class)
public abstract class MinecartEntityRendererMixin<T extends AbstractMinecartEntity, S extends AbstractMinecartEntity> extends EntityRenderer<T> {

    @Unique
    private AbstractMinecartEntity childCart = null;

    protected MinecartEntityRendererMixin(EntityRendererFactory.Context context) {super(context);}

    @Inject(method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at= @At("HEAD"))
    public void mctrains(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ){
        childCart = entity;
    }

    // 矿车之间的粒子渲染
    @Inject(method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void mctrains$updateRenderState(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
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
                    world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.0, 0.0);
                } catch (Throwable e) {
                    // 退回到 FLAME 以防某些 mappings 签名不匹配
                    try { world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                    catch (Throwable ignored) { break; }
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: updateRenderState particle error: " + ex);
        }
    }

    // 头车渲染粒子
    @Inject(method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void mctrains$renderHeadParticles(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
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
                    world.addParticle(ParticleTypes.COMPOSTER, px, py, pz, 0.0, 0.0, 0.0);
                } catch (Throwable e) {
                    try { world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                    catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: head particle error: " + ex);
        }
    }
}
