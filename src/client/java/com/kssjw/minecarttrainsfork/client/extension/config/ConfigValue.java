package com.kssjw.minecarttrainsfork.client.extension.config;

import com.kssjw.minecarttrainsfork.client.extension.config.ConfigEnum.ParticleOption;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "minecart-trains-fork")
public class ConfigValue implements ConfigData {

    //  Link Particle
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledDefaultLinkParticle = true;

    //  Head Particle
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledDefaultHeadParticle = true;

    // Link Line
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledLinkLine = true;

    // Notice
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledNotice = true;

    /* ------ */

    //  Link Particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledCustomLinkParticle = false;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.EnumHandler
    public ParticleOption selectedLinkParticle = ParticleOption.soul_fire_flame;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int customLinkParticleCycle = 40;

    //  Head Particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledCustomHeadParticle = false;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.ColorPicker
    public ParticleOption selectedHeadParticle = ParticleOption.composter;

    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int customHeadParticleCycle = 40;
}