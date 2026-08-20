package top.windysky.minecarttrainsfork.client.manager;

import net.minecraft.core.particles.SimpleParticleType;

import org.jspecify.annotations.NonNull;

import top.windysky.minecarttrainsfork.client.extension.config.ClientConfigValue;

import me.shedaniel.autoconfig.AutoConfig;

public class ClientConfigManager {
    
    private static ClientConfigValue config = AutoConfig.getConfigHolder(ClientConfigValue.class).getConfig();

    public static boolean isEnabledLinkLine() {
        return ClientLoadManager.isAPIFound() ? config.enabledLinkLine : true;
    }

    public static boolean isEnabledHeadParticle() {
        return ClientLoadManager.isAPIFound() ? config.enabledHeadParticle : true;
    }

    public static boolean isEnabledLinkParticle() {
        return ClientLoadManager.isAPIFound() ? config.enabledLinkParticle : false;
    }

    public static boolean isEnabledNotice() {
        return ClientLoadManager.isAPIFound() ? config.enabledNotice : true;
    }

    /* */

    public static @NonNull SimpleParticleType getLinkParticleType() {
        return config.linkParticleType.getType();
    }

    public static double getLinkParticleHeight() {
        return config.linkParticleHeight * 0.1;
    }

    public static int getLinkParticleCycle() {
        return config.linkParticleCycle;
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
}