package com.kssjw.minecarttrainsfork.client.menu;

import com.kssjw.minecarttrainsfork.client.config.ValueConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfig;

public class ConfigMenu implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ValueConfig.class, parent).get();
    }
}