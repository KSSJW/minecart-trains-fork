package com.kssjw.minecarttrainsfork.client.extension.config;

import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import com.kssjw.minecarttrainsfork.client.util.ToastUtil;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.autoconfig.AutoConfigClient;

public class ConfigEntry implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            if (LoadManager.isAPIFound() == true) {
                return AutoConfigClient.getConfigScreen(ConfigValue.class, parent).get();
            } else {
                ToastUtil.toast("toast.minecart-trains-fork.apinotfound.title", "toast.minecart-trains-fork.apinotfound.desc");
                return null;
            }
        };
    }
}