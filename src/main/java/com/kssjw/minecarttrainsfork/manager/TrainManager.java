package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class TrainManager {
    
    public static void tick(AbstractMinecartEntity entity) {
        if (!entity.getEntityWorld().isClient()) {
            if(entity.getChainedParent() != null) {
                double distance = entity.getChainedParent().distanceTo(entity) - 1;

                if(distance <= 4) {
                    Vec3d directionToParent = entity.getChainedParent().getEntityPos().subtract(entity.getEntityPos()).normalize();

                    if(distance > 1) {
                        Vec3d parentVelocity = entity.getChainedParent().getVelocity();

                        if(parentVelocity.length() == 0) {
                            entity.setVelocity(directionToParent.multiply(0.05));
                        } else {
                            entity.setVelocity(directionToParent.multiply(parentVelocity.length()));
                            entity.setVelocity(entity.getVelocity().multiply(distance));
                        }
                    } else if(distance < 0.8) {
                        entity.setVelocity(directionToParent.multiply(-0.05));
                    }else {
                        entity.setVelocity(Vec3d.ZERO);
                    }
                } else {
                    IChainableUtil.unsetChainedParentChild(entity.getChainedParent(), entity);
                    entity.dropStack((ServerWorld) entity.getEntityWorld(), new ItemStack(Items.IRON_CHAIN));
                    return;
                }

                if(entity.getChainedParent().isRemoved()) {
                    IChainableUtil.unsetChainedParentChild(entity.getChainedParent(), entity);
                }
            }

            if(entity.getChainedChild() != null && entity.getChainedChild().isRemoved()) {
                IChainableUtil.unsetChainedParentChild(entity, entity.getChainedChild());
            }

            for(Entity otherEntity : entity.getEntityWorld().getOtherEntities(entity, entity.getBoundingBox().expand(0.1), entity::collidesWith)) {
                if(otherEntity instanceof AbstractMinecartEntity otherCart && entity.getChainedParent() != null && !otherCart.equals(entity.getChainedChild())) {
                    otherCart.setVelocity(entity.getVelocity());
                }
            }
        }
    }
}