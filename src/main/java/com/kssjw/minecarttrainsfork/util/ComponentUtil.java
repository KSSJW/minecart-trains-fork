package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.kssjw.minecarttrainsfork.MinecartTrainsFork;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ComponentUtil {

    private ComponentUtil() {}

	public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(MinecartTrainsFork.MOD_ID);

	public static final DeferredHolder<DataComponentType<?>, DataComponentType<UUID>> PARENT_ID =
		COMPONENTS.register("parent_id", () ->
			DataComponentType.<UUID>builder()
				.persistent(ComponentUtil.UUID_CODEC)
				.networkSynchronized(ComponentUtil.UUID_STREAM_CODEC)
				.build()
		);

	public static final Codec<UUID> UUID_CODEC =
        RecordCodecBuilder.create(instance -> instance.group(
            Codec.LONG.fieldOf("most_sig_bits").forGetter(UUID::getMostSignificantBits),
            Codec.LONG.fieldOf("least_sig_bits").forGetter(UUID::getLeastSignificantBits)
        ).apply(instance, UUID::new));

    public static final StreamCodec<FriendlyByteBuf, UUID> UUID_STREAM_CODEC =
        StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, UUID::getMostSignificantBits,
            ByteBufCodecs.VAR_LONG, UUID::getLeastSignificantBits,
            UUID::new
        );

    public static void register(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}