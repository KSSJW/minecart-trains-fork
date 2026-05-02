package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import net.minecraft.item.ItemStack;

public class ComponentUtil {

    private static final String KEY = "parent_id";

    public static void setParent(ItemStack stack, UUID uuid) {
        if (uuid == null) {
            stack.removeSubNbt(KEY);
        } else {
            stack.getOrCreateNbt().putUuid(KEY, uuid);
        }
    }

    public static void removeParent(ItemStack stack) {
        stack.removeSubNbt(KEY);
    }

    public static UUID getParent(ItemStack stack) {
        if (stack.hasNbt() && stack.getNbt().containsUuid(KEY)) {
            return stack.getNbt().getUuid(KEY);
        }
        return null;
    }
}