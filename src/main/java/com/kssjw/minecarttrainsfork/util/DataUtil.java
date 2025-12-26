package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.NbtCompound;

public class DataUtil {

    private DataUtil() {}

    public static void writeData(NbtCompound nbt, IChainableUtil icu) {
        nbt.putLong("ParentUUIDMost", icu.getParentUUID() != null ? icu.getParentUUID().getMostSignificantBits() : 0L);
        nbt.putLong("ParentUUIDLeast", icu.getParentUUID() != null ? icu.getParentUUID().getLeastSignificantBits() : 0L);

        nbt.putLong("ChildUUIDMost", icu.getChildUUID() != null ? icu.getChildUUID().getMostSignificantBits() : 0L);
        nbt.putLong("ChildUUIDLeast", icu.getChildUUID() != null ? icu.getChildUUID().getLeastSignificantBits() : 0L);

        nbt.putInt("ParentClientID", icu.getParentClientID());
        nbt.putInt("ChildClientID", icu.getChildClientID());
    }


    public static void readData(NbtCompound nbt, IChainableUtil icu) {
        long parentMost = nbt.getLong("ParentUUIDMost", 0L);
        long parentLeast = nbt.getLong("ParentUUIDLeast", 0L);
        @Nullable UUID parentUUID = (parentMost != 0L || parentLeast != 0L) ? new UUID(parentMost, parentLeast) : null;
        icu.setParentUUID(parentUUID);

        long childMost = nbt.getLong("ChildUUIDMost", 0L);
        long childLeast = nbt.getLong("ChildUUIDLeast", 0L);
        @Nullable UUID childUUID = (childMost != 0L || childLeast != 0L) ? new UUID(childMost, childLeast) : null;
        icu.setChildUUID(childUUID);

        int parentClientID = nbt.getInt("ParentClientID", -1);
        icu.setParentClientID(parentClientID);

        int childClientID = nbt.getInt("ChildClientID", -1);
        icu.setChildClientID(childClientID);
    }
}