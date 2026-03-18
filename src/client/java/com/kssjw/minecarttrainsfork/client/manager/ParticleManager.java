package com.kssjw.minecarttrainsfork.client.manager;

import org.joml.Matrix4f;

import com.kssjw.minecarttrainsfork.util.PositionUitl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class ParticleManager {

    public static void linkParticle(AbstractMinecartEntity entity) {
        defaultLinkParticle(entity);

        if (LoadManager.isAPIFound()) customLinkParticle(entity);
    }

    public static void headParticle(AbstractMinecartEntity entity) {
        defaultHeadParticle(entity);

        if (LoadManager.isAPIFound()) customHeadParticle(entity);
    }

    public static void linkLine(AbstractMinecartEntity entity) {
        line(entity);
    }
    
    // 默认连接粒子
    private static void defaultLinkParticle(AbstractMinecartEntity cart) {
        if (LoadManager.isAPIFound() && ConfigManager.isEnabledDefaultLinkParticle() == false) return;
        if (cart == null) return;                
        if (!(cart.getWorld() instanceof ClientWorld world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = 40;  // 每 X 时间刻染一次
        final int MAX_STEPS = 6;    // 每次最多生成 X 个粒子
        long ticks = MinecraftClient.getInstance().inGameHud.getTicks();

        if (ticks % FRAME_SKIP != 0) return;

        Vec3d parentPos = PositionUitl.getParentPos(cart.getUuid());

        if (parentPos == null) return;

        // 粒子位置
        double sx = parentPos.getX();
        double sy = parentPos.getY() + 0.6;
        double sz = parentPos.getZ();
        double ex = cart.getX();
        double ey = cart.getY() + 0.6;
        double ez = cart.getZ();

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
                world.addParticleClient(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    // 自定义连接粒子
    private static void customLinkParticle(AbstractMinecartEntity cart) {
        if (ConfigManager.isEnabledCustomLinkParticle() == false) return;
        if (cart == null) return;
        if (!(cart.getWorld() instanceof ClientWorld world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = ConfigManager.getCustomLinkParticleCycle();
        final int MAX_STEPS = 6;
        long ticks = MinecraftClient.getInstance().inGameHud.getTicks();

        if (ticks % FRAME_SKIP != 0) return;

        Vec3d parentPos = PositionUitl.getParentPos(cart.getUuid());

        if (parentPos == null) return;

        // 粒子位置
        double sx = parentPos.getX();
        double sy = parentPos.getY() + 0.6;
        double sz = parentPos.getZ();
        double ex = cart.getX();
        double ey = cart.getY() + 0.6;
        double ez = cart.getZ();

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
                world.addParticleClient(ConfigManager.getSelectedLinkParticle(), px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    // 默认头车粒子
    private static void defaultHeadParticle(AbstractMinecartEntity entity) {
        if (LoadManager.isAPIFound() && ConfigManager.isEnabledDefaultHeadParticle() == false) return;
        if (!(entity.getWorld() instanceof ClientWorld world)) return;
        if (PositionUitl.getParentPos(entity.getUuid()) != null) return;

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = 40; // 每 X 时间刻染一次
        final int MAX_HEAD_PARTICLES = 6;
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

                try {
                    world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    // 自定义头车粒子
    private static void customHeadParticle(AbstractMinecartEntity entity) {
        if (ConfigManager.isEnabledCustomHeadParticle() == false) return;
        if (!(entity.getWorld() instanceof ClientWorld)) return;
        if (PositionUitl.getParentPos(entity.getUuid()) != null) return;

        ClientWorld world = (ClientWorld) entity.getWorld();

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = ConfigManager.getCustomHeadParticleCycle();
        final int MAX_HEAD_PARTICLES = 6;
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
                world.addParticleClient(ConfigManager.getSelectedHeadParticle(), px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    private static void line(AbstractMinecartEntity entity) {
        if (LoadManager.isAPIFound() && ConfigManager.isEnabledLinkLine() == false) return;

        Vec3d parentPos = PositionUitl.getParentPos(entity.getUuid());

        if (parentPos == null) return;

        Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getCameraPos();
        Vec3d pos1 = entity.getPos().subtract(camPos);
        Vec3d pos2 = parentPos.subtract(camPos);

        // 方向向量
        Vec3d dir = pos2.subtract(pos1).normalize();
        double dirX = dir.x;
        double dirZ = dir.z;

        // 边缘点，保证线条在两车之间
        double offset = entity.getWidth() / 2.0;
        Vec3d pos1Edge = pos1.add(offset * dirX, 0, offset * dirZ);
        Vec3d pos2Edge = pos2.add(-offset * dirX, 0, -offset * dirZ);

        // 构造圆截面基向量
        Vec3d up = Math.abs(dir.y) > 0.9 ? new Vec3d(1,0,0) : new Vec3d(0,1,0);
        Vec3d side = dir.crossProduct(up).normalize();
        up = side.crossProduct(dir).normalize();

        MatrixStack matrices = new MatrixStack();
        MatrixStack.Entry entry = matrices.peek();
        Matrix4f matrix = entry.getPositionMatrix();
        VertexConsumer consumer = MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers().getBuffer(RenderLayer.getSolid());

        int segments = 12; // 圆截面分段数
        float radius = 0.04F; // 半径

        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            Vec3d offset1 = side.multiply(Math.cos(angle1) * radius).add(up.multiply(Math.sin(angle1) * radius));
            Vec3d offset2 = side.multiply(Math.cos(angle2) * radius).add(up.multiply(Math.sin(angle2) * radius));

            // 在 pos1Edge 和 pos2Edge 两端生成圆环顶点
            Vec3d v1 = pos1Edge.add(offset1);
            Vec3d v2 = pos1Edge.add(offset2);
            Vec3d v3 = pos2Edge.add(offset2);
            Vec3d v4 = pos2Edge.add(offset1);

            int light = WorldRenderer.getLightmapCoordinates(entity.getWorld(), BlockPos.ofFloored(pos1Edge));

            // 渲染四边形 v1-v2-v3-v4
            consumer.vertex(matrix, (float)v1.x, (float)(v1.y + 0.3), (float)v1.z).color(0xFF252c3d).texture(0.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0.0F, 1.0F, 0.0F);
            consumer.vertex(matrix, (float)v2.x, (float)(v2.y + 0.3), (float)v2.z).color(0xFF252c3d).texture(1.0F, 0.0F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0.0F, 1.0F, 0.0F);
            consumer.vertex(matrix, (float)v3.x, (float)(v3.y + 0.3), (float)v3.z).color(0xFF252c3d).texture(1.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0.0F, 1.0F, 0.0F);
            consumer.vertex(matrix, (float)v4.x, (float)(v4.y + 0.3), (float)v4.z).color(0xFF252c3d).texture(0.0F, 1.0F).overlay(OverlayTexture.DEFAULT_UV).light(light).normal(0.0F, 1.0F, 0.0F);
        }
    }
}