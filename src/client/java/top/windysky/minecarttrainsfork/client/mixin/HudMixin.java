package top.windysky.minecarttrainsfork.client.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import top.windysky.minecarttrainsfork.client.manager.ClientConfigManager;
import top.windysky.minecarttrainsfork.client.manager.ClientLoadManager;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.Hud;
import net.minecraft.network.chat.Component;

@Mixin(Hud.class)
public class HudMixin {

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