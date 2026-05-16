package com.kssjw.minecarttrainsfork.client.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class IllegalOperationScreenUtil {

    // 拦截提示界面
    public static ConfirmScreen get(Screen parent) {
        return new ConfirmScreen(
            (result) -> Minecraft.getInstance().setScreen(parent), // 返回上一级菜单
            Component.translatable("screen.minecart-trains-fork.IllegalOperationScreen.title"),
            Component.translatable("screen.minecart-trains-fork.IllegalOperationScreen.desc"),
            Component.translatable("screen.minecart-trains-fork.IllegalOperationScreen.yes"),
            Component.translatable("screen.minecart-trains-fork.IllegalOperationScreen.no")
        );
    }
}