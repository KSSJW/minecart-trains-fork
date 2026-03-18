package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.client.extension.config.ConfigValue;

public class LoadManager {

    private static boolean apiFound;

    public static void init() {
        try {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            apiFound = true;
        } catch (ClassNotFoundException e) {
            apiFound = false;
        }

        if (apiFound == true) me.shedaniel.autoconfig.AutoConfig.register(ConfigValue.class, me.shedaniel.autoconfig.serializer.GsonConfigSerializer::new); // 替代 AutoConfig.register(ValueConfig.class, GsonConfigSerializer::new);
        
    }

    public static boolean isAPIFound() {
        return apiFound;
    }
}