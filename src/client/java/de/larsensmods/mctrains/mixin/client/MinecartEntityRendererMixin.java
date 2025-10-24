package de.larsensmods.mctrains.mixin.client;

import de.larsensmods.mctrains.interfaces.IChainable;
import de.larsensmods.mctrains.util.ChainableHelpers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MinecartEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Debug(export = false)
@Mixin(MinecartEntityRenderer.class)
public abstract class MinecartEntityRendererMixin<T extends AbstractMinecartEntity, S extends AbstractMinecartEntity> extends EntityRenderer<T> {

    protected MinecartEntityRendererMixin(EntityRendererFactory.Context context) { super(context); }

    @Inject(method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V", at = @At("HEAD"))
    public void mctrains(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ){
        // 保留占位或后续扩展；不再使用 childCart 字段以避免未使用警告
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

            // 先把实体安全转换为 IChainable
            IChainable childChain = ChainableHelpers.asChainable((Entity) entity);
            if (childChain == null) return;

            AbstractMinecartEntity parent = childChain.getChainedParent() != null ? childChain.getChainedParent().asMinecart() : null;
            AbstractMinecartEntity child = childChain.asMinecart();
            if (parent == null || child == null) return;

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
            if (entity == null) return;

            IChainable chain = ChainableHelpers.asChainable((Entity) entity);
            if (chain == null) return;

            // 头车：只有没有父车时才渲染头车粒子
            if (chain.getChainedParent() != null) return;

            AbstractMinecartEntity head = chain.asMinecart();
            if (head == null) return;
            if (!(head.getEntityWorld() instanceof ClientWorld)) return;
            ClientWorld world = (ClientWorld) head.getEntityWorld();

            final int FRAME_SKIP_HEAD = 40;         // 每 X 时间刻染一次
            final int MAX_HEAD_PARTICLES = 6;      // 每次最多生成 X 个粒子
            long ticks = MinecraftClient.getInstance().inGameHud.getTicks();
            if (ticks % FRAME_SKIP_HEAD != 0) return;

            // 在 head 周围生成粒子（示例位置，可以按需要调整）
            for (int i = 0; i < MAX_HEAD_PARTICLES; i++) {
                double px = head.getX() + (Math.random() - 0.5) * 0.6;
                double py = head.getY() + 0.5 + Math.random() * 0.3;
                double pz = head.getZ() + (Math.random() - 0.5) * 0.6;
                try {
                    world.addParticle(ParticleTypes.COMPOSTER, px, py, pz, 0.0, 0.0, 0.0);
                } catch (Throwable e) {
                    try { world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                    catch (Throwable ignored) { break; }
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: renderHeadParticles particle error: " + ex);
        }
    }
}