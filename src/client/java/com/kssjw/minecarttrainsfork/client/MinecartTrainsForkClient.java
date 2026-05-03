package com.kssjw.minecarttrainsfork.client;

import com.kssjw.minecarttrainsfork.client.extension.config.ConfigEntry;
import com.kssjw.minecarttrainsfork.client.manager.LoadManager;

import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.api.distmarker.Dist;

@Mod(value = "minecart_trains_fork", dist = Dist.CLIENT)
public class MinecartTrainsForkClient {

	public MinecartTrainsForkClient() {
		LoadManager.init();
		ModLoadingContext.get().registerExtensionPoint(
			IConfigScreenFactory.class,
			() -> (mod, parent) -> {
                return ConfigEntry.getModConfigScreenFactory(parent);
            }
		);
	}
}