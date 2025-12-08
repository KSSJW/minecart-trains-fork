package com.kssjw.minecarttrainsfork.util;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class UnLinkUtil {

    private UnLinkUtil() {};

    public static void unlinkHandle(IChainableUtil icu, ServerWorld world) {

        // 清理父节点
        if (icu.getParentUUID() != null) {
            Entity parentEntity = world.getEntity(icu.getParentUUID());
            if (parentEntity instanceof IChainableUtil parent) {
                parent.setChildUUID(null);
                parent.setChildClientID(0);
            }
        }

        // 清理子节点
        if (icu.getChildUUID() != null) {
            Entity childEntity = world.getEntity(icu.getChildUUID());
            if (childEntity instanceof IChainableUtil child) {
                child.setParentUUID(null);
                child.setParentClientID(0);
            }
        }

        // 保存是否被连接
        boolean wasLinked = icu.getParentUUID() != null || icu.getChildUUID() != null;

        // 最后清理自己
        icu.setParentUUID(null);
        icu.setChildUUID(null);
        icu.setParentClientID(0);
        icu.setChildClientID(0);

        // 掉落铁链
        // TODO ShouldFix: 掉落的铁链比较特殊
        if (wasLinked && icu instanceof Entity entity) {
            ItemStack chain = new ItemStack(Items.IRON_CHAIN, 1);
            entity.dropStack(world, chain);
        }
    }
}