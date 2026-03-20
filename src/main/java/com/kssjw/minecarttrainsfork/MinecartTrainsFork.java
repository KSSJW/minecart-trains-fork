package com.kssjw.minecarttrainsfork;

import com.kssjw.minecarttrainsfork.manager.EventManager;
import com.kssjw.minecarttrainsfork.util.ComponentUtil;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MinecartTrainsFork implements ModInitializer {

	public static final String MOD_ID = "minecart-trains-fork";

	@Override
	public void onInitialize() {
		
		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "parent_id"), ComponentUtil.PARENT_ID);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return EventManager.init(entity, player, hand, world, ComponentUtil.PARENT_ID);
		});

		// For Development
		// ParticleEnumGenerator.generateEnum();
	}
}