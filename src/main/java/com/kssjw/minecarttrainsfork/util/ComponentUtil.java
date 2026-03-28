package com.kssjw.minecarttrainsfork.util;

import java.util.UUID;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ComponentUtil {

    private ComponentUtil() {}
    
    public static final DataComponentType<UUID> PARENT_ID = DataComponentType.<UUID>builder().persistent(
		RecordCodecBuilder.create(uuidInstance -> uuidInstance.group(
			Codec.LONG.fieldOf("most_sig_bits").forGetter(UUID::getMostSignificantBits),
			Codec.LONG.fieldOf("least_sig_bits").forGetter(UUID::getLeastSignificantBits)
		).apply(uuidInstance, UUID::new))
	).networkSynchronized(
		StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, UUID::getMostSignificantBits,
			ByteBufCodecs.VAR_LONG, UUID::getLeastSignificantBits,
			UUID::new
		)
	).build();
}