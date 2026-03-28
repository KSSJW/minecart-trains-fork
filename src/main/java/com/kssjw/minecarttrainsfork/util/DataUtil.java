package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class DataUtil {

    private DataUtil() {}

    public static void writeData(ValueOutput view, IChainableUtil icu) {
        view.putLong("ParentUUIDMost", icu.getParentUUID() != null ? icu.getParentUUID().getMostSignificantBits() : 0L);
        view.putLong("ParentUUIDLeast", icu.getParentUUID() != null ? icu.getParentUUID().getLeastSignificantBits() : 0L);

        view.putLong("ChildUUIDMost", icu.getChildUUID() != null ? icu.getChildUUID().getMostSignificantBits() : 0L);
        view.putLong("ChildUUIDLeast", icu.getChildUUID() != null ? icu.getChildUUID().getLeastSignificantBits() : 0L);
    }


    public static void readData(ValueInput view, IChainableUtil icu) {
        long parentMost = view.getLongOr("ParentUUIDMost", 0L);
        long parentLeast = view.getLongOr("ParentUUIDLeast", 0L);
        @Nullable UUID parentUUID = (parentMost != 0L || parentLeast != 0L) ? new UUID(parentMost, parentLeast) : null;
        icu.setParentUUID(parentUUID);

        long childMost = view.getLongOr("ChildUUIDMost", 0L);
        long childLeast = view.getLongOr("ChildUUIDLeast", 0L);
        @Nullable UUID childUUID = (childMost != 0L || childLeast != 0L) ? new UUID(childMost, childLeast) : null;
        icu.setChildUUID(childUUID);
    }
}