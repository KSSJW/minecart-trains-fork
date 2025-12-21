package com.kssjw.minecarttrainsfork.client.manager;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.particle.ParticleTypes;

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
    private static void defaultLinkParticle(AbstractMinecartEntity entity) {
        if (apiFound == true && ConfigManager.isEnabledDefaultLinkParticle() == false) return;

        if (entity == null) return;
        AbstractMinecartEntity child = entity;
        AbstractMinecartEntity parent = child.getChainedParent();
        if (parent == null) return;
        if (!(child.getEntityWorld() instanceof ClientWorld)) return;
        ClientWorld world = (ClientWorld) child.getEntityWorld();
        
        // 速度与最大数量
        final int FRAME_SKIP = ConfigManager.getDefaultLinkParticleCycle(); // 每 X 时间刻染一次
        final int MAX_STEPS = 6;    // 每次最多生成 X 个粒子
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

        for (int i = 0; i <= steps; i++) {
            double t = (double)i / (double)steps;
            double px = sx + dx * t;
            double py = sy + dy * t;
            double pz = sz + dz * t;
            try {

                // 默认连接粒子,默认开启
                world.addParticleClient(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.0, 0.0);
                
            } catch (Throwable e) {

                // 退回到 FLAME 以防某些 mappings 签名不匹配
                try { world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                catch (Throwable ignored) { break; }
            }
        }
    }

    // 自定义连接粒子
    private static void customLinkParticle(AbstractMinecartEntity entity) {
        if (ConfigManager.isEnabledCustomLinkParticle() == false) return;

        if (entity == null) return;
        AbstractMinecartEntity child = entity;
        AbstractMinecartEntity parent = child.getChainedParent();
        if (parent == null) return;
        if (!(child.getEntityWorld() instanceof ClientWorld)) return;
        ClientWorld world = (ClientWorld) child.getEntityWorld();
        
        // 速度与最大数量
        final int FRAME_SKIP = ConfigManager.getCustomLinkParticleCycle();
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

        for (int i = 0; i <= steps; i++) {
            double t = (double)i / (double)steps;
            double px = sx + dx * t;
            double py = sy + dy * t;
            double pz = sz + dz * t;
            try {
                world.addParticleClient(ConfigManager.getSelectedLinkParticle(), px, py, pz, 0.0, 0.0, 0.0);
            } catch (Throwable e) {

                // 退回到 FLAME 以防某些 mappings 签名不匹配
                try { world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                catch (Throwable ignored) { break; }
            }
        }
    }

    // 默认头车粒子
    private static void defaultHeadParticle(AbstractMinecartEntity entity) {
        if (apiFound == true && ConfigManager.isEnabledDefaultHeadParticle() == false) return;

        if (entity == null || entity.getChainedParent() != null) return;
        if (!(entity.getEntityWorld() instanceof ClientWorld)) return;
        ClientWorld world = (ClientWorld) entity.getEntityWorld();

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

                // 默认头车粒子,默认开启
                world.addParticleClient(ParticleTypes.COMPOSTER, px, py, pz, 0.0, 0.0, 0.0);
                
            } catch (Throwable e) {

                // 退回到 FLAME 以防某些 mappings 签名不匹配
                try { world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                catch (Throwable ignored) {}
            }
        }
    }

    // 自定义头车粒子
    private static void customHeadParticle(AbstractMinecartEntity entity) {
        if (ConfigManager.isEnabledCustomHeadParticle() == false) return;

        if (entity == null || entity.getChainedParent() != null) return;
        if (!(entity.getEntityWorld() instanceof ClientWorld)) return;
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

                // 退回到 FLAME 以防某些 mappings 签名不匹配
                try { world.addParticleClient(ParticleTypes.FLAME, px, py, pz, 0.0, 0.0, 0.0); }
                catch (Throwable ignored) {}
            }
        }
    }
}