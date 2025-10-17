package de.larsensmods.mctrains;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.larsensmods.mctrains.config.MCTConfig;
import de.larsensmods.mctrains.interfaces.IChainable;
import de.larsensmods.mctrains.networking.ClientboundSyncMinecartTrainPacket;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MinecartTrains implements ModInitializer {

	public static final String MOD_ID = "minecart-trains";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static MCTConfig CONFIG;

	public static final ComponentType<UUID> PARENT_ID = ComponentType.<UUID>builder().codec(
			RecordCodecBuilder.create(uuidInstance -> uuidInstance.group(
					Codec.LONG.fieldOf("most_sig_bits").forGetter(UUID::getMostSignificantBits),
					Codec.LONG.fieldOf("least_sig_bits").forGetter(UUID::getLeastSignificantBits)
			).apply(uuidInstance, UUID::new))
	).packetCodec(
			PacketCodec.tuple(
					PacketCodecs.VAR_LONG, UUID::getMostSignificantBits,
					PacketCodecs.VAR_LONG, UUID::getLeastSignificantBits,
					UUID::new
			)
	).build();

	@Override
	public void onInitialize() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		File configFile = new File("config", "minecart-trains.json");
		boolean createNewConfig = false;
		if(configFile.exists()) {
            try(FileReader reader = new FileReader(configFile)){
				CONFIG = gson.fromJson(reader, MCTConfig.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (JsonSyntaxException | JsonIOException e) {
				LOGGER.warn("Error reading config file", e);
				if(!configFile.renameTo(new File("config", "minecart-trains.json.bak"))){
					throw new RuntimeException("Couldn't access broken config file, aborting...", e);
				}
				createNewConfig = true;
			}
        }else{
			createNewConfig = true;
		}
		if(createNewConfig) {
			CONFIG = new MCTConfig();
			configFile.getParentFile().mkdirs();
			try(FileWriter writer = new FileWriter(configFile)) {
				gson.toJson(CONFIG, writer);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		PayloadTypeRegistry.playS2C().register(ClientboundSyncMinecartTrainPacket.TYPE, ClientboundSyncMinecartTrainPacket.CODEC);

		Registry.register(Registries.DATA_COMPONENT_TYPE, Identifier.of(MOD_ID, "parent_id"), PARENT_ID);

		UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if(entity instanceof AbstractMinecartEntity cart) {
				ItemStack stack = player.getStackInHand(hand);

				if(player.isSneaking() && stack.isOf(Items.CHAIN) && CONFIG.enableCartChaining()) {
					if(world instanceof ServerWorld server) {
						UUID uuid = stack.get(PARENT_ID);

						if(uuid != null && !cart.getUuid().equals(uuid)) {
							if(server.getEntity(uuid) instanceof AbstractMinecartEntity parent) {
								Set<IChainable> train = new HashSet<>();
								train.add(parent);

								AbstractMinecartEntity nextChainedParent;
								while((nextChainedParent = parent.getChainedParent()) != null && !train.contains(nextChainedParent)) {
									train.add(nextChainedParent);
								}

								if(train.contains(cart) || parent.getChainedChild() != null) {
									player.sendMessage(Text.translatable(MOD_ID + ".invalid_chaining").formatted(Formatting.RED), true);
								} else {
									if(cart.getChainedParent() != null) {
										IChainable.unsetChainedParentChild(cart, cart.getChainedParent());
									}
									IChainable.setChainedParentChild(parent, cart);
								}
							} else {
								stack.remove(PARENT_ID);
							}

							world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.BLOCK_CHAIN_PLACE, SoundCategory.NEUTRAL, 1f, 1.1f);

							if(!player.isCreative()) {
								stack.decrement(1);
							}

							stack.remove(PARENT_ID);
						} else {
							stack.set(PARENT_ID, cart.getUuid());
							world.playSound(null, cart.getX(), cart.getY(), cart.getZ(), SoundEvents.BLOCK_CHAIN_HIT, SoundCategory.NEUTRAL, 1f, 1.1f);
						}
					}

					return ActionResult.SUCCESS;
				}
			}

			return ActionResult.PASS;
		});
	}
}