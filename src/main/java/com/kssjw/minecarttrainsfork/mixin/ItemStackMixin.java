package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.kssjw.minecarttrainsfork.util.ComponentUtil;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    
    @Inject(method = "split", at = @At("RETURN"))
    private void onSplit(int amount, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        NbtCompound nbt = result.getOrCreateNbt();

        if (result.isOf(Items.CHAIN) && nbt != null && nbt.contains(ComponentUtil.PARENT_ID)) nbt.remove(ComponentUtil.PARENT_ID);
    }

    @Inject(method = "copyWithCount", at = @At("RETURN"))
    private void onCopyWithCount(int count, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack result = cir.getReturnValue();
        NbtCompound nbt = result.getOrCreateNbt();

        if (result.isOf(Items.CHAIN) && nbt != null && nbt.contains(ComponentUtil.PARENT_ID)) nbt.remove(ComponentUtil.PARENT_ID);
    }
}