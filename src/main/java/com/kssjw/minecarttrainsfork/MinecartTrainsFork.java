package com.kssjw.minecarttrainsfork;

import com.kssjw.minecarttrainsfork.manager.EventManager;
import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundFullSyncTrainPacket;
import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundSyncMinecartTrainPacket;
import com.kssjw.minecarttrainsfork.util.ComponentUtil;
import com.kssjw.minecarttrainsfork.util.ModIdUtil;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MinecartTrainsFork implements ModInitializer {

	@Override
	public void onInitialize() {
		
		PayloadTypeRegistry.playS2C().register(ClientboundSyncMinecartTrainPacket.TYPE, ClientboundSyncMinecartTrainPacket.CODEC);
		PayloadTypeRegistry.playS2C().register(ClientboundFullSyncTrainPacket.TYPE, ClientboundFullSyncTrainPacket.CODEC);

		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(ModIdUtil.MOD_ID, "parent_id"), ComponentUtil.PARENT_ID);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return EventManager.init(entity, player, hand, world, ComponentUtil.PARENT_ID);
		});
	}
}