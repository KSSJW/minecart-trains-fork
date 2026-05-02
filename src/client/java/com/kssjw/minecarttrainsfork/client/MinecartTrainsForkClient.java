package com.kssjw.minecarttrainsfork.client;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LoadManager.init();

		ClientPlayNetworking.registerGlobalReceiver(
			NetworkManager.RelationshipPayload.TYPE,
			(payload, context) -> {
				context.client().execute(() -> {
					ClientWorld clientWorld = MinecraftClient.getInstance().world;
				
					if (clientWorld != null) {
						UUID childUUID = payload.childUUID();
						UUID parentUUID = payload.parentUUID();

						IChainableUtil childChainableUtil = null;
						IChainableUtil parentChainableUtil = null;

						for (Entity entity : clientWorld.getEntities()) {
							if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(childUUID)) childChainableUtil = (IChainableUtil) entity;
							if (entity instanceof AbstractMinecartEntity && entity.getUuid().equals(parentUUID)) parentChainableUtil = (IChainableUtil) entity;
						}

						if (childChainableUtil != null) childChainableUtil.setParentUUID(parentUUID);
						if (parentChainableUtil != null) parentChainableUtil.setChildUUID(childUUID);
					}
				});
			}
		);
	}
}