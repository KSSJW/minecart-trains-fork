package com.kssjw.minecarttrainsfork.manager;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class NetworkManager {

    public static void sendRelationshipPayload(UUID childUUID, UUID parentUUID, ServerPlayer player) {
        if (player == null) return;

        RelationshipPayload relationship = new RelationshipPayload(childUUID, parentUUID);
        ServerPlayNetworking.send(player, relationship);
    }

    public record RelationshipPayload(UUID childUUID, UUID parentUUID) implements CustomPacketPayload {
        public static final Type<RelationshipPayload> TYPE = new Type<>(
          Identifier.fromNamespaceAndPath(MinecartTrainsFork.MOD_ID, "relationship")  
        );

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        public static final StreamCodec<FriendlyByteBuf, RelationshipPayload> CODEC = StreamCodec.of(
            
            // 写入 UUID
            (buf, payload) -> {
                buf.writeUUID(payload.childUUID() != null ? payload.childUUID() : new UUID(0L, 0L));
                buf.writeUUID(payload.parentUUID() != null ? payload.parentUUID() : new UUID(0L, 0L));
            },

            // 读取 UUID
            buf -> {
                UUID child = buf.readUUID();
                UUID parent = buf.readUUID();

                if (child.getMostSignificantBits() == 0L && child.getLeastSignificantBits() == 0L) child = null;
                if (parent.getMostSignificantBits() == 0L && parent.getLeastSignificantBits() == 0L) parent = null;

                return new RelationshipPayload(child, parent);
            }
        );
    }
}