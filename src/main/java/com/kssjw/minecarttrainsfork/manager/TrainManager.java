package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class TrainManager {
    
    public static void tick(AbstractMinecartEntity entity) {
        if (!entity.getWorld().isClient()) {

            IChainableUtil entityIChainable = (IChainableUtil)entity;

            if (entityIChainable.getChainedParent() != null) {
                double distance = entityIChainable.getChainedParent().distanceTo(entity) - 1;

                if (distance <= 4) {
                    Vec3d directionToParent = entityIChainable.getChainedParent().getPos().subtract(entity.getPos()).normalize();

                    if (distance > 1) {
                        Vec3d parentVelocity = entityIChainable.getChainedParent().getVelocity();

                        if (parentVelocity.length() == 0) {
                            entity.setVelocity(directionToParent.multiply(0.05));
                        } else {
                            entity.setVelocity(directionToParent.multiply(parentVelocity.length()));
                            entity.setVelocity(entity.getVelocity().multiply(distance));
                        }
                    } else if(distance < 0.8) {
                        entity.setVelocity(directionToParent.multiply(-0.05));
                    } else {
                        entity.setVelocity(Vec3d.ZERO);
                    }
                } else {
                    AbstractMinecartEntity parentCart = entityIChainable.getChainedParent();

                    IChainableUtil.unsetChainedParentChild((IChainableUtil)parentCart, entityIChainable);
                    entity.dropStack(new ItemStack(Items.CHAIN));

                    for (PlayerEntity p : entity.getEntityWorld().getPlayers()) {
                        NetworkManager.sendRelationshipPayload(entity.getUuid(), null, (ServerPlayerEntity) p);
                        NetworkManager.sendRelationshipPayload(null, parentCart.getUuid(), (ServerPlayerEntity) p);
                    }

                    return;
                }

                if (entityIChainable.getChainedParent().isRemoved()) {
                    AbstractMinecartEntity parentCart = entityIChainable.getChainedParent();

                    IChainableUtil.unsetChainedParentChild((IChainableUtil)parentCart, entityIChainable);

                    for (PlayerEntity p : entity.getEntityWorld().getPlayers()) {
                        NetworkManager.sendRelationshipPayload(entity.getUuid(), null, (ServerPlayerEntity) p);
                        NetworkManager.sendRelationshipPayload(null, parentCart.getUuid(), (ServerPlayerEntity) p);
                    }
                }
            }

            if (entityIChainable.getChainedChild() != null && entityIChainable.getChainedChild().isRemoved()) {
                AbstractMinecartEntity childCart = entityIChainable.getChainedChild();

                IChainableUtil.unsetChainedParentChild(entityIChainable, (IChainableUtil)childCart);

                for (PlayerEntity p : entity.getEntityWorld().getPlayers()) {
                        NetworkManager.sendRelationshipPayload(childCart.getUuid(), null, (ServerPlayerEntity) p);
                        NetworkManager.sendRelationshipPayload(null, childCart.getUuid(), (ServerPlayerEntity) p);
                }
            }

            for (Entity otherEntity : entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(0.1), entity::collidesWith)) {

                if (
                    otherEntity instanceof AbstractMinecartEntity otherCart
                    && entityIChainable.getChainedParent() != null
                    && entityIChainable.getChainedChild() != null
                    && entityIChainable.getChainedChild() instanceof AbstractMinecartEntity childCart
                    && !otherCart.equals(childCart)
                ) {
                    otherCart.setVelocity(entity.getVelocity());
                }
            }
        }
    }
}