package com.kssjw.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ConfigManager;
import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void interceptOverlayMessage(Component message, boolean tinted, CallbackInfo ci) {
        String insertion = message.getStyle().getInsertion();

        if (LoadManager.isAPIFound() == true
            && ConfigManager.isEnabledNotice() == false
            && "MINECARTTRAINSFORK_OPTIONAL".equals(insertion)
        ) {
            ci.cancel();    // 拦截，不显示在 Action Bar
        }
    }
}