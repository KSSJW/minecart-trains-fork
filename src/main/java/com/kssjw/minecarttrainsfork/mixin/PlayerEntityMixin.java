package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.kssjw.minecarttrainsfork.util.ExitUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    
    @Unique
    private ItemStack lastMainHand = ItemStack.EMPTY;

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity)(Object)this;
        ItemStack current = player.getMainHandStack();

        // 检查是否发生变化
        if (!ItemStack.areEqual(current, lastMainHand)) {

            ExitUtil.exit(current, lastMainHand, player);

            lastMainHand = current.copy();  // 更新缓存
        }
    }
}
