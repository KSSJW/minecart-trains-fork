package com.kssjw.minecarttrainsfork.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;

public class CompatUtil {
    
    private CompatUtil() {}

	public static long getLongCompat(NbtCompound nbt, String key, long defaultValue) {
		if (nbt.contains(key, NbtElement.LONG_TYPE)) {
			return nbt.getLong(key);
		} else {
			return defaultValue;
		}
	}

    public static int getIntCompat(NbtCompound nbt, String key, int defaultValue) {
		if (nbt.contains(key, NbtElement.LONG_TYPE)) {
			return nbt.getInt(key);
		} else {
			return defaultValue;
		}
	}
}
