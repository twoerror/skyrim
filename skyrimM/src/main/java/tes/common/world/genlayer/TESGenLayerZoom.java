package tes.common.world.genlayer;

import net.minecraft.world.World;

public class TESGenLayerZoom extends TESGenLayer {
	public TESGenLayerZoom(long l, TESGenLayer layer) {
		super(l);
		tesParent = layer;
	}

	public static TESGenLayer magnify(long seed, TESGenLayer source, int zooms) {
		TESGenLayer layer = source;
		for (int i = 0; i < zooms; ++i) {
			layer = new TESGenLayerZoom(seed + i, layer);
		}
		return layer;
	}

	@Override
	public int[] getInts(World world, int i, int k, int xSize, int zSize) {
		int i1 = i >> 1;
		int k1 = k >> 1;
		int xSizeZoom = (xSize >> 1) + 2;
		int zSizeZoom = (zSize >> 1) + 2;
		int[] variants = tesParent.getInts(world, i1, k1, xSizeZoom, zSizeZoom);
		int i2 = xSizeZoom - 1 << 1;
		int k2 = zSizeZoom - 1 << 1;
		int[] ints = TESIntCache.get(world).getIntArray(i2 * k2);
		for (int k3 = 0; k3 < zSizeZoom - 1; ++k3) {
			for (int i3 = 0; i3 < xSizeZoom - 1; ++i3) {
				initChunkSeed((long) i3 + i1 << 1, (long) k3 + k1 << 1);
				int int00 = variants[i3 + k3 * xSizeZoom];
				int int01 = variants[i3 + (k3 + 1) * xSizeZoom];
				int int10 = variants[i3 + 1 + k3 * xSizeZoom];
				int int11 = variants[i3 + 1 + (k3 + 1) * xSizeZoom];
				int index = (i3 << 1) + (k3 << 1) * i2;
				ints[index] = int00;
				ints[index + 1] = selectRandom(int00, int10);
				ints[index + i2] = selectRandom(int00, int01);
				ints[index + i2 + 1] = selectModeOrRandom(int00, int10, int01, int11);
			}
		}
		int[] zoomedInts = TESIntCache.get(world).getIntArray(xSize * zSize);
		for (int k3 = 0; k3 < zSize; ++k3) {
			System.arraycopy(ints, (k3 + (k & 1)) * i2 + (i & 1), zoomedInts, k3 * xSize, xSize);
		}
		return zoomedInts;
	}
}