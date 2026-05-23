package com.kssjw.minecarttrainsfork.client;

import com.kssjw.minecarttrainsfork.client.extension.config.ConfigEntry;
import com.kssjw.minecarttrainsfork.client.manager.ClientLoadManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler.ConfigScreenFactory;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "minecart_trains_fork", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MinecartTrainsForkClient {

	@SubscribeEvent
	public static void onClientSetup(FMLClientSetupEvent event) {
		ClientLoadManager.init();
		ModLoadingContext.get().registerExtensionPoint(
			ConfigScreenFactory.class,
			() -> new ConfigScreenFactory(
				(mc, parent) -> {
					return ConfigEntry.getModConfigScreenFactory(parent);
				}
			)
		);
	}
}