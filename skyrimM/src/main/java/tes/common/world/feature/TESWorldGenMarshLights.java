package tes.common.world.feature;

import tes.common.database.TESBlocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESWorldGenMarshLights extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		for (int l = 0; l < 4; ++l) {
			int j1;
			int k1;
			int i1 = i + random.nextInt(8) - random.nextInt(8);
			if (!world.isAirBlock(i1, j1 = j, k1 = k + random.nextInt(8) - random.nextInt(8)) || !TESBlocks.marshLights.canPlaceBlockAt(world, i1, j1, k1)) {
				continue;
			}
			world.setBlock(i1, j1, k1, TESBlocks.marshLights, 0, 2);
		}
		return true;
	}
}