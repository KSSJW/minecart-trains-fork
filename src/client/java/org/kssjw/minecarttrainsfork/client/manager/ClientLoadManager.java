package org.kssjw.minecarttrainsfork.client.manager;

import org.kssjw.minecarttrainsfork.client.extension.config.ClientConfigValue;

public class ClientLoadManager {

    private static boolean apiFound;

    public static void init() {
        try {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            apiFound = true;
        } catch (ClassNotFoundException e) {
            apiFound = false;
        }

        if (apiFound == true) me.shedaniel.autoconfig.AutoConfig.register(ClientConfigValue.class, me.shedaniel.autoconfig.serializer.GsonConfigSerializer::new); // 替代 AutoConfig.register(ValueConfig.class, GsonConfigSerializer::new);
        
    }

    public static boolean isAPIFound() {
        return apiFound;
    }
}