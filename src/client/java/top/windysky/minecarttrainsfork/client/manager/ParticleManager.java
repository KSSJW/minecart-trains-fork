package top.windysky.minecarttrainsfork.client.manager;

import java.util.UUID;

import org.joml.Matrix4f;

import top.windysky.minecarttrainsfork.MinecartTrainsFork;
import top.windysky.minecarttrainsfork.util.IChainableUtil;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

public class ParticleManager {

    public static void linkLine(AbstractMinecart cart, Vec3 camPos, PoseStack poseStack, SubmitNodeCollector submitNodeCollector) {
        double lineWidth = 0.05;

        if (ClientLoadManager.isAPIFound()) {
            if (!ClientConfigManager.isEnabledLinkLine()) {
                return;
            }

            lineWidth = ClientConfigManager.getLineWidth();
        }

        final double LINE_WIDTH = lineWidth;

        if (cart == null || !(cart.level() instanceof ClientLevel world)) {
            return;
        }

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();

        if (parentCartUuid == null) {
            return;
        }

        AbstractMinecart parentCart = (AbstractMinecart) world.getEntity(parentCartUuid);

        if (parentCart == null) {
            return;
        }

        Vec3 cartPos = cart.position();
        Vec3 parentPos = parentCart.position();

        if (camPos == null) {
            return;
        }

        // >= 1.20.5
        Vec3 pos1 = cartPos.subtract(camPos);
        Vec3 pos2 = parentPos.subtract(camPos);

        Vec3 dir = pos2.subtract(pos1).normalize(); // Direction vector

        // Edge points of two cars
        double offset = cart.getBbWidth() / 2.0;
        Vec3 pos1Edge = pos1.add(offset * dir.x, 0, offset * dir.z);
        Vec3 pos2Edge = pos2.add(-offset * dir.x, 0, -offset * dir.z);

        Vec3 up = Math.abs(dir.y) > 0.9 ? new Vec3(1,0,0) : new Vec3(0,1,0);

        double width = LINE_WIDTH;

        Vec3 side1 = dir.cross(up).normalize().scale(width);    // Level
        Vec3 side2 = dir.cross(side1).normalize().scale(width); // Vertical

        Vec3 a1 = pos1Edge.add(side1);
        Vec3 a2 = pos2Edge.add(side1);
        Vec3 a3 = pos2Edge.subtract(side1);
        Vec3 a4 = pos1Edge.subtract(side1);

        Vec3 b1 = pos1Edge.add(side2);
        Vec3 b2 = pos2Edge.add(side2);
        Vec3 b3 = pos2Edge.subtract(side2);
        Vec3 b4 = pos1Edge.subtract(side2);

        int light = LightCoordsUtil.getLightCoords(cart.level(), BlockPos.containing(cartPos.add(parentPos).scale(0.5)));

        Vec3 normal1 = side1.normalize();
        Vec3 normal2 = side2.normalize();

        if (poseStack == null) {
            return;
        }

        submitNodeCollector.submitCustomGeometry(
            poseStack,
            RenderTypes.entityCutout(Identifier.fromNamespaceAndPath(MinecartTrainsFork.MOD_ID, "textures/chain.png")),
            (pose, buffer) -> {
                Matrix4f matrix = pose.pose();

                /*
                 *   ---|    1 --------- 2   |---
                 *      |    |           |   |
                 * cart |    |           |   | cart
                 *      |    |           |   |
                 *   ---|    4 --------- 3   |---
                */

                // Level
                buffer.addVertex(matrix, (float)a1.x, (float)(a1.y + 0.3), (float)a1.z)
                    .setUv(0.0F, 0.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal1.x, (float)normal1.y, (float)normal1.z);

                buffer.addVertex(matrix, (float)a2.x, (float)(a2.y + 0.3), (float)a2.z)
                    .setUv(1.0F, 0.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal1.x, (float)normal1.y, (float)normal1.z);

                buffer.addVertex(matrix, (float)a3.x, (float)(a3.y + 0.3), (float)a3.z)
                    .setUv(1.0F, 1.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal1.x, (float)normal1.y, (float)normal1.z);

                buffer.addVertex(matrix, (float)a4.x, (float)(a4.y + 0.3), (float)a4.z)
                    .setUv(0.0F, 1.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal1.x, (float)normal1.y, (float)normal1.z);

                // Vertical
                buffer.addVertex(matrix, (float)b1.x, (float)(b1.y + 0.3), (float)b1.z)
                    .setUv(0.0F, 0.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal2.x, (float)normal2.y, (float)normal2.z);

                buffer.addVertex(matrix, (float)b2.x, (float)(b2.y + 0.3), (float)b2.z)
                    .setUv(1.0F, 0.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal2.x, (float)normal2.y, (float)normal2.z);

                buffer.addVertex(matrix, (float)b3.x, (float)(b3.y + 0.3), (float)b3.z)
                    .setUv(1.0F, 1.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal2.x, (float)normal2.y, (float)normal2.z);

                buffer.addVertex(matrix, (float)b4.x, (float)(b4.y + 0.3), (float)b4.z)
                    .setUv(0.0F, 1.0F)
                    .setColor(255, 255, 255, 255)
                    .setOverlay(OverlayTexture.NO_OVERLAY)
                    .setLight(light)
                    .setNormal((float)normal2.x, (float)normal2.y, (float)normal2.z);
            }
        );
    }

    public static void headParticle(AbstractMinecart cart) {
        int frameSkip = 40;
        int maxSteps = 6;
        double particleHeight = 0.8;
        SimpleParticleType particleType = ParticleTypes.COMPOSTER;

        if (ClientLoadManager.isAPIFound()) {
            if (!ClientConfigManager.isEnabledHeadParticle()) {
                return;
            }

            frameSkip = ClientConfigManager.getHeadParticleCycle();
            maxSteps = ClientConfigManager.getHeadParticleNumber();
            particleHeight = ClientConfigManager.getHeadParticleHeight();
            particleType = ClientConfigManager.getHeadParticleType();
        }

        if (!(cart.level() instanceof ClientLevel world)) {
            return;
        }

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();

        if (parentCartUuid != null && world.getEntity(parentCartUuid) != null) {
            return;
        }

        final int FRAME_SKIP = frameSkip;
        final int MAX_STEPS = maxSteps;
        final double PARTICLE_HEIGHT = particleHeight;
        final SimpleParticleType PARTICLE_TYPE = particleType;
        long ticks = Minecraft.getInstance().gui.hud.getGuiTicks();

        if (ticks % FRAME_SKIP != 0) {
            return;
        }

        double baseX = cart.getX();
        double baseY = cart.getY() + PARTICLE_HEIGHT;
        double baseZ = cart.getZ();

        for (int i = 0; i < MAX_STEPS; i++) {
            double offsetX = (Math.random() - 0.5) * 0.4;
            double offsetY = (Math.random() - 0.5) * 0.2;
            double offsetZ = (Math.random() - 0.5) * 0.4;
            double px = baseX + offsetX;
            double py = baseY + offsetY;
            double pz = baseZ + offsetZ;

            world.addParticle(PARTICLE_TYPE, px, py, pz, 0.0, 0.0, 0.0);
        }
    }

    public static void linkParticle(AbstractMinecart cart) {
        int frameSkip = 40;
        int maxSteps = 6;
        double particleHeight = 0.6;
        SimpleParticleType particleType = ParticleTypes.SOUL_FIRE_FLAME;

        if (ClientLoadManager.isAPIFound()) {
            if (!ClientConfigManager.isEnabledLinkParticle()) {
                return;
            }

            frameSkip = ClientConfigManager.getLinkParticleCycle();
            particleType = ClientConfigManager.getLinkParticleType();
            particleHeight = ClientConfigManager.getLinkParticleHeight();
        }

        if (cart == null || !(cart.level() instanceof ClientLevel world)) {
            return;
        }

        final int FRAME_SKIP = frameSkip;
        final int MAX_STEPS = maxSteps;
        final double PARTICLE_HEIGHT = particleHeight;
        final SimpleParticleType PARTICLE_TYPE = particleType;
        long ticks = Minecraft.getInstance().gui.hud.getGuiTicks();

        if (ticks % FRAME_SKIP != 0) {
            return;
        }

        UUID parentCartUuid = ((IChainableUtil) cart).getParentUUID();

        if (parentCartUuid == null) {
            return;
        }

        AbstractMinecart parentCart = (AbstractMinecart) world.getEntity(parentCartUuid);

        if (parentCart == null) {
            return;
        }

        Vec3 parentPos = parentCart.position();

        double sx = parentPos.x;
        double sy = parentPos.y + PARTICLE_HEIGHT;
        double sz = parentPos.z;
        double ex = cart.getX();
        double ey = cart.getY() + PARTICLE_HEIGHT;
        double ez = cart.getZ();

        double dx = ex - sx, dy = ey - sy, dz = ez - sz;
        double distSq = dx*dx + dy*dy + dz*dz;

        if (distSq < 1e-6) {
            return;
        }

        double dist = Math.sqrt(distSq);

        double spacing = Math.max(0.25, dist / MAX_STEPS);
        int steps = Math.min(MAX_STEPS, Math.max(1, (int)Math.ceil(dist / spacing)));

        for (int i = 0; i <= steps; i++) {
            double t = (double)i / (double)steps;
            double px = sx + dx * t;
            double py = sy + dy * t;
            double pz = sz + dz * t;

            world.addParticle(PARTICLE_TYPE, px, py, pz, 0.0, 0.0, 0.0);
        }
    }
}
