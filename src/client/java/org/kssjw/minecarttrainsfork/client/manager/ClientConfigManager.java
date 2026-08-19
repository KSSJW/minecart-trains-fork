package org.kssjw.minecarttrainsfork.client.manager;

import net.minecraft.core.particles.SimpleParticleType;

import org.jspecify.annotations.NonNull;

import org.kssjw.minecarttrainsfork.client.extension.config.ClientConfigValue;

import me.shedaniel.autoconfig.AutoConfig;

public class ClientConfigManager {
    
    private static ClientConfigValue config = AutoConfig.getConfigHolder(ClientConfigValue.class).getConfig();

    public static boolean isEnabledDefaultLinkParticle() {
        return ClientLoadManager.isAPIFound() ? config.enabledDefaultLinkParticle : false;
    }

    public static boolean isEnabledDefaultHeadParticle() {
        return ClientLoadManager.isAPIFound() ? config.enabledDefaultHeadParticle : true;
    }

    public static boolean isEnabledLinkLine() {
        return ClientLoadManager.isAPIFound() ? config.enabledLinkLine : true;
    }

    public static boolean isEnabledNotice() {
        return ClientLoadManager.isAPIFound() ? config.enabledNotice : true;
    }

    /* ------ */

    public static boolean isEnabledCustomLinkParticle() {
        return config.enabledCustomLinkParticle;
    }

    public static @NonNull SimpleParticleType getSelectedLinkParticle() {
        return config.selectedLinkParticle.getType();
    }

    public static int getCustomLinkParticleCycle() {
        return config.customLinkParticleCycle;
    }

    public static boolean isEnabledCustomHeadParticle() {
        return config.enabledCustomHeadParticle;
    }

    public static @NonNull SimpleParticleType getSelectedHeadParticle() {
        return config.selectedHeadParticle.getType();
    }

    public static int getCustomHeadParticleCycle() {
        return config.customHeadParticleCycle;
    }
}