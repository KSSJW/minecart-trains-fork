package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.util.IChainableUtil;
import com.kssjw.minecarttrainsfork.util.PositionUitl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class TrainManager {
    
    public static void tick(AbstractMinecart entity) {
        if (!entity.level().isClientSide()) {

            if (!PositionUitl.isWorldInitiated(entity.level())) PositionUitl.setWorld(entity.level());

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
                    IChainableUtil.unsetChainedParentChild((IChainableUtil)entityIChainable.getChainedParent(), entityIChainable);
                    entity.spawnAtLocation((ServerLevel) entity.level(), new ItemStack(Items.IRON_CHAIN));

                    return;
                }

                if (entityIChainable.getChainedParent().isRemoved()) IChainableUtil.unsetChainedParentChild((IChainableUtil)entityIChainable.getChainedParent(), entityIChainable);
            }

            if (entityIChainable.getChainedChild() != null && entityIChainable.getChainedChild().isRemoved()) IChainableUtil.unsetChainedParentChild(entityIChainable, (IChainableUtil)entityIChainable.getChainedChild());

            for (Entity otherEntity : entity.level().getEntities(entity, entity.getBoundingBox().inflate(0.1), entity::canCollideWith)) {

                if (
                    otherEntity instanceof AbstractMinecart otherCart
                    && entityIChainable.getChainedParent() != null
                    && !otherCart.equals(entityIChainable.getChainedChild())
                ) {
                    otherCart.setDeltaMovement(entity.getDeltaMovement());
                }
            }
        }
    }
}