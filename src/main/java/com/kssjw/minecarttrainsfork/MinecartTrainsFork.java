package com.kssjw.minecarttrainsfork;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.kssjw.minecarttrainsfork.manager.EventManager;
import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundSyncMinecartTrainPacket;
import com.kssjw.minecarttrainsfork.util.ModIdUtil;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.UUID;

public class MinecartTrainsFork implements ModInitializer {

	public static final ComponentType<UUID> PARENT_ID = ComponentType.<UUID>builder().codec(
			RecordCodecBuilder.create(uuidInstance -> uuidInstance.group(
					Codec.LONG.fieldOf("most_sig_bits").forGetter(UUID::getMostSignificantBits),
					Codec.LONG.fieldOf("least_sig_bits").forGetter(UUID::getLeastSignificantBits)
			).apply(uuidInstance, UUID::new))
	).packetCodec(
			PacketCodec.tuple(
					PacketCodecs.VAR_LONG, UUID::getMostSignificantBits,
					PacketCodecs.VAR_LONG, UUID::getLeastSignificantBits,
					UUID::new
			)
	).build();

	@Override
	public void onInitialize() {
		
		PayloadTypeRegistry.playS2C().register(ClientboundSyncMinecartTrainPacket.TYPE, ClientboundSyncMinecartTrainPacket.CODEC);

		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ModIdUtil.MOD_ID, "parent_id"), PARENT_ID);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return EventManager.init(entity, player, hand, world, PARENT_ID);
		});
	}
}