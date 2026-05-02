package com.kssjw.minecarttrainsfork.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.text.Text;

public class ToastUtil {

    private ToastUtil() {}

    public static void toast(String title, String description) {
        if (title == null || description == null) return;

        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        SystemToast.add(
            toastManager,
            SystemToast.Type.NARRATOR_TOGGLE,
            Text.translatable(title),
            Text.translatable(description)
        );
    }
}