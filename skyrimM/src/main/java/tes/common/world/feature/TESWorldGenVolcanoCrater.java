package tes.common.world.feature;

import tes.common.database.TESBlocks;
import tes.common.util.TESCrashHandler;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESWorldGenVolcanoCrater extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		TESCrashHandler.getBiomeGenForCoords(world, i, k);
		int maxWidth = 15;
		int minWidth = 5;
		int craterWidth = MathHelper.getRandomIntegerInRange(random, minWidth, maxWidth);
		int highestHeight = j;
		int lowestHeight = j;
		for (int i1 = i - craterWidth; i1 <= i + craterWidth; ++i1) {
			for (int k1 = k - craterWidth; k1 <= k + craterWidth; ++k1) {
				int heightValue = world.getHeightValue(i1, k1);
				int j1 = heightValue - 1;
				if (heightValue > highestHeight) {
					highestHeight = heightValue;
				}
				if (heightValue >= lowestHeight) {
					continue;
				}
				lowestHeight = heightValue;
			}
		}
		int heightCheck = 8;
		if (highestHeight - lowestHeight > heightCheck) {
			return false;
		}
		int spheres = 1;
		for (int l = 0; l < spheres; ++l) {
			int posY = world.getTopSolidOrLiquidBlock(i, k);
			int sphereWidth = MathHelper.getRandomIntegerInRange(random, minWidth, maxWidth);
			for (int i1 = i - sphereWidth; i1 <= i + sphereWidth; ++i1) {
				for (int k1 = k - sphereWidth; k1 <= k + sphereWidth; ++k1) {
					int j1;
					int i2 = i1 - i;
					int k2 = k1 - k;
					int xzDistSq = i2 * i2 + k2 * k2;
					if (xzDistSq >= sphereWidth * sphereWidth && (xzDistSq >= (sphereWidth + 1) * (sphereWidth + 1) || random.nextInt(3) != 0)) {
						continue;
					}
					for (int j2 = world.getTopSolidOrLiquidBlock(i1, k1); j2 > posY; --j2) {
						setBlockAndNotifyAdequately(world, i1, j2, k1, Blocks.air, 0);
					}
					int depthHere = (int) ((sphereWidth - Math.sqrt(xzDistSq)) * 0.7) + random.nextInt(2);
					for (j1 = posY - depthHere - 1; j1 >= posY - (depthHere + heightCheck + 2 + random.nextInt(2)) && !world.getBlock(i1, j1, k1).isOpaqueCube(); --j1) {
						setBlockAndNotifyAdequately(world, i1, j1, k1, Blocks.stone, 0);
					}
					for (j1 = posY; j1 >= posY - depthHere; --j1) {
						int jDepth = posY - j1;
						if (jDepth > 6) {
							setBlockAndNotifyAdequately(world, i1, j1, k1, Blocks.lava, 0);
							if (!world.getBlock(i1, j1 - 1, k1).isOpaqueCube()) {
								setBlockAndNotifyAdequately(world, i1, j1 - 1, k1, Blocks.obsidian, 0);
							}
						} else {
							setBlockAndNotifyAdequately(world, i1, j1, k1, Blocks.air, 0);
						}
						if (jDepth > 4) {
							setBlockAndNotifyAdequately(world, i1, j1 - 1, k1, Blocks.obsidian, 0);
							continue;
						}
						if (jDepth <= 2) {
							continue;
						}
						if (random.nextInt(4) == 0) {
							setBlockAndNotifyAdequately(world, i1, j1 - 1, k1, Blocks.gravel, 0);
							setBlockAndNotifyAdequately(world, i1, j1 - 2, k1, Blocks.stone, 0);
							continue;
						}
						setBlockAndNotifyAdequately(world, i1, j1 - 1, k1, TESBlocks.obsidianGravel, 0);
						setBlockAndNotifyAdequately(world, i1, j1 - 2, k1, Blocks.obsidian, 0);
					}
				}
			}
		}
		return true;
	}
}