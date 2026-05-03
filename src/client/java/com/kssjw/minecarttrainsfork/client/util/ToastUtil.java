package com.kssjw.minecarttrainsfork.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;

public class ToastUtil {

    private ToastUtil() {}

    public static void toast(String title, String description) {
        if (title == null || description == null) return;

        ToastComponent toastManager = Minecraft.getInstance().getToasts();
        SystemToast.add(
            toastManager,
            SystemToast.SystemToastId.NARRATOR_TOGGLE,
            Component.translatable(title),
            Component.translatable(description)
        );
    }
}