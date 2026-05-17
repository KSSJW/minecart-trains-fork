package com.kssjw.minecarttrainsfork.client.extension.config;

import com.kssjw.minecarttrainsfork.client.util.ConfigEntryScreenUtil;
import net.minecraft.client.gui.screens.Screen;

public class ConfigEntry {
    
    public static Screen getModConfigScreenFactory(Screen parent) {
        return new ConfigEntryScreenUtil(parent);
    };
}