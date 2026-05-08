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

public class ConfigEntryScreen extends Screen {

    private final Screen parent;

    public ConfigEntryScreen(Screen parent) {
        super(Component.literal(""));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;

        boolean inWorld = this.minecraft.level != null && this.minecraft.player != null;
        boolean isMultiplayerWorld = inWorld && this.minecraft.getCurrentServer() != null;

        String title = "Minecart Trains Fork Configuration";
        this.addRenderableWidget(
            new StringWidget(
                w / 2 - this.font.width(Component.literal(title)) / 2,
                h / 3,
                this.font.width(Component.literal(title)),
                this.font.lineHeight,
                Component.literal(title),
                this.font
            )
        );
        
        String server = "Server";
        this.addRenderableWidget(
            Button.builder(
                isMultiplayerWorld ? Component.literal(server).withStyle(ChatFormatting.RED) : Component.literal(server).withStyle(ChatFormatting.GREEN),
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

        String client = "Client";
        this.addRenderableWidget(
            Button.builder(
                Component.literal(client).withStyle(ChatFormatting.GREEN),
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

        String cancel = "Cancel";
        this.addRenderableWidget(
            Button.builder(
                Component.literal(cancel),
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