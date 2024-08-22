package tes.common.world.genlayer;

import net.minecraft.world.World;

public class TESGenLayerBiomeVariants extends TESGenLayer {
	public static final int RANDOM_MAX = 10000;

	public TESGenLayerBiomeVariants(long l) {
		super(l);
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int[] ints = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k1 = 0; k1 < zSize; ++k1) {
			for (int i1 = 0; i1 < xSize; ++i1) {
				initChunkSeed(i + i1, k + k1);
				ints[i1 + k1 * xSize] = nextInt(RANDOM_MAX);
			}
		}
		return ints;
	}
}