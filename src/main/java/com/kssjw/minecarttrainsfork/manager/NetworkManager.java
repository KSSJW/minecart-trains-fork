package com.kssjw.minecarttrainsfork.manager;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class NetworkManager {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
		var registrar = event.registrar("1");

		 registrar.playToClient(
			NetworkManager.RelationshipPayload.TYPE,
        	NetworkManager.RelationshipPayload.CODEC,
			(payload, context) -> {
				context.enqueueWork(() -> {
					ClientLevel clientWorld = Minecraft.getInstance().level;

					if (clientWorld == null) return;

					UUID childUUID = payload.childUUID();
					UUID parentUUID = payload.parentUUID();

					IChainableUtil childChainableUtil = (IChainableUtil) clientWorld.getEntity(childUUID);
					IChainableUtil parentChainableUtil = (IChainableUtil) clientWorld.getEntity(parentUUID);

					if (childChainableUtil != null) childChainableUtil.setParentUUID(parentUUID);
					if (parentChainableUtil != null) parentChainableUtil.setChildUUID(childUUID);
				});
			}
		 );
	}

    public static void sendRelationshipPayload(UUID childUUID, UUID parentUUID, ServerPlayer player) {
        if (player == null) return;

        RelationshipPayload relationship = new RelationshipPayload(childUUID, parentUUID);
        player.connection.send(relationship);
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