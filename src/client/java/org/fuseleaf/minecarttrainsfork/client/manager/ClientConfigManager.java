package org.fuseleaf.minecarttrainsfork.client.manager;

import net.minecraft.core.particles.SimpleParticleType;

import static org.fuseleaf.minecarttrainsfork.client.manager.ClientLoadManager.config;

import org.jspecify.annotations.NonNull;

public class ClientConfigManager {
    public static boolean isEnabledLinkLine() {
        return config != null ? config.enabledLinkLine : true;
    }

    public static boolean isEnabledHeadParticle() {
        return config != null ? config.enabledHeadParticle : true;
    }

    public static boolean isEnabledLinkParticle() {
        return config != null ? config.enabledLinkParticle : false;
    }

    public static boolean isEnabledNotice() {
        return config != null ? config.enabledNotice : true;
    }

    /* */

    public static double getLineWidth() {
        return config.lineWidth * 0.01;
    }

    public static boolean isAlwaysRenderHeadParticle() {
        return config.alwaysRenderHeadParticle;
    }

    public static @NonNull SimpleParticleType getHeadParticleType() {
        return config.headParticleType.getType();
    }

    public static int getHeadParticleNumber() {
        return config.headParticleNumber;
    }

    public static double getHeadParticleHeight() {
        return config.headParticleHeight * 0.1;
    }

    public static int getHeadParticleCycle() {
        return config.headParticleCycle;
    }

    public static @NonNull SimpleParticleType getLinkParticleType() {
        return config.linkParticleType.getType();
    }

    public static double getLinkParticleHeight() {
        return config.linkParticleHeight * 0.1;
    }

    public static int getLinkParticleCycle() {
        return config.linkParticleCycle;
    }
}
