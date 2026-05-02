package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class TrainManager {
    
    public static void tick(AbstractMinecart entity) {
        if (!entity.level().isClientSide()) {

            IChainableUtil entityIChainable = (IChainableUtil)entity;

            if (entityIChainable.getChainedParent() != null) {
                double distance = entityIChainable.getChainedParent().distanceTo(entity) - 1;

                if (distance <= 4) {
                    Vec3 directionToParent = entityIChainable.getChainedParent().position().subtract(entity.position()).normalize();

                    if (distance > 1) {
                        Vec3 parentVelocity = entityIChainable.getChainedParent().getDeltaMovement();

                        if (parentVelocity.length() == 0) {
                            entity.setDeltaMovement(directionToParent.scale(0.05));
                        } else {
                            entity.setDeltaMovement(directionToParent.scale(parentVelocity.length()));
                            entity.setDeltaMovement(entity.getDeltaMovement().scale(distance));
                        }
                    } else if(distance < 0.8) {
                        entity.setDeltaMovement(directionToParent.scale(-0.05));
                    } else {
                        entity.setDeltaMovement(Vec3.ZERO);
                    }
                } else {
                    AbstractMinecart parentCart = entityIChainable.getChainedParent();

                    IChainableUtil.unsetChainedParentChild((IChainableUtil)parentCart, entityIChainable);
                    entity.spawnAtLocation((ServerLevel) entity.level(), new ItemStack(Items.IRON_CHAIN));

                    for (Player p : entity.level().players()) {
                        NetworkManager.sendRelationshipPayload(entity.getUUID(), null, (ServerPlayer) p);
                        NetworkManager.sendRelationshipPayload(null, parentCart.getUUID(), (ServerPlayer) p);
                    }

                    return;
                }

                if (entityIChainable.getChainedParent().isRemoved()) {
                    AbstractMinecart parentCart = entityIChainable.getChainedParent();

                    IChainableUtil.unsetChainedParentChild((IChainableUtil)parentCart, entityIChainable);

                    for (Player p : entity.level().players()) {
                        NetworkManager.sendRelationshipPayload(entity.getUUID(), null, (ServerPlayer) p);
                        NetworkManager.sendRelationshipPayload(null, parentCart.getUUID(), (ServerPlayer) p);
                    }
                }
            }

            if (entityIChainable.getChainedChild() != null && entityIChainable.getChainedChild().isRemoved()) {
                AbstractMinecart childCart = entityIChainable.getChainedChild();

                IChainableUtil.unsetChainedParentChild(entityIChainable, (IChainableUtil)childCart);

                for (Player p : entity.level().players()) {
                        NetworkManager.sendRelationshipPayload(childCart.getUUID(), null, (ServerPlayer) p);
                        NetworkManager.sendRelationshipPayload(null, childCart.getUUID(), (ServerPlayer) p);
                }
            }

            for (Entity otherEntity : entity.level().getEntities(entity, entity.getBoundingBox().inflate(0.1), entity::canCollideWith)) {

                if (
                    otherEntity instanceof AbstractMinecart otherCart
                    && entityIChainable.getChainedParent() != null
                    && entityIChainable.getChainedChild() != null
                    && entityIChainable.getChainedChild() instanceof AbstractMinecart childCart
                    && !otherCart.equals(childCart)
                ) {
                    otherCart.setDeltaMovement(entity.getDeltaMovement());
                }
            }
        }
    }
}