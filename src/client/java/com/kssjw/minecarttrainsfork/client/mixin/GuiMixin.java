package com.kssjw.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ClientConfigManager;
import com.kssjw.minecarttrainsfork.client.manager.ClientLoadManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
public class GuiMixin {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void injectSetOverlayMessage(Component message, boolean tinted, CallbackInfo ci) {
        String insertion = message.getStyle().getInsertion();

        if (ClientLoadManager.isAPIFound() == true
            && ClientConfigManager.isEnabledNotice() == false
            && "MINECARTTRAINSFORK_OPTIONAL".equals(insertion)
        ) {
            ci.cancel();    // 拦截，不显示在 Action Bar
        }
    }
}