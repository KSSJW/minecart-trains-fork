package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class PositionUitl {
    
    private PositionUitl() {}

    private static World world;

    public static boolean isWorldInitiated(World targetWorld) {
        if (world == null || targetWorld != world) {
            return false;
        } else {
            return true;
        }
    }

    public static void setWorld(World targetWorld) {
        world = targetWorld;
    }

    public static UUID getParentUUID(UUID uuid) {
        IChainableUtil iChainable = (IChainableUtil)(((ServerWorld)world).getEntity(uuid));

        if (iChainable == null || iChainable.getParentUUID() == null) {
            return null;
        } else {
            return iChainable.getParentUUID();
        }
    }
}