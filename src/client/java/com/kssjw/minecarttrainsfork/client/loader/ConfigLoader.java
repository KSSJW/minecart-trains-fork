package com.kssjw.minecarttrainsfork.client.loader;

import com.kssjw.minecarttrainsfork.client.config.ValueConfig;

public class ConfigLoader {

    private static boolean found;
    
    // 替代 AutoConfig.register(ValueConfig.class, GsonConfigSerializer::new);
    public static void load() {
        try {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            found = true;
        } catch (ClassNotFoundException e) {
            found = false;
        }

        if (found == true) {
            me.shedaniel.autoconfig.AutoConfig.register(ValueConfig.class, me.shedaniel.autoconfig.serializer.GsonConfigSerializer::new);
        }
    }

    public static boolean isFound() {
        return found;
    }
}
