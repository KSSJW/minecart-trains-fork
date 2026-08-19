package org.kssjw.minecarttrainsfork.client;

import java.util.UUID;

import org.kssjw.minecarttrainsfork.client.manager.ClientLoadManager;
import org.kssjw.minecarttrainsfork.manager.NetworkManager;
import org.kssjw.minecarttrainsfork.util.IChainableUtil;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

public class MinecartTrainsForkClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientLoadManager.init();

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