package com.kssjw.minecarttrainsfork.util;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.packet.s2c.play.EntitiesDestroyS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class ResendUtil {

    private static void forceResendEntity(AbstractMinecartEntity cart, ServerPlayerEntity player) {
        
        // 让客户端删除实体
        player.networkHandler.sendPacket(
            new EntitiesDestroyS2CPacket(cart.getId())
        );

        // 手动构造 Spawn 包（不依赖 EntityTrackerEntry）
        EntitySpawnS2CPacket spawnPacket = new EntitySpawnS2CPacket(
            cart.getId(),
            cart.getUuid(),
            cart.getX(),
            cart.getY(),
            cart.getZ(),
            cart.getPitch(),
            cart.getYaw(),
            cart.getType(),
            0,  // entityData：矿车一般是 0
            cart.getVelocity(),
            cart.getHeadYaw()
        );

        player.networkHandler.sendPacket(spawnPacket);
    }

    private static void forceResendEntityToAll(AbstractMinecartEntity cart, ServerWorld world) {
        for (ServerPlayerEntity player : world.getPlayers()) {
            forceResendEntity(cart, player);
        }
    }

    // 刷新断链的矿车
    public static void forceResendUnlinkedMinecarts(ServerWorld world) {
        for (var entity : world.iterateEntities()) {
            if (entity instanceof AbstractMinecartEntity cart) {
                if (cart instanceof IChainableUtil chainable) {

                    // 判断是否为头车
                    if (chainable.getParentClientID() == -1 || chainable.getParentUUID() == null) {
                        LogUtil.print("HEAD CART");
                        forceResendEntityToAll(cart, world);
                        continue;
                    }

                    // 判断是否断链
                    if (chainable.getChildClientID() == -1) {
                        if (chainable.getParentUUID() != null) continue;
                        forceResendEntityToAll(cart, world);
                    }
                }
            }
        }
    }
}
