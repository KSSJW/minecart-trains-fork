package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IChainableUtil {

    /* Interfaces cannot have constructors */

    UUID getParentUUID();
    void setParentUUID(@Nullable UUID uuid);

    UUID getChildUUID();
    void setChildUUID(@Nullable UUID uuid);

    // 默认实现：客户端断开时清理引用和 ID
    default @Nullable AbstractMinecartEntity getChainedParent() { return null; }
    default void setChainedParent(@Nullable AbstractMinecartEntity newParent) {}

    default @Nullable AbstractMinecartEntity getChainedChild() { return null; }
    default void setChainedChild(@Nullable AbstractMinecartEntity newChild) {}

    default AbstractMinecartEntity getAbstractMinecartEntity() {
        return (AbstractMinecartEntity) this;
    }

    // 建立连接：先清理旧关系，再建立新关系
    static void setChainedParentChild(@NotNull IChainableUtil parent, @NotNull IChainableUtil child) {
        unsetChainedParentChild(parent, (IChainableUtil)parent.getChainedChild());
        unsetChainedParentChild(child, (IChainableUtil)child.getChainedParent());
        parent.setChainedChild(child.getAbstractMinecartEntity());
        child.setChainedParent(parent.getAbstractMinecartEntity());
    }

    // 断开连接：同时清理引用和 ID
    static void unsetChainedParentChild(@Nullable IChainableUtil parent, @Nullable IChainableUtil child) {
        if (parent != null) parent.setChainedChild(null);
        if (child != null) child.setChainedParent(null);
    }
}