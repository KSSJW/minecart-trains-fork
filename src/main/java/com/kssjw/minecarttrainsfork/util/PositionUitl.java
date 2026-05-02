package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class PositionUitl {
    
    private PositionUitl() {}

    private static Level world;

    public static boolean isWorldInitiated(Level targetWorld) {
        if (world == null || targetWorld != world) {
            return false;
        } else {
            return true;
        }
    }

    public static void setWorld(Level targetWorld) {
        world = targetWorld;
    }

    public static UUID getParentUUID(UUID uuid) {
        IChainableUtil iChainable = (IChainableUtil)(((ServerLevel)world).getEntity(uuid));

        if (iChainable == null || iChainable.getParentUUID() == null) {
            return null;
        } else {
            return iChainable.getParentUUID();
        }
    }
}