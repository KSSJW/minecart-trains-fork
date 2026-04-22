package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import com.kssjw.minecarttrainsfork.util.PositionUitl;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TrainManager {
    
    public static void tick(AbstractMinecartEntity entity) {
        if (!entity.getWorld().isClient()) {

            if (!PositionUitl.isWorldInitiated(entity.getWorld())) PositionUitl.setWorld(entity.getWorld());

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
                    IChainableUtil.unsetChainedParentChild((IChainableUtil)entityIChainable.getChainedParent(), entityIChainable);
                    entity.dropStack((ServerWorld) entity.getWorld(), new ItemStack(Items.CHAIN));

                    return;
                }

                if (entityIChainable.getChainedParent().isRemoved()) IChainableUtil.unsetChainedParentChild((IChainableUtil)entityIChainable.getChainedParent(), entityIChainable);
            }

            if (entityIChainable.getChainedChild() != null && entityIChainable.getChainedChild().isRemoved()) IChainableUtil.unsetChainedParentChild(entityIChainable, (IChainableUtil)entityIChainable.getChainedChild());

            for (Entity otherEntity : entity.getWorld().getOtherEntities(entity, entity.getBoundingBox().expand(0.1), entity::collidesWith)) {

                if (
                    otherEntity instanceof AbstractMinecartEntity otherCart
                    && entityIChainable.getChainedParent() != null
                    && !otherCart.equals(entityIChainable.getChainedChild())
                ) {
                    otherCart.setVelocity(entity.getVelocity());
                }
            }
        }
    }
}