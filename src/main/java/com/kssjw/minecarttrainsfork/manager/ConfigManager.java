package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.extension.config.ConfigValue;

import me.shedaniel.autoconfig.AutoConfig;

public class ConfigManager {

    private static ConfigValue config = AutoConfig.getConfigHolder(ConfigValue.class).getConfig();

    public static boolean isEnabledBrakingAfterTrainSeparation() {
        return LoadManager.isAPIFound() ? config.brakingAfterTrainSeparation : true;
    }

    public static double getCartSpacing() {
        return LoadManager.isAPIFound() ? config.cartSpacing / 10.0 : 0.5;
    }
}