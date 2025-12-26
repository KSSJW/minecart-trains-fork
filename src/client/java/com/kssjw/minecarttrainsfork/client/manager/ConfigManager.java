package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.client.config.ValueConfig;

import net.minecraft.particle.SimpleParticleType;

import me.shedaniel.autoconfig.AutoConfig;

public class ConfigManager {
    
    // 获取配置实例
    private static ValueConfig config = AutoConfig.getConfigHolder(ValueConfig.class).getConfig();

    public static boolean isEnabledDefaultLinkParticle() {
        return config.enabledDefaultLinkParticle;
    }

    public static boolean isEnabledDefaultHeadParticle() {
        return config.enabledDefaultHeadParticle;
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

    public static boolean isEnabledCustomHeadParticle() {
        return config.enabledCustomHeadParticle;
    }

    public static SimpleParticleType getSelectedHeadParticle() {
        return config.selectedHeadParticle.getType();
    }
}
