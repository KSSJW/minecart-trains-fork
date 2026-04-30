package com.kssjw.minecarttrainsfork.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import com.kssjw.minecarttrainsfork.util.UnLinkUtil;

public class EventManager {

    private static InteractionResult link(ItemStack stack, AbstractMinecart cart, Player player, InteractionHand hand, Level world, DataComponentType<UUID> PARENT_ID) {
        if (
            player.isShiftKeyDown()
            && stack.is(Items.IRON_CHAIN)
            && world instanceof ServerLevel server
        ) {
            UUID uuid = stack.get(PARENT_ID);

            if (uuid != null && !cart.getUUID().equals(uuid)) {
                if (server.getEntity(uuid) instanceof AbstractMinecart parent) {

                    IChainableUtil parentIChainable = (IChainableUtil)parent;
                    IChainableUtil cartIChainable = (IChainableUtil)cart;

                    Set<IChainableUtil> train = new HashSet<>();
                    train.add(parentIChainable);

                    AbstractMinecart nextChainedParent;

                    while ((nextChainedParent = (parentIChainable).getChainedParent()) != null && !train.contains((IChainableUtil)nextChainedParent)) {
                        train.add((IChainableUtil)nextChainedParent);
                    }

                    if (train.contains(cartIChainable) || (parentIChainable).getChainedChild() != null) {
                        player.sendOverlayMessage(Component.translatable(MinecartTrainsFork.MOD_ID + " ")
                            .append(Component.translatable("message.minecart-trains-fork.invalidchaining"))
                            .withStyle(ChatFormatting.RED));
                    } else {

                        if ((cartIChainable).getChainedParent() != null) IChainableUtil.unsetChainedParentChild(cartIChainable, (IChainableUtil)((cartIChainable).getChainedParent()));

                        IChainableUtil.setChainedParentChild(parentIChainable, cartIChainable);

                        NetworkManager.sendRelationshipPayload(cart.getUUID(), parent.getUUID(), (ServerPlayer) player);
                    }
                } else {
                    stack.remove(PARENT_ID);
                }

                world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.CHAIN_PLACE, SoundSource.NEUTRAL, 1f, 1.1f);

                if (!player.isCreative()) stack.shrink(1);

                stack.remove(PARENT_ID);
            } else {
                stack.set(PARENT_ID, cart.getUUID());
                world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.CHAIN_HIT, SoundSource.NEUTRAL, 1f, 1.1f);
            }
            
            return InteractionResult.SUCCESS;

        } else {
            return InteractionResult.PASS;
        }
    }

    private static InteractionResult unlink(Player player, ItemStack stack, AbstractMinecart cart, Level world) {
        if (player.isShiftKeyDown() && stack.getItem() instanceof AxeItem) {
            IChainableUtil icu = (IChainableUtil)(Object)cart;
            
            if (!world.isClientSide()) {
                ServerLevel serverWorld = (ServerLevel)world;
                UnLinkUtil.unlinkHandle(icu, serverWorld, player);
            }

            return InteractionResult.SUCCESS;

        } else {
            return InteractionResult.PASS;
        }
    }

    public static InteractionResult init(Entity entity, Player player, InteractionHand hand, Level world, DataComponentType<UUID> PARENT_ID) {
        if (entity instanceof AbstractMinecart cart && hand != null) {
            ItemStack stack = player.getItemInHand(hand);

            // 链接逻辑
            InteractionResult linkResult = link(stack, cart, player, hand, world, PARENT_ID);
            
            if (linkResult == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }

            // 解编逻辑
            InteractionResult unlinkResult = unlink(player, stack, cart, world);

            if (unlinkResult == InteractionResult.SUCCESS) {
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }
}