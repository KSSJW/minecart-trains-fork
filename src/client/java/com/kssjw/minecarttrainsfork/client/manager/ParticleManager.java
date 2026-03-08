package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.util.PositionUitl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;

public class ParticleManager {

    private static boolean apiFound = LoadManager.isAPIFound();

    public static void linkParticle(AbstractMinecartEntity entity) {
        defaultLinkParticle(entity);
        if (apiFound == true) {
            customLinkParticle(entity);
        }
    }

    public static void headParticle(AbstractMinecartEntity entity) {
        defaultHeadParticle(entity);
        if (apiFound == true) {
            customHeadParticle(entity);
        }
    }
    
    // 默认连接粒子
    private static void defaultLinkParticle(AbstractMinecartEntity cart) {
        if (apiFound == true && ConfigManager.isEnabledDefaultLinkParticle() == false) return;
        if (cart == null) return;                
        if (!(cart.getEntityWorld() instanceof ClientWorld world)) return;
        
        // 速度与最大数量
        final int FRAME_SKIP = ConfigManager.getDefaultLinkParticleCycle(); // 每 X 时间刻染一次
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
        if (!(cart.getEntityWorld() instanceof ClientWorld world)) return;
        
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
        if (apiFound == true && ConfigManager.isEnabledDefaultHeadParticle() == false) return;
        if (!(entity.getEntityWorld() instanceof ClientWorld world)) return;
        if (PositionUitl.getParentPos(entity.getUuid()) != null) return;

        // 速度与最大数量
        final int FRAME_SKIP_HEAD = ConfigManager.getDefaultHeadParticleCycle();    // 每 X 时间刻染一次
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
        if (!(entity.getEntityWorld() instanceof ClientWorld)) return;
        if (PositionUitl.getParentPos(entity.getUuid()) != null) return;

        ClientWorld world = (ClientWorld) entity.getEntityWorld();

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
}