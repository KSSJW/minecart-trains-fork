package top.windysky.minecarttrainsfork.manager;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

import top.windysky.minecarttrainsfork.extension.config.ConfigValue;

public class LoadManager {
    public static ConfigValue config;

    private static boolean apiFound;

    public static void init() {
        try {
            Class.forName("me.shedaniel.autoconfig.AutoConfig");
            apiFound = true;
        } catch (ClassNotFoundException e) {
            apiFound = false;
        }

        if (apiFound == true) {
            AutoConfig.register(ConfigValue.class, GsonConfigSerializer::new);
            config = AutoConfig.getConfigHolder(ConfigValue.class).getConfig();
        }
    }

    public static boolean isAPIFound() {
        return apiFound;
    }
}