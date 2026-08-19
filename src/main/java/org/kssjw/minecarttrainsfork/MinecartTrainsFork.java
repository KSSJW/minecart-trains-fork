package org.kssjw.minecarttrainsfork;

import org.kssjw.minecarttrainsfork.manager.EventManager;
import org.kssjw.minecarttrainsfork.manager.LoadManager;
import org.kssjw.minecarttrainsfork.manager.NetworkManager;
import org.kssjw.minecarttrainsfork.util.ComponentUtil;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class MinecartTrainsFork implements ModInitializer {

	public static final String MOD_ID = "minecart-trains-fork";

	@Override
	public void onInitialize() {
		LoadManager.init();
		
		Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, "parent_id"), ComponentUtil.PARENT_ID);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return EventManager.init(entity, player, hand, world, ComponentUtil.PARENT_ID);
		});

		PayloadTypeRegistry.clientboundPlay().register(NetworkManager.RelationshipPayload.TYPE, NetworkManager.RelationshipPayload.CODEC);

		// For Development
		// ParticleEnumGenerator.generateEnum();
	}
}