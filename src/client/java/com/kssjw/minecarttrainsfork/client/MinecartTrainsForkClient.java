package com.kssjw.minecarttrainsfork.client;

import com.kssjw.minecarttrainsfork.client.loader.ConfigLoader;
import com.kssjw.minecarttrainsfork.client.networking.ClientPacketHandler;
import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundFullSyncTrainPacket;
import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundSyncMinecartTrainPacket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncMinecartTrainPacket.TYPE, (payload, context) -> {
			ClientPacketHandler.handleSyncMinecartTrain(payload);
		});
		ClientPlayNetworking.registerGlobalReceiver(ClientboundFullSyncTrainPacket.TYPE, (payload, context) -> {
			ClientPacketHandler.handleFullSyncTrain(payload);
		});

		ConfigLoader.load();
	}
}