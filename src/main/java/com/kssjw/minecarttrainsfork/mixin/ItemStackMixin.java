package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kssjw.minecarttrainsfork.util.ComponentUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    
    @Inject(method = "split", at = @At("RETURN"))
    private void onSplit(int amount, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();

        if (result.is(Items.CHAIN)) result.remove(ComponentUtil.PARENT_ID);
    }

    @Inject(method = "copyWithCount", at = @At("RETURN"))
    private void onCopyWithCount(int count, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();

        if (result.is(Items.CHAIN)) result.remove(ComponentUtil.PARENT_ID);
    }
}