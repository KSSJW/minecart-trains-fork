package com.kssjw.minecarttrainsfork.util;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IChainableUtil {

    UUID getParentUUID();
    void setParentUUID(@Nullable UUID uuid);

    UUID getChildUUID();
    void setChildUUID(@Nullable UUID uuid);

    int getParentClientID();
    void setParentClientID(int id);

    int getChildClientID();
    void setChildClientID(int id);

    // 默认实现：客户端断开时清理引用和 ID
    default @Nullable AbstractMinecartEntity getChainedParent() { return null; }
    default void setChainedParent(@Nullable AbstractMinecartEntity newParent) {}

    default void setClientChainedParent(int entityId) {
        if (entityId == -1) {
            setChainedParent(null);
            setParentClientID(-1);
        } else {
            setParentClientID(entityId);
        }
    }

    default @Nullable AbstractMinecartEntity getChainedChild() { return null; }
    default void setChainedChild(@Nullable AbstractMinecartEntity newChild) {}

    default void setClientChainedChild(int entityId) {
        if (entityId == -1) {
            setChainedChild(null);
            setChildClientID(-1);
        } else {
            setChildClientID(entityId);
        }
    }

    default AbstractMinecartEntity getAsAbstractMinecartEntity() {
        return (AbstractMinecartEntity) this;
    }

    // 建立连接：先清理旧关系，再建立新关系
    static void setChainedParentChild(@NotNull IChainableUtil parent, @NotNull IChainableUtil child) {
        unsetChainedParentChild(parent, parent.getChainedChild());
        unsetChainedParentChild(child, child.getChainedParent());
        parent.setChainedChild(child.getAsAbstractMinecartEntity());
        child.setChainedParent(parent.getAsAbstractMinecartEntity());
    }

    // 断开连接：同时清理引用和 ID
    static void unsetChainedParentChild(@Nullable IChainableUtil parent, @Nullable IChainableUtil child) {
        if (parent != null) {
            parent.setChainedChild(null);
            parent.setChildClientID(-1);
        }
        if (child != null) {
            child.setChainedParent(null);
            child.setParentClientID(-1);
        }
    }
}