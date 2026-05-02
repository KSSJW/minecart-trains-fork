package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;

public class UnLinkUtil {

    private UnLinkUtil() {};

    public static void unlinkHandle(IChainableUtil icu, ServerWorld world, PlayerEntity player) {
        UUID parentUUID = icu.getParentUUID();
        UUID childUUID = icu.getChildUUID();

        // 清理父节点
        if (parentUUID != null) {

            Entity parentEntity = world.getEntity(parentUUID);

            if (parentEntity instanceof IChainableUtil parent) {
                parent.setChildUUID(null);

                NetworkManager.sendRelationshipPayload(null, parentEntity.getUuid(), (ServerPlayerEntity) player);
            }
        }

        // 清理子节点
        if (childUUID != null) {
            Entity childEntity = world.getEntity(childUUID);

            if (childEntity instanceof IChainableUtil child) {
                child.setParentUUID(null);

                NetworkManager.sendRelationshipPayload(childEntity.getUuid(), null, (ServerPlayerEntity) player);
            }
        }

        // 保存连接状态
        boolean wasLinked = parentUUID != null || childUUID != null;
        boolean hadParent = parentUUID != null;
        boolean hadChild = childUUID != null;

        // 最后清理自己
        icu.setParentUUID(null);
        icu.setChildUUID(null);

        NetworkManager.sendRelationshipPayload(((AbstractMinecartEntity) icu).getUuid(), null, (ServerPlayerEntity) player);
        NetworkManager.sendRelationshipPayload(null, ((AbstractMinecartEntity) icu).getUuid(), (ServerPlayerEntity) player);
        
        // 根据情况掉落铁链
        if (wasLinked && icu instanceof Entity entity) {
            double dx;
            double dy;
            double dz;

            if (player == null) {
                float yaw = entity.getYaw(); // 矿车朝向角度
                double offset = 0.6;         // 偏移距离，控制掉落在轨道两侧

                dx = Math.cos(Math.toRadians(yaw + 90)) * offset;
                dy = 0.8;
                dz = Math.sin(Math.toRadians(yaw + 90)) * offset;

            } else {

                // 掉落在玩家附近
                double px = player.getX();
                double pz = player.getZ();

                dx = Math.signum(px - entity.getX()) * 0.5;
                dy = 0.8;
                dz = Math.signum(pz - entity.getZ()) * 0.5;
            }

            double x = entity.getX() + dx;
            double y = entity.getY() + dy;
            double z = entity.getZ() + dz;

            if (world == null) return;

            if (hadParent) {
                ItemEntity itemEntity = new ItemEntity(world, x, y, z, new ItemStack(Items.IRON_CHAIN));
                world.spawnEntity(itemEntity);
            }
            
            if (hadChild) {
                ItemEntity itemEntity = new ItemEntity(world, x, y, z, new ItemStack(Items.IRON_CHAIN));
                world.spawnEntity(itemEntity);
            }
        }
    }
}