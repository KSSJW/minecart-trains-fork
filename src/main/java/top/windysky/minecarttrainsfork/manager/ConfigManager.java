package top.windysky.minecarttrainsfork.manager;

import static top.windysky.minecarttrainsfork.manager.LoadManager.config;

public class ConfigManager {
    public static boolean isEnabledBrakingAfterTrainSeparation() {
        return config != null ? config.brakingAfterTrainSeparation : true;
    }

    public static double getCartSpacing() {
        return config != null ? config.cartSpacing / 10.0 : 0.5;
    }
}