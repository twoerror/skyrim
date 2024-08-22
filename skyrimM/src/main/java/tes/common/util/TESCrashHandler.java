package tes.common.util;

import tes.common.world.biome.TESBiome;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;

public class TESCrashHandler {
	private TESCrashHandler() {
	}

	public static BiomeGenBase getBiomeGenForCoords(IBlockAccess world, int i, int k) {
		try {
			BiomeGenBase biome = world.getBiomeGenForCoords(i, k);
			if (biome == null) {
				return TESBiome.ocean;
			}
			return biome;
		} catch (Exception e) {
			return TESBiome.ocean;
		}
	}

	@SuppressWarnings("TypeMayBeWeakened")
	public static BiomeGenBase getBiomeGenForCoords(World world, int i, int k) {
		try {
			BiomeGenBase biome = world.getBiomeGenForCoords(i, k);
			if (biome == null) {
				return TESBiome.ocean;
			}
			return biome;
		} catch (Exception e) {
			return TESBiome.ocean;
		}
	}

	public static BiomeGenBase getBiomeGenForCoords(WorldProvider world, int i, int k) {
		try {
			BiomeGenBase biome = world.getBiomeGenForCoords(i, k);
			if (biome == null) {
				return TESBiome.ocean;
			}
			return biome;
		} catch (Exception e) {
			return TESBiome.ocean;
		}
	}
}