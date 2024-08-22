package tes.common.world.genlayer;

import tes.common.TESDimension;
import tes.common.world.biome.TESBiome;
import net.minecraft.world.World;

import java.util.List;

public class TESGenLayerClassicBiomes extends TESGenLayer {
	private final TESDimension dimension;

	public TESGenLayerClassicBiomes(long l, TESGenLayer layer, TESDimension dim) {
		super(l);
		tesParent = layer;
		dimension = dim;
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int[] oceans = tesParent.getInts(world, i, k, xSize, zSize);
		int[] ints = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				int biomeID;
				initChunkSeed(i + i1, k + k1);
				int isOcean = oceans[i1 + k1 * xSize];
				if (isOcean == 1) {
					biomeID = TESBiome.ocean.biomeID;
				} else {
					List<TESBiome> biomeList = dimension.getMajorBiomes();
					int randIndex = nextInt(biomeList.size());
					TESBiome biome = biomeList.get(randIndex);
					biomeID = biome.biomeID;
				}
				ints[i1 + k1 * xSize] = biomeID;
			}
		}
		return ints;
	}
}