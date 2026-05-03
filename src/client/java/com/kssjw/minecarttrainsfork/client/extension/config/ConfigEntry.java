package com.kssjw.minecarttrainsfork.client.extension.config;

import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import com.kssjw.minecarttrainsfork.client.util.ToastUtil;

import me.shedaniel.autoconfig.AutoConfigClient;

import net.minecraft.client.gui.screens.Screen;

public class ConfigEntry {
    
    public static Screen getModConfigScreenFactory(Screen parent) {

        if (LoadManager.isAPIFound()) {
            return AutoConfigClient.getConfigScreen(ConfigValue.class, parent).get();
        } else {
            ToastUtil.toast("toast.minecart-trains-fork.apinotfound.title", "toast.minecart-trains-fork.apinotfound.desc");
            return null;
        }
    };
}