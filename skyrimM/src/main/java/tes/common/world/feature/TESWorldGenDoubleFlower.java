package tes.common.world.feature;

import tes.common.database.TESBlocks;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESWorldGenDoubleFlower extends WorldGenerator {
	private int flowerType;

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		boolean flag = false;
		for (int l = 0; l < 64; ++l) {
			int j1;
			int k1;
			int i1 = i + random.nextInt(8) - random.nextInt(8);
			if (!world.isAirBlock(i1, j1 = j + random.nextInt(4) - random.nextInt(4), k1 = k + random.nextInt(8) - random.nextInt(8)) || world.provider.hasNoSky && j1 >= 254 || !TESBlocks.doubleFlower.canPlaceBlockAt(world, i1, j1, k1)) {
				continue;
			}
			((BlockDoublePlant) TESBlocks.doubleFlower).func_149889_c(world, i1, j1, k1, flowerType, 2);
			flag = true;
		}
		return flag;
	}

	public void setFlowerType(int i) {
		flowerType = i;
	}
}