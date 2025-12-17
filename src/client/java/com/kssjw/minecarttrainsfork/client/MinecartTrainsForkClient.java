package com.kssjw.minecarttrainsfork.client;

import com.kssjw.minecarttrainsfork.client.manager.ClientNetworkManager;
import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import com.kssjw.minecarttrainsfork.manager.NetworkManager.ClientboundFullSyncTrainPacket;
import com.kssjw.minecarttrainsfork.manager.NetworkManager.ClientboundSyncMinecartTrainPacket;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncMinecartTrainPacket.TYPE, (payload, context) -> {
			ClientNetworkManager.handleSyncMinecartTrain(payload);
		});
		ClientPlayNetworking.registerGlobalReceiver(ClientboundFullSyncTrainPacket.TYPE, (payload, context) -> {
			ClientNetworkManager.handleFullSyncTrain(payload);
		});

		LoadManager.init();
	}
}