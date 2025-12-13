package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.client.config.ValueConfig;

import me.shedaniel.autoconfig.AutoConfig;

public class ConfigManager {
    
    // 获取配置实例
    private static ValueConfig config = AutoConfig.getConfigHolder(ValueConfig.class).getConfig();

    public static boolean isEnabledLinkParticle() {
        return config.enabledLinkParticle;
    }

    public static boolean isEnabledHeadParticle() {
        return config.enabledHeadParticle;
    }
}
