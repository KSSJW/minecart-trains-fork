package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.jetbrains.annotations.Nullable;

public class DataUtil {

    private DataUtil() {}

    public static void writeData(WriteView view, IChainableUtil icu) {
        view.putLong("ParentUUIDMost", icu.getParentUUID() != null ? icu.getParentUUID().getMostSignificantBits() : 0L);
        view.putLong("ParentUUIDLeast", icu.getParentUUID() != null ? icu.getParentUUID().getLeastSignificantBits() : 0L);

        view.putLong("ChildUUIDMost", icu.getChildUUID() != null ? icu.getChildUUID().getMostSignificantBits() : 0L);
        view.putLong("ChildUUIDLeast", icu.getChildUUID() != null ? icu.getChildUUID().getLeastSignificantBits() : 0L);
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
    }
}