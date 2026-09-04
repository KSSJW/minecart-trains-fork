package org.fuseleaf.minecarttrainsfork.extension.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "minecart-trains-fork-server")
public class ConfigValue implements ConfigData {

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean brakingAfterTrainSeparation = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.Tooltip(count = 1)
    @ConfigEntry.BoundedDiscrete(min = 3, max = 10)
    public int cartSpacing = 5;
}
