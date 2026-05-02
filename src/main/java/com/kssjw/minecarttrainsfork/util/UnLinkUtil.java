package com.kssjw.minecarttrainsfork.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class UnLinkUtil {

    private UnLinkUtil() {};

    public static void unlinkHandle(IChainableUtil icu, ServerLevel world, Player player) {

        // 清理父节点
        if (icu.getParentUUID() != null) {
            Entity parentEntity = world.getEntity(icu.getParentUUID());

            if (parentEntity instanceof IChainableUtil parent) parent.setChildUUID(null);
        }

        // 清理子节点
        if (icu.getChildUUID() != null) {
            Entity childEntity = world.getEntity(icu.getChildUUID());

            if (childEntity instanceof IChainableUtil child) child.setParentUUID(null);
        }

        // 保存连接状态
        boolean wasLinked = icu.getParentUUID() != null || icu.getChildUUID() != null;
        boolean hadParent = icu.getParentUUID() != null;
        boolean hadChild = icu.getChildUUID() != null;

        // 最后清理自己
        icu.setParentUUID(null);
        icu.setChildUUID(null);

        // 根据情况掉落铁链
        if (wasLinked && icu instanceof Entity entity) {
            double dx;
            double dy;
            double dz;

            if (player == null) {
                float yaw = entity.getYRot(); // 矿车朝向角度
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

            if (hadParent) {
                ItemEntity itemEntity = new ItemEntity(world, x, y, z, new ItemStack(Items.IRON_CHAIN));
                world.addFreshEntity(itemEntity);
            }
            
            if (hadChild) {
                ItemEntity itemEntity = new ItemEntity(world, x, y, z, new ItemStack(Items.IRON_CHAIN));
                world.addFreshEntity(itemEntity);
            }
        }
    }
}