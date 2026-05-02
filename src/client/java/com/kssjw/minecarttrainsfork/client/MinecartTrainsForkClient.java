package com.kssjw.minecarttrainsfork.client;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LoadManager.init();

		ClientPlayNetworking.registerGlobalReceiver(
			NetworkManager.RelationshipPayload.TYPE,
			(payload, context) -> {
				context.client().execute(() -> {
					ClientLevel clientWorld = Minecraft.getInstance().level;
				
					if (clientWorld != null) {
						UUID childUUID = payload.childUUID();
						UUID parentUUID = payload.parentUUID();

						IChainableUtil childChainableUtil = (IChainableUtil) clientWorld.getEntity(childUUID);
						IChainableUtil parentChainableUtil = (IChainableUtil) clientWorld.getEntity(parentUUID);

						if (childChainableUtil != null) childChainableUtil.setParentUUID(parentUUID);
						if (parentChainableUtil != null) parentChainableUtil.setChildUUID(childUUID);
					}
				});
			}
		);
	}
}