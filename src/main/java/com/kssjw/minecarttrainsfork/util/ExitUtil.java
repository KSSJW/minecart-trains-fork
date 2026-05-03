package com.kssjw.minecarttrainsfork.util;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ExitUtil {
    
    private ExitUtil() {}

    public static void exit(ItemStack current, ItemStack lastMainHand, Player player) {

        if (!lastMainHand.is(Items.CHAIN) && current.is(Items.CHAIN)) {
            player.displayClientMessage(Component.translatable(MinecartTrainsFork.MOD_ID + " ")
                .append(Component.translatable("message.minecart-trains-fork.chainingstarted"))
                .setStyle(Style.EMPTY.withInsertion("MINECARTTRAINSFORK_OPTIONAL"))
                .withStyle(ChatFormatting.GREEN), true);
        }

        // 如果之前是铁链，现在不是 → 清除 PARENT_ID
        if (lastMainHand.is(Items.CHAIN) && !current.is(Items.CHAIN)) {
            Inventory inv = player.getInventory();

            for (int i = 0; i < inv.getContainerSize(); i++) {
                ItemStack stack = inv.getItem(i);

                if (stack.is(Items.CHAIN)) stack.remove(ComponentUtil.PARENT_ID);
            }

            player.displayClientMessage(Component.translatable(MinecartTrainsFork.MOD_ID + " ")
                .append(Component.translatable("message.minecart-trains-fork.chainingcleared"))
                .setStyle(Style.EMPTY.withInsertion("MINECARTTRAINSFORK_OPTIONAL"))
                .withStyle(ChatFormatting.YELLOW), true);
        }
    }
}