package com.kssjw.minecarttrainsfork.client.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class IllegalOperationScreenUtil {

    // 拦截提示界面
    public static ConfirmScreen get(Screen parent) {
        return new ConfirmScreen(
            (result) -> MinecraftClient.getInstance().setScreen(parent), // 返回上一级菜单
            Text.translatable("screen.minecart-trains-fork.IllegalOperationScreen.title"),
            Text.translatable("screen.minecart-trains-fork.IllegalOperationScreen.desc"),
            Text.translatable("screen.minecart-trains-fork.IllegalOperationScreen.yes"),
            Text.translatable("screen.minecart-trains-fork.IllegalOperationScreen.no")
        );
    }
}