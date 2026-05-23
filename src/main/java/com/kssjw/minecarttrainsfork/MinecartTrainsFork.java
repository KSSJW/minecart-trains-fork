package com.kssjw.minecarttrainsfork;

import com.kssjw.minecarttrainsfork.manager.EventManager;
import com.kssjw.minecarttrainsfork.manager.LoadManager;
import com.kssjw.minecarttrainsfork.manager.NetworkManager;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("minecart_trains_fork")
public class MinecartTrainsFork {

	public static final String MOD_ID = "minecart_trains_fork";

	public MinecartTrainsFork() {		
		LoadManager.init();
		
		MinecraftForge.EVENT_BUS.addListener(EventManager::onEntityInteract);

		NetworkManager.register();

		// For Development
		// ParticleEnumGenerator.generateEnum();
	}
}