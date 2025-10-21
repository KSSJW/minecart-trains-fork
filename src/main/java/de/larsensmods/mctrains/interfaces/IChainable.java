package de.larsensmods.mctrains.interfaces;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IChainable {
    default @Nullable AbstractMinecartEntity getChainedParent() {
        return null;
    }
    default void setChainedParent(@Nullable AbstractMinecartEntity newParent) {}
    default void setClientChainedParent(int entityId) {}

    default @Nullable AbstractMinecartEntity getChainedChild() {
        return null;
    }
    default void setChainedChild(@Nullable AbstractMinecartEntity newChild) {}
    default void setClientChainedChild(int entityId) {}

    default AbstractMinecartEntity getAsAbstractMinecartEntity() {
        return (AbstractMinecartEntity) this;
    }

    static void setChainedParentChild(@NotNull IChainable parent, @NotNull IChainable child) {
        // 修正强制类型转换
        unsetChainedParentChild(parent, (IChainable) (Object) parent.getChainedChild());
        unsetChainedParentChild(child, (IChainable) (Object) child.getChainedParent());

        parent.setChainedChild(child.getAsAbstractMinecartEntity());
        child.setChainedParent(parent.getAsAbstractMinecartEntity());
    }

    static void unsetChainedParentChild(@Nullable IChainable parent, @Nullable IChainable child) {
        if (parent != null) {
            parent.setChainedChild(null);
        }
        if (child != null) {
            child.setChainedParent(null);
        }
    }
}