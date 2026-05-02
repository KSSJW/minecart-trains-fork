package com.kssjw.minecarttrainsfork.manager;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

// 1.20
public class NetworkManager {
    public static final Identifier RELATIONSHIP_PACKET = new Identifier(MinecartTrainsFork.MOD_ID, "relationship");

    public class RelationshipPayload {
        public final UUID childUUID;
        public final UUID parentUUID;

        public RelationshipPayload(UUID childUUID, UUID parentUUID) {
            this.childUUID = childUUID;
            this.parentUUID = parentUUID;
        }
    }

    public static void sendRelationshipPayload(UUID childUUID, UUID parentUUID, ServerPlayerEntity player) {
        if (player == null) return;

        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());

        buf.writeBoolean(childUUID != null);
        if (childUUID != null) buf.writeUuid(childUUID);

        buf.writeBoolean(parentUUID != null);
        if (parentUUID != null) buf.writeUuid(parentUUID);

        ServerPlayNetworking.send(player, RELATIONSHIP_PACKET, buf);
    }
}