package org.kssjw.minecarttrainsfork.client.extension.config;

import org.kssjw.minecarttrainsfork.client.util.ConfigEntryScreenUtil;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ConfigEntry implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            return new ConfigEntryScreenUtil(parent);
        };
    }
}