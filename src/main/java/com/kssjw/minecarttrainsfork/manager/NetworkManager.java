package com.kssjw.minecarttrainsfork.manager;

import java.util.ArrayList;
import java.util.List;

import com.kssjw.minecarttrainsfork.util.ModIdUtil;
import com.mojang.datafixers.util.Pair;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

public class NetworkManager {
    
    public static void sendLinkingInitData(Entity entity) {
        if(entity instanceof AbstractMinecartEntity minecart) {
            ServerWorld world = (ServerWorld)entity.getEntityWorld();
            for (ServerPlayerEntity player : world.getPlayers()) {
                ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(minecart.getChainedParent() != null ? minecart.getChainedParent().getId() : -1, entity.getId()));
                ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(entity.getId(), minecart.getChainedChild() != null ? minecart.getChainedChild().getId() : -1));
            }
        }
    }

    public static void sendUnlinkData(AbstractMinecartEntity minecart) {
        ServerWorld world = (ServerWorld)minecart.getEntityWorld();
        for (ServerPlayerEntity player : world.getPlayers()) {
            ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(-1, minecart.getId()));    // 父断开
            ServerPlayNetworking.send(player, new ClientboundSyncMinecartTrainPacket(minecart.getId(), -1));    // 子断开
        }
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

    public static record ClientboundFullSyncTrainPacket(List<Pair<Integer,Integer>> links) implements CustomPayload {

        public static final CustomPayload.Id<ClientboundFullSyncTrainPacket> TYPE = new CustomPayload.Id<>(Identifier.of(ModIdUtil.MOD_ID, "full_sync_minecart_chain"));
        public static final PacketCodec<RegistryByteBuf, ClientboundFullSyncTrainPacket> CODEC =
            PacketCodec.of((packet, buffer) -> {
                buffer.writeVarInt(packet.links.size());
                for (var link : packet.links) {
                    buffer.writeVarInt(link.getFirst());
                    buffer.writeVarInt(link.getSecond());
                }
            }, buffer -> {
                int size = buffer.readVarInt();
                List<Pair<Integer,Integer>> links = new ArrayList<>();
                for (int i = 0; i < size; i++) {
                    links.add(Pair.of(buffer.readVarInt(), buffer.readVarInt()));
                }
                return new ClientboundFullSyncTrainPacket(links);
            });

        @Override
        public Id<? extends CustomPayload> getId() { return TYPE; }
    }
}