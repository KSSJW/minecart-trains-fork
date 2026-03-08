package com.kssjw.minecarttrainsfork.client.manager;

import net.minecraft.particle.SimpleParticleType;

import com.kssjw.minecarttrainsfork.client.extension.config.ConfigValue;

import me.shedaniel.autoconfig.AutoConfig;

public class ConfigManager {
    
    private static ConfigValue config = AutoConfig.getConfigHolder(ConfigValue.class).getConfig();

    public static boolean isEnabledDefaultLinkParticle() {
        return config.enabledDefaultLinkParticle;
    }

    public static int getDefaultLinkParticleCycle() {
        return config.defaultLinkParticleCycle;
    }

    public static boolean isEnabledDefaultHeadParticle() {
        return config.enabledDefaultHeadParticle;
    }

    public static int getDefaultHeadParticleCycle() {
        return config.defaultHeadParticleCycle;
    }

    public static boolean isEnabledNotice() {
        return config.enabledNotice;
    }

    /* ------ */

    public static boolean isEnabledCustomLinkParticle() {
        return config.enabledCustomLinkParticle;
    }

    public static SimpleParticleType getSelectedLinkParticle() {
        return config.selectedLinkParticle.getType();
    }

    public static int getCustomLinkParticleCycle() {
        return config.customLinkParticleCycle;
    }

    public static boolean isEnabledCustomHeadParticle() {
        return config.enabledCustomHeadParticle;
    }

    public static SimpleParticleType getSelectedHeadParticle() {
        return config.selectedHeadParticle.getType();
    }

    public static int getCustomHeadParticleCycle() {
        return config.customHeadParticleCycle;
    }
}