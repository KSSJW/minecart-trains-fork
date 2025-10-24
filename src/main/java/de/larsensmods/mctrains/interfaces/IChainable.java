package de.larsensmods.mctrains.interfaces;

import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * IChainable exposes chain relationships in terms of the interface itself
 * (getChainedParent/getChainedChild return IChainable), avoiding compile-time
 * dependence on AbstractMinecartEntity. When caller needs the concrete entity,
 * they must use asEntity() / asMinecart() guarded by instanceof checks.
 */
public interface IChainable {
    // Chain accessors operate on the interface type to be compile-time safe
    @Nullable IChainable getChainedParent();
    void setChainedParent(@Nullable IChainable newParent);
    void setClientChainedParent(int entityId);

    @Nullable IChainable getChainedChild();
    void setChainedChild(@Nullable IChainable newChild);
    void setClientChainedChild(int entityId);

    /**
     * Return the underlying Entity (if applicable). Default returns null.
     * Callers must check instanceof or null before casting.
     */
    @Nullable default Entity asEntity() {
        return null;
    }

    /**
     * Convenience: return as AbstractMinecartEntity when the underlying entity
     * is indeed a minecart. Default returns null; caller must check.
     */
    @Nullable default AbstractMinecartEntity asMinecart() {
        Entity e = asEntity();
        return (e instanceof AbstractMinecartEntity) ? (AbstractMinecartEntity) e : null;
    }

    /**
     * Set parent-child relationship using IChainable parameters.
     * This method only manipulates interface-level links, so callers can
     * safely use it at compile time without referencing concrete types.
     */
    static void setChainedParentChild(@NotNull IChainable parent, @NotNull IChainable child){
        // remove any previous links touching these nodes
        unsetChainedParentChild(parent, parent.getChainedChild());
        unsetChainedParentChild(child, child.getChainedParent());
        parent.setChainedChild(child);
        child.setChainedParent(parent);
    }

    static void unsetChainedParentChild(@Nullable IChainable parent, @Nullable IChainable child){
        if(parent != null){
            parent.setChainedChild(null);
        }
        if(child != null){
            child.setChainedParent(null);
        }
    }
}