package com.kssjw.minecarttrainsfork.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.kssjw.minecarttrainsfork.util.ComponentUtil;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {

    @Inject(method = "onPlayerCollision", at = @At("HEAD"))
    private void onPickup(PlayerEntity player, CallbackInfo ci) {
        ItemEntity self = (ItemEntity)(Object)this;
        ItemStack stack = self.getStack();
        NbtCompound nbt = stack.getOrCreateNbt();
        
        if (stack.isOf(Items.CHAIN) && nbt != null && nbt.contains(ComponentUtil.PARENT_ID)) nbt.remove(ComponentUtil.PARENT_ID);
    }
}