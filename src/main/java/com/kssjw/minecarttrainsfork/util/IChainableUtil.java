package com.kssjw.minecarttrainsfork.util;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;

import java.util.UUID;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IChainableUtil {

    /* Interfaces cannot have constructors */

    UUID getParentUUID();
    void setParentUUID(UUID uuid);

    UUID getChildUUID();
    void setChildUUID(UUID uuid);

    int getParentClientID();
    void setParentClientID(int id);
    
    int getChildClientID();
    void setChildClientID(int id);

    default @Nullable AbstractMinecartEntity getChainedParent(){
        return null;
    }
    default void setChainedParent(@Nullable AbstractMinecartEntity newParent){}
    default void setClientChainedParent(int entityId){}

    default @Nullable AbstractMinecartEntity getChainedChild(){
        return null;
    }
    default void setChainedChild(@Nullable AbstractMinecartEntity newChild){}
    default void setClientChainedChild(int entityId){}

    default AbstractMinecartEntity getAsAbstractMinecartEntity(){
        return (AbstractMinecartEntity) this;
    }

    static void setChainedParentChild(@NotNull IChainableUtil parent, @NotNull IChainableUtil child){
        unsetChainedParentChild(parent, parent.getChainedChild());
        unsetChainedParentChild(child, child.getChainedParent());
        parent.setChainedChild(child.getAsAbstractMinecartEntity());
        child.setChainedParent(parent.getAsAbstractMinecartEntity());
    }

    static void unsetChainedParentChild(@Nullable IChainableUtil parent, @Nullable IChainableUtil child){
        if(parent != null){
            parent.setChainedChild(null);
        }
        if(child != null){
            child.setChainedParent(null);
        }
    }
}
