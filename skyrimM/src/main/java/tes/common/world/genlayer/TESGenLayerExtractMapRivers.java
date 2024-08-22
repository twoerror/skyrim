package tes.common.world.genlayer;

import tes.common.world.biome.TESBiome;
import net.minecraft.world.World;

public class TESGenLayerExtractMapRivers extends TESGenLayer {
	public TESGenLayerExtractMapRivers(long l, TESGenLayer layer) {
		super(l);
		tesParent = layer;
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int[] biomes = tesParent.getInts(world, i, k, xSize, zSize);
		int[] ints = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				initChunkSeed(i + i1, k + k1);
				int biomeID = biomes[i1 + k1 * xSize];
				ints[i1 + k1 * xSize] = biomeID == TESBiome.river.biomeID ? 2 : 0;
			}
		}
		return ints;
	}
}