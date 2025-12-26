package com.kssjw.minecarttrainsfork.client.util;

import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;

public class ListUtil {

    private ListUtil() {}

    public enum ParticleOption {
        angry_villager(ParticleTypes.ANGRY_VILLAGER),
        ash(ParticleTypes.ASH),
        bubble(ParticleTypes.BUBBLE),
        bubble_column_up(ParticleTypes.BUBBLE_COLUMN_UP),
        bubble_pop(ParticleTypes.BUBBLE_POP),
        campfire_cosy_smoke(ParticleTypes.CAMPFIRE_COSY_SMOKE),
        campfire_signal_smoke(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE),
        cherry_leaves(ParticleTypes.CHERRY_LEAVES),
        cloud(ParticleTypes.CLOUD),
        composter(ParticleTypes.COMPOSTER),
        crimson_spore(ParticleTypes.CRIMSON_SPORE),
        crit(ParticleTypes.CRIT),
        current_down(ParticleTypes.CURRENT_DOWN),
        damage_indicator(ParticleTypes.DAMAGE_INDICATOR),
        dolphin(ParticleTypes.DOLPHIN),
        dripping_dripstone_lava(ParticleTypes.DRIPPING_DRIPSTONE_LAVA),
        dripping_dripstone_water(ParticleTypes.DRIPPING_DRIPSTONE_WATER),
        dripping_honey(ParticleTypes.DRIPPING_HONEY),
        dripping_lava(ParticleTypes.DRIPPING_LAVA),
        dripping_obsidian_tear(ParticleTypes.DRIPPING_OBSIDIAN_TEAR),
        dripping_water(ParticleTypes.DRIPPING_WATER),
        dust_plume(ParticleTypes.DUST_PLUME),
        egg_crack(ParticleTypes.EGG_CRACK),
        elder_guardian(ParticleTypes.ELDER_GUARDIAN),
        electric_spark(ParticleTypes.ELECTRIC_SPARK),
        enchant(ParticleTypes.ENCHANT),
        enchanted_hit(ParticleTypes.ENCHANTED_HIT),
        end_rod(ParticleTypes.END_ROD),
        explosion(ParticleTypes.EXPLOSION),
        explosion_emitter(ParticleTypes.EXPLOSION_EMITTER),
        falling_dripstone_lava(ParticleTypes.FALLING_DRIPSTONE_LAVA),
        falling_dripstone_water(ParticleTypes.FALLING_DRIPSTONE_WATER),
        falling_honey(ParticleTypes.FALLING_HONEY),
        falling_lava(ParticleTypes.FALLING_LAVA),
        falling_nectar(ParticleTypes.FALLING_NECTAR),
        falling_obsidian_tear(ParticleTypes.FALLING_OBSIDIAN_TEAR),
        falling_spore_blossom(ParticleTypes.FALLING_SPORE_BLOSSOM),
        falling_water(ParticleTypes.FALLING_WATER),
        firefly(ParticleTypes.FIREFLY),
        firework(ParticleTypes.FIREWORK),
        fishing(ParticleTypes.FISHING),
        flame(ParticleTypes.FLAME),
        glow(ParticleTypes.GLOW),
        glow_squid_ink(ParticleTypes.GLOW_SQUID_INK),
        gust(ParticleTypes.GUST),
        gust_emitter_large(ParticleTypes.GUST_EMITTER_LARGE),
        gust_emitter_small(ParticleTypes.GUST_EMITTER_SMALL),
        happy_villager(ParticleTypes.HAPPY_VILLAGER),
        heart(ParticleTypes.HEART),
        infested(ParticleTypes.INFESTED),
        item_cobweb(ParticleTypes.ITEM_COBWEB),
        item_slime(ParticleTypes.ITEM_SLIME),
        item_snowball(ParticleTypes.ITEM_SNOWBALL),
        landing_honey(ParticleTypes.LANDING_HONEY),
        landing_lava(ParticleTypes.LANDING_LAVA),
        landing_obsidian_tear(ParticleTypes.LANDING_OBSIDIAN_TEAR),
        large_smoke(ParticleTypes.LARGE_SMOKE),
        lava(ParticleTypes.LAVA),
        mycelium(ParticleTypes.MYCELIUM),
        nautilus(ParticleTypes.NAUTILUS),
        note(ParticleTypes.NOTE),
        ominous_spawning(ParticleTypes.OMINOUS_SPAWNING),
        pale_oak_leaves(ParticleTypes.PALE_OAK_LEAVES),
        poof(ParticleTypes.POOF),
        portal(ParticleTypes.PORTAL),
        raid_omen(ParticleTypes.RAID_OMEN),
        rain(ParticleTypes.RAIN),
        reverse_portal(ParticleTypes.REVERSE_PORTAL),
        scrape(ParticleTypes.SCRAPE),
        sculk_charge_pop(ParticleTypes.SCULK_CHARGE_POP),
        sculk_soul(ParticleTypes.SCULK_SOUL),
        small_flame(ParticleTypes.SMALL_FLAME),
        small_gust(ParticleTypes.SMALL_GUST),
        smoke(ParticleTypes.SMOKE),
        sneeze(ParticleTypes.SNEEZE),
        snowflake(ParticleTypes.SNOWFLAKE),
        sonic_boom(ParticleTypes.SONIC_BOOM),
        soul(ParticleTypes.SOUL),
        soul_fire_flame(ParticleTypes.SOUL_FIRE_FLAME),
        spit(ParticleTypes.SPIT),
        splash(ParticleTypes.SPLASH),
        spore_blossom_air(ParticleTypes.SPORE_BLOSSOM_AIR),
        squid_ink(ParticleTypes.SQUID_INK),
        sweep_attack(ParticleTypes.SWEEP_ATTACK),
        totem_of_undying(ParticleTypes.TOTEM_OF_UNDYING),
        trial_omen(ParticleTypes.TRIAL_OMEN),
        trial_spawner_detection(ParticleTypes.TRIAL_SPAWNER_DETECTION),
        trial_spawner_detection_ominous(ParticleTypes.TRIAL_SPAWNER_DETECTION_OMINOUS),
        underwater(ParticleTypes.UNDERWATER),
        vault_connection(ParticleTypes.VAULT_CONNECTION),
        warped_spore(ParticleTypes.WARPED_SPORE),
        wax_off(ParticleTypes.WAX_OFF),
        wax_on(ParticleTypes.WAX_ON),
        white_ash(ParticleTypes.WHITE_ASH),
        white_smoke(ParticleTypes.WHITE_SMOKE),
        witch(ParticleTypes.WITCH);

        private final SimpleParticleType type;

        ParticleOption(SimpleParticleType type) {
          this.type = type;
        }

        public SimpleParticleType getType() {
          return type;
        }
    }
}