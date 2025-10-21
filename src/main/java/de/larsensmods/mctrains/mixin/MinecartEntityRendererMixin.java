package de.larsensmods.mctrains.mixin;

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

import de.larsensmods.mctrains.interfaces.IChainable;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.client.MinecraftClient;

@Debug(export = true)
@Mixin(MinecartEntityRenderer.class)
public abstract class MinecartEntityRendererMixin<T extends AbstractMinecartEntity> extends EntityRenderer<T> {

    @Unique
    private AbstractMinecartEntity childCart = null;

    protected MinecartEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at= @At("HEAD"))
    public void mctrains_updateRenderState(
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
            AbstractMinecartEntity parent = ((IChainable)(Object)child).getChainedParent();
            if (parent == null) return;
            if (!(child.getEntityWorld() instanceof ClientWorld)) return;
            ClientWorld world = (ClientWorld) child.getEntityWorld();

            final int FRAME_SKIP = 40;
            final int MAX_STEPS = 6;
            long ticks = MinecraftClient.getInstance().inGameHud.getTicks();
            if (ticks % FRAME_SKIP != 0) return;

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

            for (int i = 0; i <= steps; i++) {
                double t = (double)i / (double)steps;
                double px = sx + dx * t;
                double py = sy + dy * t;
                double pz = sz + dz * t;
                try {
                    world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.0, 0.0);
                } catch (Throwable e) {
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: updateRenderState particle error: " + ex);
        }
    }

    @Inject(method = "render(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"))
    public void mctrains_renderHeadParticles(
        T entity,
        float yaw,
        float tickDelta,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        try {
            if (entity == null || ((IChainable)(Object)entity).getChainedParent() != null) return;
            if (!(entity.getEntityWorld() instanceof ClientWorld)) return;
            ClientWorld world = (ClientWorld) entity.getEntityWorld();

            final int FRAME_SKIP_HEAD = 40;
            final int MAX_HEAD_PARTICLES = 6;
            long ticks = MinecraftClient.getInstance().inGameHud.getTicks();
            if (ticks % FRAME_SKIP_HEAD != 0) return;

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
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);
                }
            }
        } catch (Throwable ex) {
            System.out.println("mctrains: head particle error: " + ex);
        }
    }
}