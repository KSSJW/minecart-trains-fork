package com.kssjw.minecarttrainsfork.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import com.kssjw.minecarttrainsfork.util.ModIdUtil;
import com.kssjw.minecarttrainsfork.util.UnLinkUtil;

import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class EventManager {

    private static ActionResult link(ItemStack stack, AbstractMinecartEntity cart, PlayerEntity player, Hand hand, World world, ComponentType<UUID> PARENT_ID) {
        if(player.isSneaking() && stack.isOf(Items.IRON_CHAIN)) {
            if(world instanceof ServerWorld server) {
                UUID uuid = stack.get(PARENT_ID);

                if(uuid != null && !cart.getUuid().equals(uuid)) {
                    if(server.getEntity(uuid) instanceof AbstractMinecartEntity parent) {
                        Set<IChainableUtil> train = new HashSet<>();
                        train.add(parent);

                        AbstractMinecartEntity nextChainedParent;
                        while((nextChainedParent = parent.getChainedParent()) != null && !train.contains(nextChainedParent)) {
                            train.add(nextChainedParent);
                        }

                        if(train.contains(cart) || parent.getChainedChild() != null) {
                            player.sendMessage(Text.translatable(ModIdUtil.MOD_ID + ".invalid_chaining").formatted(Formatting.RED), true);
                        } else {
                            if(cart.getChainedParent() != null) {
                                IChainableUtil.unsetChainedParentChild(cart, cart.getChainedParent());
                            }
                            IChainableUtil.setChainedParentChild(parent, cart);
                        }
                    } else {
                        stack.remove(PARENT_ID);
                    }

                    world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.NEUTRAL, 1f, 1.1f);

                    if(!player.isCreative()) {
                        stack.decrement(1);
                    }

                    stack.remove(PARENT_ID);
                } else {
                    stack.set(PARENT_ID, cart.getUuid());
                    world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.NEUTRAL, 1f, 1.1f);
                }
            }

            return ActionResult.SUCCESS;

        } else {
            return ActionResult.PASS;
        }
    }

    private static ActionResult unlink(PlayerEntity player, ItemStack stack, AbstractMinecartEntity cart, World world) {
        if (player.isSneaking() && stack.getItem() instanceof AxeItem) {
            IChainableUtil icu = (IChainableUtil)(Object)cart;
            if (!world.isClient()) {
                ServerWorld serverWorld = (ServerWorld)world;
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity)player;
                UnLinkUtil.unlinkHandle(icu, serverWorld);
                NetWorkManager.sendUnlinkData(cart, serverPlayer);  // TODO ShouldFix: 斧头交互，部分情况下粒子不能自动修正
            }

            return ActionResult.SUCCESS;

        } else {
            return ActionResult.PASS;
        }
    }

    public static ActionResult init(Entity entity, PlayerEntity player, Hand hand, World world, ComponentType<UUID> PARENT_ID) {
        if (entity instanceof AbstractMinecartEntity cart) {
            ItemStack stack = player.getStackInHand(hand);

            // 链接逻辑
            ActionResult linkResult = link(stack, cart, player, hand, world, PARENT_ID);
            if (linkResult == ActionResult.SUCCESS) {
                return ActionResult.SUCCESS;
            }

            // 解编逻辑
            ActionResult unlinkResult = unlink(player, stack, cart, world);
            if (unlinkResult == ActionResult.SUCCESS) {
                return ActionResult.SUCCESS;
            }

            // TODO Testing
            // 潜行 + 空手 → 结束编组
            if (player.isSneaking() && stack.isEmpty()) {
                if (world instanceof ServerWorld) {
                    // 清理掉铁链上的 PARENT_ID
                    stack.remove(PARENT_ID);

                    // 播放提示音
                    world.playSound(null, cart.getX(), cart.getY(), cart.getZ(),
                            SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, SoundCategory.NEUTRAL, 1f, 1f);

                    // 给玩家提示
                    player.sendMessage(Text.translatable(ModIdUtil.MOD_ID + ".chaining_cleared")
                            .formatted(Formatting.YELLOW), true);
                }
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
    }
}