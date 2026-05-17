package com.kssjw.minecarttrainsfork.client.util;

import com.kssjw.minecarttrainsfork.client.extension.config.ClientConfigValue;
import com.kssjw.minecarttrainsfork.client.manager.ClientLoadManager;
import com.kssjw.minecarttrainsfork.extension.config.ConfigValue;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ConfigEntryScreenUtil extends Screen {

    private final Screen parent;

    public ConfigEntryScreenUtil(Screen parent) {
        super(Text.literal(""));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = this.width;
        int h = this.height;

        boolean inWorld = this.client.world != null && this.client.player != null;
        boolean isMultiplayerWorld = inWorld && this.client.getCurrentServerEntry() != null;

        Text title = Text.translatable("screen.minecart-trains-fork.ConfigEntryScreen.title");
        this.addDrawableChild(
            new TextWidget(
                w / 2 - this.textRenderer.getWidth(title) / 2,
                h / 3,
                this.textRenderer.getWidth(title),
                this.textRenderer.fontHeight,
                title,
                this.textRenderer
            )
        );
        
        MutableText server = Text.translatable("screen.minecart-trains-fork.ConfigEntryScreen.server");
        this.addDrawableChild(
            ButtonWidget.builder(
                isMultiplayerWorld ? server.formatted(Formatting.RED) : server.formatted(Formatting.GREEN),
                button -> {
                    if (isMultiplayerWorld) {
                        this.client.setScreen(IllegalOperationScreenUtil.get(this));
                    } else {
                        this.client.setScreen(AutoConfigClient.getConfigScreen(ConfigValue.class, this).get());
                    }
                }
            )
            .dimensions(w / 2 - 50, h / 2, 100, 20)
            .build()
        );

        MutableText client = Text.translatable("screen.minecart-trains-fork.ConfigEntryScreen.client");
        this.addDrawableChild(
            ButtonWidget.builder(
                client.formatted(Formatting.GREEN),
                button -> {
                    if (ClientLoadManager.isAPIFound()) {
                        this.client.setScreen(AutoConfigClient.getConfigScreen(ClientConfigValue.class, this).get());
                    } else {
                        ToastUtil.toast("toast.minecart-trains-fork.apinotfound.title", "toast.minecart-trains-fork.apinotfound.desc");
                        this.client.setScreen(parent);
                    }
                }
            )
            .dimensions(w / 2 - 50, h / 2 + 25, 100, 20)
            .build()
        );

        Text cancel = Text.translatable("screen.minecart-trains-fork.ConfigEntryScreen.cancel");
        this.addDrawableChild(
            ButtonWidget.builder(
                cancel,
                button -> {
                    this.client.setScreen(parent);
                }
            )
            .dimensions(w / 2 - 50, h / 2 + 50, 100, 20)
            .build()
        );
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }
}