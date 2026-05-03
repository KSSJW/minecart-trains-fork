package com.kssjw.minecarttrainsfork;

import com.kssjw.minecarttrainsfork.manager.EventManager;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;
import com.kssjw.minecarttrainsfork.util.ComponentUtil;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("minecart_trains_fork")
public class MinecartTrainsFork {

	public static final String MOD_ID = "minecart_trains_fork";

	public MinecartTrainsFork(IEventBus modEventBus) {
		
		ComponentUtil.register(modEventBus);

		NeoForge.EVENT_BUS.addListener(EventManager::onEntityInteract);

		modEventBus.addListener(NetworkManager::registerPayloads);

		// For Development
		// ParticleEnumGenerator.generateEnum();
	}
}