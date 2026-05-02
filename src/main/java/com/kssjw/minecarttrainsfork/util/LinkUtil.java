package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.Nullable;

public class LinkUtil {

    private LinkUtil() {}

    public static void setChainedParent(@Nullable AbstractMinecartEntity newParent, IChainableUtil icu) {
        if(newParent != null) {
            @Nullable UUID parentUUID = newParent.getUuid();
            icu.setParentUUID(parentUUID);
        } else {
            @Nullable UUID parentUUID = null;
            icu.setParentUUID(parentUUID);
        }
    }

    public static void setChainedChild(@Nullable AbstractMinecartEntity newChild, IChainableUtil icu) {
        if(newChild != null) {
            @Nullable UUID childUUID = newChild.getUuid();
            icu.setChildUUID(childUUID);
        } else {
            @Nullable UUID childUUID = null;
            icu.setChildUUID(childUUID);
        }
    }
}