package com.kssjw.minecarttrainsfork.client.extension.config;

import com.kssjw.minecarttrainsfork.client.util.ConfigEntryScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ConfigEntry implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return new ConfigEntryScreen(parent);
        };
    }
}