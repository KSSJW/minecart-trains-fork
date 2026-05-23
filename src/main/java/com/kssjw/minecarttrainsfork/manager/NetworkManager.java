package com.kssjw.minecarttrainsfork.manager;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.UUID;
import java.util.function.Supplier;

public class NetworkManager {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL =
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(MinecartTrainsFork.MOD_ID, "main"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );

    private static int packetId = 0;

    public static void register() {

        CHANNEL.registerMessage(
                packetId++,
                RelationshipPayload.class,

                RelationshipPayload::encode,
                RelationshipPayload::decode,
                RelationshipPayload::handle
        );
    }

    public static class RelationshipPayload {

        public UUID childUUID;
        public UUID parentUUID;

        public RelationshipPayload(UUID childUUID, UUID parentUUID) {
            this.childUUID = childUUID;
            this.parentUUID = parentUUID;
        }

        // 编码
        public static void encode(RelationshipPayload packet, FriendlyByteBuf buf) {

            buf.writeBoolean(packet.childUUID != null);

            if (packet.childUUID != null)
                buf.writeUUID(packet.childUUID);

            buf.writeBoolean(packet.parentUUID != null);

            if (packet.parentUUID != null)
                buf.writeUUID(packet.parentUUID);
        }

        // 解码
        public static RelationshipPayload decode(FriendlyByteBuf buf) {

            UUID child = null;
            UUID parent = null;

            if (buf.readBoolean())
                child = buf.readUUID();

            if (buf.readBoolean())
                parent = buf.readUUID();

            return new RelationshipPayload(child, parent);
        }

        // 接收处理
        public static void handle(
                RelationshipPayload packet,
                Supplier<NetworkEvent.Context> ctx
        ) {

            ctx.get().enqueueWork(() -> {

                Minecraft client = Minecraft.getInstance();

                ClientLevel clientWorld = client.level;

                if (clientWorld == null)
                    return;

                IChainableUtil childChainableUtil = null;
                IChainableUtil parentChainableUtil = null;

                for (Entity entity : clientWorld.entitiesForRendering()) {

                    if (entity instanceof AbstractMinecart minecart) {

                        if (minecart.getUUID().equals(packet.childUUID))
                            childChainableUtil = (IChainableUtil) minecart;

                        if (minecart.getUUID().equals(packet.parentUUID))
                            parentChainableUtil = (IChainableUtil) minecart;
                    }
                }

                if (childChainableUtil != null)
                    childChainableUtil.setParentUUID(packet.parentUUID);

                if (parentChainableUtil != null)
                    parentChainableUtil.setChildUUID(packet.childUUID);
            });

            ctx.get().setPacketHandled(true);
        }
    }

    // 发送
    public static void sendRelationshipPayload(
            UUID childUUID,
            UUID parentUUID,
            ServerPlayer player
    ) {

        if (player == null)
            return;

        CHANNEL.send(
                PacketDistributor.PLAYER.with(() -> player),
                new RelationshipPayload(childUUID, parentUUID)
        );
    }
}