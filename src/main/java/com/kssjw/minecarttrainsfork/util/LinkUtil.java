package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import org.jetbrains.annotations.Nullable;

public class LinkUtil {

    private LinkUtil() {}

    public static void setChainedParent(@Nullable AbstractMinecart newParent, IChainableUtil icu) {
        if(newParent != null) {
            @Nullable UUID parentUUID = newParent.getUUID();
            icu.setParentUUID(parentUUID);
        } else {
            @Nullable UUID parentUUID = null;
            icu.setParentUUID(parentUUID);
        }
    }

    public static void setChainedChild(@Nullable AbstractMinecart newChild, IChainableUtil icu) {
        if(newChild != null) {
            @Nullable UUID childUUID = newChild.getUUID();
            icu.setChildUUID(childUUID);
        } else {
            @Nullable UUID childUUID = null;
            icu.setChildUUID(childUUID);
        }
    }
}