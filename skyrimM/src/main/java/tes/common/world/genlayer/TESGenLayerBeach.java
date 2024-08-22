package tes.common.world.genlayer;

import tes.common.TESDimension;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.other.TESBiomeBeach;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.stream.IntStream;

public class TESGenLayerBeach extends TESGenLayer {
	private final TESDimension dimension;
	private final BiomeGenBase targetBiome;

	public TESGenLayerBeach(long l, TESGenLayer layer, TESDimension dim, BiomeGenBase target) {
		super(l);
		tesParent = layer;
		dimension = dim;
		targetBiome = target;
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int[] biomes = tesParent.getInts(world, i - 1, k - 1, xSize + 2, zSize + 2);
		int[] ints = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				initChunkSeed(i + i1, k + k1);
				int biomeID = biomes[i1 + 1 + (k1 + 1) * (xSize + 2)];
				TESBiome biome = dimension.getBiomeList()[biomeID];
				int newBiomeID = biomeID;
				if (biomeID != targetBiome.biomeID && biome.getHeightBaseParameter() >= 0.0f) {
					int biome1 = biomes[i1 + 1 + (k1 + 1 - 1) * (xSize + 2)];
					int biome2 = biomes[i1 + 1 + 1 + (k1 + 1) * (xSize + 2)];
					int biome3 = biomes[i1 + 1 - 1 + (k1 + 1) * (xSize + 2)];
					int biome4 = biomes[i1 + 1 + (k1 + 1 + 1) * (xSize + 2)];
					//noinspection StreamToLoop
				}
				ints[i1 + k1 * xSize] = newBiomeID;
			}
		}
		return ints;
	}
}