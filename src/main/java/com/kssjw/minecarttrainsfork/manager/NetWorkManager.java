package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.util.ModIdUtil;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

// TODO 全局广播
public class NetWorkManager {
    
    public static void sendLinkingInitData(Entity entity, ServerPlayerEntity player) {
        if(entity instanceof AbstractMinecartEntity minecart) {
            ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(minecart.getChainedParent() != null ? minecart.getChainedParent().getId() : -1, entity.getId()));
            ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(entity.getId(), minecart.getChainedChild() != null ? minecart.getChainedChild().getId() : -1));
        }
    }

    public static void sendUnlinkData(AbstractMinecartEntity minecart, ServerPlayerEntity player) {

        // 父断开
        ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(-1, minecart.getId()));

        // 子断开
        ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(minecart.getId(), -1));
    }

    public static record ClientboundSyncMinecartTrainPacket(int parentEntityID, int childEntityID) implements CustomPayload {

        public static final CustomPayload.Id<ClientboundSyncMinecartTrainPacket> TYPE = new CustomPayload.Id<>(Identifier.of(ModIdUtil.MOD_ID, "sync_minecart_chain"));
        public static final PacketCodec<RegistryByteBuf, ClientboundSyncMinecartTrainPacket> CODEC = PacketCodec.of((packet, buffer) -> {
            buffer.writeVarInt(packet.parentEntityID);
            buffer.writeVarInt(packet.childEntityID);
        }, buffer -> {
            int parentId = buffer.readVarInt();
            int childId = buffer.readVarInt();

            return new ClientboundSyncMinecartTrainPacket(parentId, childId);
        });

        @Override
        public Id<? extends CustomPayload> getId() {
            return TYPE;
        }
    }
}
