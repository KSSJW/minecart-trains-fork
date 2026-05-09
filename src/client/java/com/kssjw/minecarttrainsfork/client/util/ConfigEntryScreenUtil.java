package com.kssjw.minecarttrainsfork.client.util;

import com.kssjw.minecarttrainsfork.client.extension.config.ClientConfigValue;
import com.kssjw.minecarttrainsfork.client.manager.ClientLoadManager;
import com.kssjw.minecarttrainsfork.extension.config.ConfigValue;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ConfigEntryScreenUtil extends Screen {

    private final Screen parent;

    public ConfigEntryScreenUtil(Screen parent) {
        super(Component.literal(""));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;

        boolean inWorld = this.minecraft.level != null && this.minecraft.player != null;
        boolean isMultiplayerWorld = inWorld && this.minecraft.getCurrentServer() != null;

        Component title = Component.translatable("screen.minecart-trains-fork.ConfigEntryScreen.title");
        this.addRenderableWidget(
            new StringWidget(
                w / 2 - this.font.width(title) / 2,
                h / 3,
                this.font.width(title),
                this.font.lineHeight,
                title,
                this.font
            )
        );
        
        MutableComponent server = Component.translatable("screen.minecart-trains-fork.ConfigEntryScreen.server");
        this.addRenderableWidget(
            Button.builder(
                isMultiplayerWorld ? server.withStyle(ChatFormatting.RED) : server.withStyle(ChatFormatting.GREEN),
                button -> {
                    if (isMultiplayerWorld) {
                        this.minecraft.setScreen(IllegalOperationScreenUtil.get(this));
                    } else {
                        this.minecraft.setScreen(AutoConfigClient.getConfigScreen(ConfigValue.class, this).get());
                    }
                }
            )
            .bounds(w / 2 - 50, h / 2, 100, 20)
            .build()
        );

        MutableComponent client = Component.translatable("screen.minecart-trains-fork.ConfigEntryScreen.client");
        this.addRenderableWidget(
            Button.builder(
                client.withStyle(ChatFormatting.GREEN),
                button -> {
                    if (ClientLoadManager.isAPIFound()) {
                        this.minecraft.setScreen(AutoConfigClient.getConfigScreen(ClientConfigValue.class, this).get());
                    } else {
                        ToastUtil.toast("toast.minecart-trains-fork.apinotfound.title", "toast.minecart-trains-fork.apinotfound.desc");
                        this.minecraft.setScreen(parent);
                    }
                }
            )
            .bounds(w / 2 - 50, h / 2 + 25, 100, 20)
            .build()
        );

        Component cancel = Component.translatable("screen.minecart-trains-fork.ConfigEntryScreen.cancel");
        this.addRenderableWidget(
            Button.builder(
                cancel,
                button -> {
                    this.minecraft.setScreen(parent);
                }
            )
            .bounds(w / 2 - 50, h / 2 + 50, 100, 20)
            .build()
        );
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(parent);
    }
}