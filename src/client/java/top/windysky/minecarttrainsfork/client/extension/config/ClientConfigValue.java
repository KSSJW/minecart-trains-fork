package top.windysky.minecarttrainsfork.client.extension.config;

import top.windysky.minecarttrainsfork.client.extension.config.ConfigEnum.ParticleOption;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "minecart-trains-fork-client")
public class ClientConfigValue implements ConfigData {

    /* General */

    // Link line
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledLinkLine = true;

    // Head particle
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledHeadParticle = true;

    // Link particle
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledLinkParticle = false;

    // Notice
    @ConfigEntry.Category("general")
    @ConfigEntry.Gui.PrefixText
    public boolean enabledNotice = true;

    /* Advanced */

    // Width of Link Line
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.BoundedDiscrete(min = 1, max = 10)
    public int lineWidth = 5;

    // Head particle type
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.ColorPicker
    public ParticleOption headParticleType = ParticleOption.composter;

    // Number of head particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 32)
    public int headParticleNumber = 6;

    // Height of head particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    public int headParticleHeight = 8;

    // Cycle of head particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int headParticleCycle = 40;

    // Link particle type
    @ConfigEntry.Category("advanced")
    @ConfigEntry.Gui.PrefixText
    @ConfigEntry.Gui.EnumHandler
    public ParticleOption linkParticleType = ParticleOption.soul_fire_flame;

    // Height of link particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 20)
    public int linkParticleHeight = 6;

    // Cycle of link particle
    @ConfigEntry.Category("advanced")
    @ConfigEntry.BoundedDiscrete(min = 1, max = 100)
    public int linkParticleCycle = 40;
}