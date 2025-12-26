package com.kssjw.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.client.manager.ConfigManager;
import com.kssjw.minecarttrainsfork.client.manager.LoadManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.text.Text;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method = "setOverlayMessage", at = @At("HEAD"), cancellable = true)
    private void interceptOverlayMessage(Text message, boolean tinted, CallbackInfo ci) {

        // 判断是否带有隐藏标记
        String insertion = message.getStyle().getInsertion();

        if (LoadManager.isAPIFound() == true
            && ConfigManager.isEnabledNotice() == false
            && "MINECARTTRAINSFORK_OPTIONAL".equals(insertion)
        ) {
            ci.cancel();    // 拦截，不显示在 Action Bar
        }
    }
}