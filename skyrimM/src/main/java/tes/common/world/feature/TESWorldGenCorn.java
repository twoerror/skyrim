package tes.common.world.feature;

import tes.common.database.TESBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESWorldGenCorn extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		for (int l = 0; l < 20; ++l) {
			int k1;
			int j1;
			int i1 = i + random.nextInt(4) - random.nextInt(4);
			Block replace = world.getBlock(i1, j1 = j, k1 = k + random.nextInt(4) - random.nextInt(4));
			if (replace.isReplaceable(world, i1, j1, k1) && !replace.getMaterial().isLiquid()) {
				boolean adjWater = false;
				block1:
				for (int i2 = -1; i2 <= 1; ++i2) {
					for (int k2 = -1; k2 <= 1; ++k2) {
						if (Math.abs(i2) + Math.abs(k2) == 1 && world.getBlock(i1 + i2, j - 1, k1 + k2).getMaterial() == Material.water) {
							adjWater = true;
							break block1;
						}
					}
				}
				if (adjWater) {
					int cornHeight = 2 + random.nextInt(2);
					for (int j2 = 0; j2 < cornHeight; ++j2) {
						if (TESBlocks.cornStalk.canBlockStay(world, i1, j1 + j2, k1)) {
							world.setBlock(i1, j1 + j2, k1, TESBlocks.cornStalk, 0, 2);
						}
					}
				}
			}
		}
		return true;
	}
}