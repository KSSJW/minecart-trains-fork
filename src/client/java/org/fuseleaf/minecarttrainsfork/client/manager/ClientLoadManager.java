package org.fuseleaf.minecarttrainsfork.client.manager;

import org.fuseleaf.minecarttrainsfork.client.extension.config.ClientConfigValue;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class ClientLoadManager {
    public static ClientConfigValue config;

    private static boolean apiFound;

    public static void init() {
        try {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            apiFound = true;
        } catch (ClassNotFoundException e) {
            apiFound = false;
        }

        if (apiFound == true) {
            AutoConfig.register(ClientConfigValue.class, GsonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(ClientConfigValue.class).getConfig();
        }
    }

    public static boolean isAPIFound() {
        return apiFound;
    }
}
