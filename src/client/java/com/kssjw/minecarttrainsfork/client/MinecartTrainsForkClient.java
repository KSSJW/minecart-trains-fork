package com.kssjw.minecarttrainsfork.client;

import com.kssjw.minecarttrainsfork.client.manager.LoadManager;

import net.fabricmc.api.ClientModInitializer;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LoadManager.init();
	}
}