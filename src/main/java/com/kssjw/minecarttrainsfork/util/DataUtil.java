package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;

public class DataUtil {

    private DataUtil() {}

    public static void writeData(WriteView view, IChainableUtil icu) {
        view.putLong("ParentUUIDMost", icu.getParentUUID() != null ? icu.getParentUUID().getMostSignificantBits() : 0L);
        view.putLong("ParentUUIDLeast", icu.getParentUUID() != null ? icu.getParentUUID().getLeastSignificantBits() : 0L);

        view.putLong("ChildUUIDMost", icu.getChildUUID() != null ? icu.getChildUUID().getMostSignificantBits() : 0L);
        view.putLong("ChildUUIDLeast", icu.getChildUUID() != null ? icu.getChildUUID().getLeastSignificantBits() : 0L);

        view.putInt("ParentClientID", icu.getParentClientID());
        view.putInt("ChildClientID", icu.getChildClientID());
    }


    public static void readData(ReadView view, IChainableUtil icu) {
        long parentMost = view.getLong("ParentUUIDMost", 0L);
        long parentLeast = view.getLong("ParentUUIDLeast", 0L);
        @Nullable UUID parentUUID = (parentMost != 0L || parentLeast != 0L) ? new UUID(parentMost, parentLeast) : null;
        icu.setParentUUID(parentUUID);

        long childMost = view.getLong("ChildUUIDMost", 0L);
        long childLeast = view.getLong("ChildUUIDLeast", 0L);
        @Nullable UUID childUUID = (childMost != 0L || childLeast != 0L) ? new UUID(childMost, childLeast) : null;
        icu.setChildUUID(childUUID);

        int parentClientID = view.getInt("ParentClientID", -1);
        icu.setParentClientID(parentClientID);

        int childClientID = view.getInt("ChildClientID", -1);
        icu.setChildClientID(childClientID);
    }
}