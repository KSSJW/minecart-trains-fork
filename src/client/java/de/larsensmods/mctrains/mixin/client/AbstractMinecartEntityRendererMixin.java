package de.larsensmods.mctrains.mixin.client;

import net.minecraft.client.render.entity.AbstractMinecartEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.state.MinecartEntityRenderState;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

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
@Mixin(AbstractMinecartEntityRenderer.class)
public abstract class AbstractMinecartEntityRendererMixin<T extends AbstractMinecartEntity, S extends MinecartEntityRenderState> extends EntityRenderer<T, S> {

    @Unique
    private AbstractMinecartEntity childCart = null;

    protected AbstractMinecartEntityRendererMixin(EntityRendererFactory.Context context) {super(context);}

    @Inject(method = "Lnet/minecraft/client/render/entity/AbstractMinecartEntityRenderer;updateRenderState(Lnet/minecraft/entity/vehicle/AbstractMinecartEntity;Lnet/minecraft/client/render/entity/state/MinecartEntityRenderState;F)V", at= @At("TAIL"))
    public void mctrains(T abstractMinecartEntity, S minecartEntityRenderState, float f, CallbackInfo ci){
        childCart = abstractMinecartEntity;
    }

    // 把此方法放在你已验证可命中的 updateRenderState 注入（或替换原有粒子逻辑）
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

            // 使用 MC 自带的 END_ROD 粒子（浅蓝）
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
}
