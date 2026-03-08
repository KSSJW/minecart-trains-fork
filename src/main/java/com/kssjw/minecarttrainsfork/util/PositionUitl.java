package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import net.minecraft.util.math.Vec3d;
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

    public static Vec3d getParentPos(UUID uuid) {
        IChainableUtil iChainable = (IChainableUtil)(world.getEntity(uuid));
        
        if (iChainable == null || iChainable.getParentUUID() == null) {
            return null;
        } else {
            return world.getEntity(iChainable.getParentUUID()).getEntityPos();
        }
    }
}