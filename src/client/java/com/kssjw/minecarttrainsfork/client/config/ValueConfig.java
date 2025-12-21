package com.kssjw.minecarttrainsfork.client.config;

import com.kssjw.minecarttrainsfork.client.util.ListUtil.ParticleOption;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "minecart-trains-fork")
public class ValueConfig implements ConfigData {

    @ConfigEntry.Category("general")
    public boolean enabledDefaultLinkParticle = true;
    
    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int defaultLinkParticleCycle = 40;

    @ConfigEntry.Category("general")
    public boolean enabledDefaultHeadParticle = true;

    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int defaultHeadParticleCycle = 40;

    @ConfigEntry.Category("general")
    public boolean enabledNotice = true;

    /* ------ */

    @ConfigEntry.Category("advanced")
    public boolean enabledCustomLinkParticle = false;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.EnumHandler
    public ParticleOption selectedLinkParticle = ParticleOption.soul_fire_flame;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int customLinkParticleCycle = 40;

    @ConfigEntry.Category("advanced")
    public boolean enabledCustomHeadParticle = false;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.ColorPicker
    public ParticleOption selectedHeadParticle = ParticleOption.composter;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int customHeadParticleCycle = 40;
}