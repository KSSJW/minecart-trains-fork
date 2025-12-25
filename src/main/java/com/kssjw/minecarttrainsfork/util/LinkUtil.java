package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

import com.kssjw.minecarttrainsfork.manager.NetworkManager.ClientboundSyncMinecartTrainPacket;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.server.world.ServerWorld;

public class LinkUtil {

    private LinkUtil() {}

    public static @Nullable AbstractMinecartEntity getChainedParent(AbstractMinecartEntity entity, IChainableUtil icu) {
        Entity target = entity.getEntityWorld() instanceof ServerWorld sWorld && icu.getParentUUID() != null ? sWorld.getEntity(icu.getParentUUID()) : entity.getEntityWorld().getEntityById(icu.getParentClientID());
        return target instanceof AbstractMinecartEntity minecart ? minecart : null;
    }

    public static void setChainedParent(@Nullable AbstractMinecartEntity newParent, IChainableUtil icu, AbstractMinecartEntity entity) {
        if(newParent != null) {
            @Nullable UUID parentUUID = newParent.getUuid();
            int parentClientID = newParent.getId();
            icu.setParentUUID(parentUUID);
            icu.setParentClientID(parentClientID);
        } else {
            @Nullable UUID parentUUID = null;
            int parentClientID = -1;
            icu.setParentUUID(parentUUID);
            icu.setParentClientID(parentClientID);
        }
        if(!entity.getEntityWorld().isClient()) {
            PlayerLookup.tracking(entity).forEach(player -> ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(getChainedParent(entity, icu) != null ? getChainedParent(entity, icu).getId() : -1, entity.getId())));
        }
    }

    public static void setClientChainedParent(int entityId, IChainableUtil icu) {
        int parentClientID = entityId;
        icu.setParentClientID(parentClientID);
    }

    public static @Nullable AbstractMinecartEntity getChainedChild(AbstractMinecartEntity entity, IChainableUtil icu) {
        Entity target = entity.getEntityWorld() instanceof ServerWorld sWorld && icu.getChildUUID() != null ? sWorld.getEntity(icu.getChildUUID()) : entity.getEntityWorld().getEntityById(icu.getChildClientID());
        return target instanceof AbstractMinecartEntity minecart ? minecart : null;
    }

    public static void setChainedChild(@Nullable AbstractMinecartEntity newChild, IChainableUtil icu) {
        if(newChild != null) {
            @Nullable UUID childUUID = newChild.getUuid();
            int childClientID = newChild.getId();
            icu.setChildUUID(childUUID);
            icu.setChildClientID(childClientID);
        } else {
            @Nullable UUID childUUID = null;
            int childClientID = -1;
            icu.setChildUUID(childUUID);
            icu.setChildClientID(childClientID);
        }
    }

    public static void setClientChainedChild(int entityId, IChainableUtil icu) {
        int childClientID = entityId;
        icu.setChildClientID(childClientID);
    }
}