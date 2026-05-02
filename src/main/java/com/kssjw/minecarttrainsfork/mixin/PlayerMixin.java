package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.kssjw.minecarttrainsfork.util.ExitUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Mixin(Player.class)
public class PlayerMixin {
    
    @Unique private ItemStack lastMainHand = ItemStack.EMPTY;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        Player player = (Player)(Object)this;
        ItemStack current = player.getMainHandItem();

        // 检查是否发生变化
        if (lastMainHand != null && !ItemStack.matches(current, lastMainHand)) {
            ExitUtil.exit(current, lastMainHand, player);
            lastMainHand = current.copy();  // 更新缓存
        }
    }
}