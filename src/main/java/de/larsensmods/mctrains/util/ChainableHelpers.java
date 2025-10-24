package de.larsensmods.mctrains.util;

import de.larsensmods.mctrains.interfaces.IChainable;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;

public final class ChainableHelpers {
    private ChainableHelpers() {}
    public static @Nullable IChainable asChainable(Entity e) {
        return (e instanceof IChainable) ? (IChainable) e : null;
    }
}