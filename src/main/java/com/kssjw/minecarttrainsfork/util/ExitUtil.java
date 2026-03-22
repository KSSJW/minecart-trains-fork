package com.kssjw.minecarttrainsfork.util;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ExitUtil {
    
    private ExitUtil() {}

    public static void exit(ItemStack current, ItemStack lastMainHand, PlayerEntity player) {

        if (!lastMainHand.isOf(Items.CHAIN) && current.isOf(Items.CHAIN)) {
            player.sendMessage(Text.translatable(MinecartTrainsFork.MOD_ID + " ")
                .append(Text.translatable("message.minecart-trains-fork.chainingstarted"))
                .setStyle(Style.EMPTY.withInsertion("MINECARTTRAINSFORK_OPTIONAL"))
                .formatted(Formatting.GREEN), true);
        }

        // 如果之前是铁链，现在不是 → 清除 PARENT_ID
        if (lastMainHand.isOf(Items.CHAIN) && !current.isOf(Items.CHAIN)) {
            PlayerInventory inv = player.getInventory();

            for (int i = 0; i < inv.size(); i++) {
                ItemStack stack = inv.getStack(i);
                NbtCompound nbt = stack.getOrCreateNbt();

                if (stack.isOf(Items.CHAIN) && nbt != null && nbt.contains(ComponentUtil.PARENT_ID)) nbt.remove(ComponentUtil.PARENT_ID);
            }

            player.sendMessage(Text.translatable(MinecartTrainsFork.MOD_ID + " ")
                .append(Text.translatable("message.minecart-trains-fork.chainingcleared"))
                .setStyle(Style.EMPTY.withInsertion("MINECARTTRAINSFORK_OPTIONAL"))
                .formatted(Formatting.YELLOW), true);
        }
    }
}