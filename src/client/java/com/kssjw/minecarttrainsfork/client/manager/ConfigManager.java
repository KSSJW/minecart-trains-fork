package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.client.extension.config.ConfigValue;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.particle.DefaultParticleType;

public class ConfigManager {
    
    private static ConfigValue config = AutoConfig.getConfigHolder(ConfigValue.class).getConfig();

    public static boolean isEnabledDefaultLinkParticle() {
        return LoadManager.isAPIFound() ? config.enabledDefaultLinkParticle : true;
    }

    public static boolean isEnabledDefaultHeadParticle() {
        return LoadManager.isAPIFound() ? config.enabledDefaultHeadParticle : true;
    }

    public static boolean isEnabledLinkLine() {
        return LoadManager.isAPIFound() ? config.enabledLinkLine : true;
    }

    public static boolean isEnabledNotice() {
        return LoadManager.isAPIFound() ? config.enabledNotice : true;
    }

    /* ------ */

    public static boolean isEnabledCustomLinkParticle() {
        return config.enabledCustomLinkParticle;
    }

    public static DefaultParticleType getSelectedLinkParticle() {
        return config.selectedLinkParticle.getType();
    }

    public static int getCustomLinkParticleCycle() {
        return config.customLinkParticleCycle;
    }

    public static boolean isEnabledCustomHeadParticle() {
        return config.enabledCustomHeadParticle;
    }

    public static DefaultParticleType getSelectedHeadParticle() {
        return config.selectedHeadParticle.getType();
    }

    public static int getCustomHeadParticleCycle() {
        return config.customHeadParticleCycle;
    }
}