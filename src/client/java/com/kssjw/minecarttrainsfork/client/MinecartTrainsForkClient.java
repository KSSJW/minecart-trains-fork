package com.kssjw.minecarttrainsfork.client;

import com.kssjw.minecarttrainsfork.client.config.ValueConfig;
import com.kssjw.minecarttrainsfork.client.networking.ClientPacketHandler;
import com.kssjw.minecarttrainsfork.manager.NetWorkManager.ClientboundSyncMinecartTrainPacket;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(ClientboundSyncMinecartTrainPacket.TYPE, (payload, context) -> {
			ClientPacketHandler.handleSyncMinecartTrain(payload);
		});

		AutoConfig.register(ValueConfig.class, GsonConfigSerializer::new);
	}

}