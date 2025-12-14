package com.kssjw.minecarttrainsfork.client.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "minecart-trains-fork")
public class ValueConfig implements ConfigData {

    @ConfigEntry.Category("general")
    public boolean enabledLinkParticle = true;

    @ConfigEntry.Category("general")
    public boolean enabledHeadParticle = true;
}