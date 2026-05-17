package com.kssjw.minecarttrainsfork.client.manager;

import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

public class ParticleManager {

    public static void linkParticle(AbstractMinecartEntity entity) {
        defaultLinkParticle(entity);

        if (ClientLoadManager.isAPIFound()) customLinkParticle(entity);
    }

    public static void headParticle(AbstractMinecartEntity entity) {
        defaultHeadParticle(entity);

        if (ClientLoadManager.isAPIFound()) customHeadParticle(entity);
    }

    public static void linkLine(AbstractMinecartEntity entity, VertexConsumerProvider vertexConsumers) {
        line(entity, vertexConsumers);
    }
    
    // 默认连接粒子
    private static void defaultLinkParticle(AbstractMinecartEntity cart) {
        if (ClientLoadManager.isAPIFound() && ClientConfigManager.isEnabledDefaultLinkParticle() == false) return;
        if (cart == null) return;                
        if (!(cart.getWorld() instanceof ClientWorld world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = 40;  // 每 X 时间刻染一次
        final int MAX_STEPS = 6;    // 每次最多生成 X 个粒子
        long ticks = MinecraftClient.getInstance().inGameHud.getTicks();

        if (ticks % FRAME_SKIP != 0) return;

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();

        if (parentCartUuid == null) return;

        AbstractMinecartEntity parentCart = null;

        // 1.21
        for (Entity entity : world.getEntities()) {
            if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(parentCartUuid)) parentCart = (AbstractMinecartEntity) entity;
        }

        if (parentCart == null) return;

        Vec3d parentPos = parentCart.getPos();

        // 粒子位置
        double sx = parentPos.x;
        double sy = parentPos.y + 0.6;
        double sz = parentPos.z;
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
                world.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    // 自定义连接粒子
    private static void customLinkParticle(AbstractMinecartEntity cart) {
        if (ClientConfigManager.isEnabledCustomLinkParticle() == false) return;
        if (cart == null) return;
        if (!(cart.getWorld() instanceof ClientWorld world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = ClientConfigManager.getCustomLinkParticleCycle();
        final int MAX_STEPS = 6;
        long ticks = MinecraftClient.getInstance().inGameHud.getTicks();

        if (ticks % FRAME_SKIP != 0) return;

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();

        if (parentCartUuid == null) return;

        AbstractMinecartEntity parentCart = null;

        // 1.21
        for (Entity entity : world.getEntities()) {
            if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(parentCartUuid)) parentCart = (AbstractMinecartEntity) entity;
        }

        if (parentCart == null) return;

        Vec3d parentPos = parentCart.getPos();

        // 粒子位置
        double sx = parentPos.x;
        double sy = parentPos.y + 0.6;
        double sz = parentPos.z;
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
                world.addParticle(ClientConfigManager.getSelectedLinkParticle(), px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    // 默认头车粒子
    private static void defaultHeadParticle(AbstractMinecartEntity cart) {
        if (ClientLoadManager.isAPIFound() && ClientConfigManager.isEnabledDefaultHeadParticle() == false) return;
        if (!(cart.getEntityWorld() instanceof ClientWorld world)) return;

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();
        AbstractMinecartEntity parentCart = null;

        // 1.21
        for (Entity entity : world.getEntities()) {
            if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(parentCartUuid)) parentCart = (AbstractMinecartEntity) entity;
        }

        if (parentCartUuid != null && parentCart != null) return;

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = 40; // 每 X 时间刻染一次
        final int MAX_HEAD_PARTICLES = 6;
        long ticks = MinecraftClient.getInstance().inGameHud.getTicks();

        if (ticks % FRAME_SKIP_HEAD != 0) return;

        // 粒子位置
        double baseX = cart.getX();
        double baseY = cart.getY() + 0.8;
        double baseZ = cart.getZ();

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

                try {
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    // 自定义头车粒子
    private static void customHeadParticle(AbstractMinecartEntity cart) {
        if (ClientConfigManager.isEnabledCustomHeadParticle() == false) return;
        if (!(cart.getEntityWorld() instanceof ClientWorld world)) return;

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();
        AbstractMinecartEntity parentCart = null;

        // 1.21
        for (Entity entity : world.getEntities()) {
            if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(parentCartUuid)) parentCart = (AbstractMinecartEntity) entity;
        }

        if (parentCartUuid != null && parentCart != null) return;

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = ClientConfigManager.getCustomHeadParticleCycle();
        final int MAX_HEAD_PARTICLES = 6;
        long ticks = MinecraftClient.getInstance().inGameHud.getTicks();
        
        if (ticks % FRAME_SKIP_HEAD != 0) return;

        // 粒子位置
        double baseX = cart.getX();
        double baseY = cart.getY() + 0.8;
        double baseZ = cart.getZ();

        for (int i = 0; i < MAX_HEAD_PARTICLES; i++) {
            double offsetX = (Math.random() - 0.5) * 0.4;
            double offsetY = (Math.random() - 0.5) * 0.2;
            double offsetZ = (Math.random() - 0.5) * 0.4;
            double px = baseX + offsetX;
            double py = baseY + offsetY;
            double pz = baseZ + offsetZ;

            try {
                world.addParticle(ClientConfigManager.getSelectedHeadParticle(), px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    private static void line(AbstractMinecartEntity cart, VertexConsumerProvider vertexConsumers) {
        if (ClientLoadManager.isAPIFound() && ClientConfigManager.isEnabledLinkLine() == false) return;
        if (cart == null) return;
        if (!(cart.getEntityWorld() instanceof ClientWorld world)) return;

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();

        if (parentCartUuid == null) return;

        AbstractMinecartEntity parentCart = null;

        // 1.21
        for (Entity entity : world.getEntities()) {
            if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(parentCartUuid)) parentCart = (AbstractMinecartEntity) entity;
        }

        if (parentCart == null) return;

        Vec3d cartPos = cart.getPos();
        Vec3d parentPos = parentCart.getPos();

        // >= 1.20.5
        Vec3d camPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();

        if (camPos == null) return;

        Vec3d pos1 = cartPos.subtract(camPos);
        Vec3d pos2 = parentPos.subtract(camPos);

        Vec3d dir = pos2.subtract(pos1).normalize(); // 方向向量

        // 边缘点，保证线条在两车之间
        double offset = cart.getWidth() / 2.0;
        Vec3d pos1Edge = pos1.add(offset * dir.x, 0, offset * dir.z);
        Vec3d pos2Edge = pos2.add(-offset * dir.x, 0, -offset * dir.z);

        Vec3d up = Math.abs(dir.y) > 0.9 ? new Vec3d(1,0,0) : new Vec3d(0,1,0);

        // >= 1.20.5
        Matrix4f matrix = (new MatrixStack()).peek().getPositionMatrix();
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutoutNoCull(Identifier.of(MinecartTrainsFork.MOD_ID, "textures/chain.png")));

        double width = 0.05;

        Vec3d side1 = dir.crossProduct(up).normalize().multiply(width);    // 水平
        Vec3d side2 = dir.crossProduct(side1).normalize().multiply(width); // 垂直

        Vec3d a1 = pos1Edge.add(side1);
        Vec3d a2 = pos2Edge.add(side1);
        Vec3d a3 = pos2Edge.subtract(side1);
        Vec3d a4 = pos1Edge.subtract(side1);

        Vec3d b1 = pos1Edge.add(side2);
        Vec3d b2 = pos2Edge.add(side2);
        Vec3d b3 = pos2Edge.subtract(side2);
        Vec3d b4 = pos1Edge.subtract(side2);

        int light = WorldRenderer.getLightmapCoordinates(cart.getEntityWorld(), BlockPos.ofFloored(cartPos.add(parentPos).multiply(0.5)));

        Vec3d normal1 = side1.normalize();
        Vec3d normal2 = side2.normalize();

        /*
            *   ---|    1 --------- 2   |---
            *      |    |           |   |
            * cart |    |           |   | cart
            *      |    |           |   |
            *   ---|    4 --------- 3   |---
        */

        // 水平
        consumer.vertex(matrix, (float)a1.x, (float)(a1.y + 0.3), (float)a1.z)
            .texture(0.0F, 0.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal1.x, (float)normal1.y, (float)normal1.z);

        consumer.vertex(matrix, (float)a2.x, (float)(a2.y + 0.3), (float)a2.z)
            .texture(1.0F, 0.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal1.x, (float)normal1.y, (float)normal1.z);

        consumer.vertex(matrix, (float)a3.x, (float)(a3.y + 0.3), (float)a3.z)
            .texture(1.0F, 1.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal1.x, (float)normal1.y, (float)normal1.z);

        consumer.vertex(matrix, (float)a4.x, (float)(a4.y + 0.3), (float)a4.z)
            .texture(0.0F, 1.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal1.x, (float)normal1.y, (float)normal1.z);

        // 垂直
        consumer.vertex(matrix, (float)b1.x, (float)(b1.y + 0.3), (float)b1.z)
            .texture(0.0F, 0.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal2.x, (float)normal2.y, (float)normal2.z);

        consumer.vertex(matrix, (float)b2.x, (float)(b2.y + 0.3), (float)b2.z)
            .texture(1.0F, 0.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal2.x, (float)normal2.y, (float)normal2.z);

        consumer.vertex(matrix, (float)b3.x, (float)(b3.y + 0.3), (float)b3.z)
            .texture(1.0F, 1.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal2.x, (float)normal2.y, (float)normal2.z);

        consumer.vertex(matrix, (float)b4.x, (float)(b4.y + 0.3), (float)b4.z)
            .texture(0.0F, 1.0F)
            .color(255, 255, 255, 255)
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(light)
            .normal((float)normal2.x, (float)normal2.y, (float)normal2.z);
    }
}