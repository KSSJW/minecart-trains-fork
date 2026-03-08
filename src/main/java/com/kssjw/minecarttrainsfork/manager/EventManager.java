package com.kssjw.minecarttrainsfork.manager;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import com.kssjw.minecarttrainsfork.util.UnLinkUtil;

import net.minecraft.component.ComponentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
        if(
            player.isSneaking()
            && stack.isOf(Items.IRON_CHAIN)
            && world instanceof ServerWorld server
        ) {
            UUID uuid = stack.get(PARENT_ID);

            if(uuid != null && !cart.getUuid().equals(uuid)) {
                if(server.getEntity(uuid) instanceof AbstractMinecartEntity parent) {

                    IChainableUtil parentIChainable = (IChainableUtil)parent;
                    IChainableUtil cartIChainable = (IChainableUtil)cart;

                    Set<IChainableUtil> train = new HashSet<>();
                    train.add(parentIChainable);

                    AbstractMinecartEntity nextChainedParent;
                    while((nextChainedParent = (parentIChainable).getChainedParent()) != null && !train.contains((IChainableUtil)nextChainedParent)) {
                        train.add((IChainableUtil)nextChainedParent);
                    }

                    if(train.contains(cartIChainable) || (parentIChainable).getChainedChild() != null) {
                        player.sendMessage(Text.translatable(MinecartTrainsFork.MOD_ID + " ")
                            .append(Text.translatable("message.minecart-trains-fork.invalidchaining"))
                            .formatted(Formatting.RED), true);
                    } else {
                        if((cartIChainable).getChainedParent() != null) {
                            IChainableUtil.unsetChainedParentChild(cartIChainable, (IChainableUtil)((cartIChainable).getChainedParent()));
                        }
                        IChainableUtil.setChainedParentChild(parentIChainable, cartIChainable);
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
                UnLinkUtil.unlinkHandle(icu, serverWorld, player);
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
        }

        return ActionResult.PASS;
    }
}