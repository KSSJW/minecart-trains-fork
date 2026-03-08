package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public class ComponentUtil {

    private ComponentUtil() {}
    
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
}