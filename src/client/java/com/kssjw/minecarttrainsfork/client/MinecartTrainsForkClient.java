package com.kssjw.minecarttrainsfork.client;

import java.util.UUID;

import com.kssjw.minecarttrainsfork.client.manager.LoadManager;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;
import com.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		LoadManager.init();

		// 1.20
		ClientPlayNetworking.registerGlobalReceiver(
			NetworkManager.RELATIONSHIP_PACKET,
			(client, handler, buf, responseSender) -> {

				UUID childUUID = buf.readBoolean() ? buf.readUuid() : null;
				UUID parentUUID = buf.readBoolean() ? buf.readUuid() : null;

				client.execute(() -> {
					ClientWorld clientWorld = client.world;

					if (clientWorld == null) return;

					IChainableUtil childChainableUtil = null;
					IChainableUtil parentChainableUtil = null;

					for (Entity entity : clientWorld.getEntities()) {
						if (entity instanceof AbstractMinecartEntity minecart) {
							if (minecart.getUuid().equals(childUUID)) childChainableUtil = (IChainableUtil) minecart;
							if (minecart.getUuid().equals(parentUUID)) parentChainableUtil = (IChainableUtil) minecart;
						}
					}

					if (childChainableUtil != null) childChainableUtil.setParentUUID(parentUUID);
					if (parentChainableUtil != null) parentChainableUtil.setChildUUID(childUUID);
				});
			}
		);
	}
}