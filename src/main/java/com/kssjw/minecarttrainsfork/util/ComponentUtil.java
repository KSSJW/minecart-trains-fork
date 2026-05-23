package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.world.item.ItemStack;

public class ComponentUtil {

    private static final String KEY = "parent_id";

    public static void setParent(ItemStack stack, UUID uuid) {
        if (uuid == null) {
            stack.removeTagKey(KEY);
        } else {
            stack.getOrCreateTag().putUUID(KEY, uuid);
        }
    }

    public static void removeParent(ItemStack stack) {
        stack.removeTagKey(KEY);
    }

    public static UUID getParent(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().hasUUID(KEY)) {
            return stack.getTag().getUUID(KEY);
        }
        return null;
    }
}