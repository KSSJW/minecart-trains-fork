package com.kssjw.minecarttrainsfork.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;

public class ToastUtil {

    private ToastUtil() {}

    public static void toast(String title, String description) {
        ToastManager toastManager = Minecraft.getInstance().getToastManager();
        SystemToast.add(
            toastManager,
            SystemToast.SystemToastId.NARRATOR_TOGGLE,
            Component.translatable(title),
            Component.translatable(description)
        );
    }
}