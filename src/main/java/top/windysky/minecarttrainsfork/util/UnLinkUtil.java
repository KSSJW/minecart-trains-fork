package top.windysky.minecarttrainsfork.util;

import java.util.UUID;

import top.windysky.minecarttrainsfork.manager.NetworkManager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class UnLinkUtil {

    private UnLinkUtil() {}

    public static void unlinkHandle(IChainableUtil icu, ServerLevel world) {
        UUID parentUUID = icu.getParentUUID();
        UUID childUUID = icu.getChildUUID();

        // 清理父节点
        if (parentUUID != null) {

            Entity parentEntity = world.getEntity(parentUUID);

            if (parentEntity instanceof IChainableUtil parent) {
                parent.setChildUUID(null);

                NetworkManager.sendRelationshipPayload(null, parentEntity.getUUID(), world);
            }
        }

        // 清理子节点
        if (childUUID != null) {
            Entity childEntity = world.getEntity(childUUID);

            if (childEntity instanceof IChainableUtil child) {
                child.setParentUUID(null);

                NetworkManager.sendRelationshipPayload(childEntity.getUUID(), null, world);
            }
        }

        // 保存连接状态
        boolean wasLinked = parentUUID != null || childUUID != null;
        boolean hadParent = parentUUID != null;
        boolean hadChild = childUUID != null;

        // 最后清理自己
        icu.setParentUUID(null);
        icu.setChildUUID(null);

        NetworkManager.sendRelationshipPayload(((AbstractMinecart) icu).getUUID(), null, world);
        NetworkManager.sendRelationshipPayload(null, ((AbstractMinecart) icu).getUUID(), world);

        // 根据情况掉落铁链
        if (wasLinked && icu instanceof Entity entity) {
            double dx;
            double dy;
            double dz;

            float yaw = entity.getYRot(); // 矿车朝向角度
            double offset = 0.6;         // 偏移距离，控制掉落在轨道两侧

            dx = Math.cos(Math.toRadians(yaw + 90)) * offset;
            dy = 0.8;
            dz = Math.sin(Math.toRadians(yaw + 90)) * offset;

            double x = entity.getX() + dx;
            double y = entity.getY() + dy;
            double z = entity.getZ() + dz;

            if (world == null) {
                return;
            }

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
