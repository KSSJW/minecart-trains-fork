package com.kssjw.minecarttrainsfork.client.manager;

import org.joml.Matrix4f;

import com.kssjw.minecarttrainsfork.util.PositionUitl;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class ParticleManager {

    public static void linkParticle(AbstractMinecart entity) {
        defaultLinkParticle(entity);

        if (LoadManager.isAPIFound()) customLinkParticle(entity);
    }

    public static void headParticle(AbstractMinecart entity) {
        defaultHeadParticle(entity);

        if (LoadManager.isAPIFound()) customHeadParticle(entity);
    }

    public static void linkLine(AbstractMinecart entity) {
        line(entity);
    }
    
    // 默认连接粒子
    private static void defaultLinkParticle(AbstractMinecart cart) {
        if (LoadManager.isAPIFound() && ConfigManager.isEnabledDefaultLinkParticle() == false) return;
        if (cart == null) return;                
        if (!(cart.level() instanceof ClientLevel world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = 40;  // 每 X 时间刻染一次
        final int MAX_STEPS = 6;    // 每次最多生成 X 个粒子
        long ticks = Minecraft.getInstance().gui.getGuiTicks();

        if (ticks % FRAME_SKIP != 0) return;

        AbstractMinecart parentCart = (AbstractMinecart)(
            cart
                .level()
                    .getEntity(
                        PositionUitl.getParentUUID(cart.getUUID())
                    )
        );

        if (parentCart == null) return;

        Vec3 parentPos = parentCart.position();

        if (parentPos == null) return;

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
    private static void customLinkParticle(AbstractMinecart cart) {
        if (ConfigManager.isEnabledCustomLinkParticle() == false) return;
        if (cart == null) return;
        if (!(cart.level() instanceof ClientLevel world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = ConfigManager.getCustomLinkParticleCycle();
        final int MAX_STEPS = 6;
        long ticks = Minecraft.getInstance().gui.getGuiTicks();

        if (ticks % FRAME_SKIP != 0) return;

        AbstractMinecart parentCart = (AbstractMinecart)(
            cart
                .level()
                    .getEntity(
                        PositionUitl.getParentUUID(cart.getUUID())
                    )
        );

        if (parentCart == null) return;

        Vec3 parentPos = parentCart.position();

        if (parentPos == null) return;

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
                world.addParticle(ConfigManager.getSelectedLinkParticle(), px, py, pz, 0.0, 0.0, 0.0);
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
    private static void defaultHeadParticle(AbstractMinecart cart) {
        if (LoadManager.isAPIFound() && ConfigManager.isEnabledDefaultHeadParticle() == false) return;
        if (!(cart.level() instanceof ClientLevel world)) return;

        AbstractMinecart parentCart = (AbstractMinecart)(
            cart
                .level()
                    .getEntity(
                        PositionUitl.getParentUUID(cart.getUUID())
                    )
        );

        if (parentCart != null) return;

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = 40; // 每 X 时间刻染一次
        final int MAX_HEAD_PARTICLES = 6;
        long ticks = Minecraft.getInstance().gui.getGuiTicks();

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
    private static void customHeadParticle(AbstractMinecart cart) {
        if (ConfigManager.isEnabledCustomHeadParticle() == false) return;
        if (!(cart.level() instanceof ClientLevel world)) return;

        AbstractMinecart parentCart = (AbstractMinecart)(
            cart
                .level()
                    .getEntity(
                        PositionUitl.getParentUUID(cart.getUUID())
                    )
        );

        if (parentCart != null) return;

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = ConfigManager.getCustomHeadParticleCycle();
        final int MAX_HEAD_PARTICLES = 6;
        long ticks = Minecraft.getInstance().gui.getGuiTicks();
        
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
                world.addParticle(ConfigManager.getSelectedHeadParticle(), px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                try {
                    world.addParticle(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0);    // Fallback
                } catch (Throwable ignored) {
                    break;
                }
            }
        }
    }

    private static void line(AbstractMinecart cart) {
        if (LoadManager.isAPIFound() && ConfigManager.isEnabledLinkLine() == false) return;

        AbstractMinecart parentCart = (AbstractMinecart)(
            cart
                .level()
                    .getEntity(
                        PositionUitl.getParentUUID(cart.getUUID())
                    )
        );

        if (cart== null || parentCart == null) return;
        
        Vec3 cartPos = cart.position();
        Vec3 parentPos = parentCart.position();

        // >= 1.20.5
        Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().position();
        Vec3 pos1 = cartPos.subtract(camPos);
        Vec3 pos2 = parentPos.subtract(camPos);

        // 方向向量
        Vec3 dir = pos2.subtract(pos1).normalize();

        // 边缘点，保证线条在两车之间
        double offset = cart.getBbWidth() / 2.0;
        Vec3 pos1Edge = pos1.add(offset * dir.x, 0, offset * dir.z);
        Vec3 pos2Edge = pos2.add(-offset * dir.x, 0, -offset * dir.z);

        // 构造圆截面基向量
        Vec3 up = Math.abs(dir.y) > 0.9 ? new Vec3(1,0,0) : new Vec3(0,1,0);
        Vec3 side = dir.cross(up).normalize();
        up = side.cross(dir).normalize();

        // >= 1.20.5
        Matrix4f matrix = (new PoseStack()).last().pose();
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderTypes.solidMovingBlock());

        int segments = 12; // 圆截面分段数
        float radius = 0.04f; // 半径

        for (int i = 0; i < segments; i++) {
            double angle1 = 2 * Math.PI * i / segments;
            double angle2 = 2 * Math.PI * (i + 1) / segments;

            Vec3 offset1 = side.scale(Math.cos(angle1) * radius).add(up.scale(Math.sin(angle1) * radius));
            Vec3 offset2 = side.scale(Math.cos(angle2) * radius).add(up.scale(Math.sin(angle2) * radius));

            // 在 pos1Edge 和 pos2Edge 两端生成圆环顶点
            Vec3 v1 = pos1Edge.add(offset1);
            Vec3 v2 = pos1Edge.add(offset2);
            Vec3 v3 = pos2Edge.add(offset2);
            Vec3 v4 = pos2Edge.add(offset1);

            int light = LevelRenderer.getLightCoords(cart.level(), BlockPos.containing(pos1Edge));

            // 渲染四边形 v1-v2-v3-v4
            consumer.addVertex(matrix, (float)v1.x, (float)(v1.y + 0.3), (float)v1.z).setColor(0xFF252c3d).setUv(0.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(matrix, (float)v2.x, (float)(v2.y + 0.3), (float)v2.z).setColor(0xFF252c3d).setUv(1.0F, 0.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(matrix, (float)v3.x, (float)(v3.y + 0.3), (float)v3.z).setColor(0xFF252c3d).setUv(1.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
            consumer.addVertex(matrix, (float)v4.x, (float)(v4.y + 0.3), (float)v4.z).setColor(0xFF252c3d).setUv(0.0F, 1.0F).setOverlay(OverlayTexture.NO_OVERLAY).setLight(light).setNormal(0.0F, 1.0F, 0.0F);
        }
    }
}