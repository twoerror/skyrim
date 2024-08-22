package tes.common.world.genlayer;

import tes.common.world.biome.TESBiome;
import net.minecraft.world.World;

public class TESGenLayerBiomeSubtypes extends TESGenLayer {
	private final TESGenLayer biomeLayer;
	private final TESGenLayer variantsLayer;

	public TESGenLayerBiomeSubtypes(long l, TESGenLayer biomes, TESGenLayer subtypes) {
		super(l);
		biomeLayer = biomes;
		variantsLayer = subtypes;
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int[] biomes = biomeLayer.getInts(world, i, k, xSize, zSize);
		int[] variants = variantsLayer.getInts(world, i, k, xSize, zSize);
		int[] ints = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				initChunkSeed(i + i1, k + k1);
				int biome = biomes[i1 + k1 * xSize];
				int variant = variants[i1 + k1 * xSize];
				int newBiome = biome;
				boolean areNotEqual = newBiome != biome;
				ints[i1 + k1 * xSize] = areNotEqual ? newBiome : biome;
			}
		}
		return ints;
	}

	@Override
	public void initWorldGenSeed(long l) {
		biomeLayer.initWorldGenSeed(l);
		variantsLayer.initWorldGenSeed(l);
		super.initWorldGenSeed(l);
	}
}