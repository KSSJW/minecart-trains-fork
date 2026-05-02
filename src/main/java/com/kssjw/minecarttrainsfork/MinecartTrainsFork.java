package com.kssjw.minecarttrainsfork;

import com.kssjw.minecarttrainsfork.manager.EventManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;

public class MinecartTrainsFork implements ModInitializer {

	public static final String MOD_ID = "minecart-trains-fork";

	@Override
	public void onInitialize() {
		
		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			return EventManager.init(entity, player, hand, world);
		});

		// For Development
		// ParticleEnumGenerator.generateEnum();
	}
}