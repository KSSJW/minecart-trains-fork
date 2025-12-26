package com.kssjw.minecarttrainsfork.client.manager;

import com.kssjw.minecarttrainsfork.client.config.ValueConfig;

public class LoadManager {

    private static boolean apiFound;

    public static void init() {
        try {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            apiFound = true;
        } catch (ClassNotFoundException e) {
            apiFound = false;
        }

        if (apiFound == true) {

            // 替代 AutoConfig.register(ValueConfig.class, GsonConfigSerializer::new);
            me.shedaniel.autoconfig.AutoConfig.register(ValueConfig.class, me.shedaniel.autoconfig.serializer.GsonConfigSerializer::new);
        }
    }

    public static boolean isAPIFound() {
        return apiFound;
    }
}