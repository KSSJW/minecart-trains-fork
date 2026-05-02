package com.kssjw.minecarttrainsfork.manager;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class NetworkManager {

    public static void sendRelationshipPayload(UUID childUUID, UUID parentUUID, ServerPlayerEntity player) {
        if (player == null) return;

        RelationshipPayload relationship = new RelationshipPayload(childUUID, parentUUID);
        ServerPlayNetworking.send(player, relationship);
    }

    public record RelationshipPayload(UUID childUUID, UUID parentUUID) implements CustomPayload {
        public static final Id<RelationshipPayload> TYPE = new Id<>(
          Identifier.of(MinecartTrainsFork.MOD_ID, "relationship")  
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return TYPE;
        }

        public static final PacketCodec<PacketByteBuf, RelationshipPayload> CODEC = PacketCodec.ofStatic(
            
            // 写入 UUID
            (buf, payload) -> {
                buf.writeUuid(payload.childUUID() != null ? payload.childUUID() : new UUID(0L, 0L));
                buf.writeUuid(payload.parentUUID() != null ? payload.parentUUID() : new UUID(0L, 0L));
            },

            // 读取 UUID
            buf -> {
                UUID child = buf.readUuid();
                UUID parent = buf.readUuid();

                if (child.getMostSignificantBits() == 0L && child.getLeastSignificantBits() == 0L) child = null;
                if (parent.getMostSignificantBits() == 0L && parent.getLeastSignificantBits() == 0L) parent = null;

                return new RelationshipPayload(child, parent);
            }
        );
    }
}