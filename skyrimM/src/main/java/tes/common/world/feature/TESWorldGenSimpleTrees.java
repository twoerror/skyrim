package tes.common.world.feature;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class TESWorldGenSimpleTrees extends WorldGenAbstractTree {
	private final int minHeight;
	private final int maxHeight;
	private final Block woodBlock;
	private final int woodMeta;
	private final Block leafBlock;
	private final int leafMeta;

	public TESWorldGenSimpleTrees(boolean flag, int i, int j, Block k, int l, Block i1, int j1) {
		super(flag);
		minHeight = i;
		maxHeight = j;
		woodBlock = k;
		woodMeta = l;
		leafBlock = i1;
		leafMeta = j1;
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		int height = MathHelper.getRandomIntegerInRange(random, minHeight, maxHeight);
		boolean flag = true;
		if (j >= 1 && j + height + 1 <= 256) {
			int i1;
			int k1;
			for (int j1 = j; j1 <= j + 1 + height; ++j1) {
				int range = 1;
				if (j1 == j) {
					range = 0;
				}
				if (j1 >= j + 1 + height - 2) {
					range = 2;
				}
				for (int i12 = i - range; i12 <= i + range && flag; ++i12) {
					for (int k12 = k - range; k12 <= k + range && flag; ++k12) {
						if (j1 >= 0 && j1 < 256 && isReplaceable(world, i12, j1, k12)) {
							continue;
						}
						flag = false;
					}
				}
			}
			if (!flag) {
				return false;
			}
			boolean flag1 = true;
			for (i1 = i; i1 <= i && flag1; ++i1) {
				for (k1 = k; k1 <= k && flag1; ++k1) {
					Block block = world.getBlock(i1, j - 1, k1);
					if (block.canSustainPlant(world, i1, j - 1, k1, ForgeDirection.UP, (IPlantable) Blocks.sapling)) {
						continue;
					}
					flag1 = false;
				}
			}
			if (flag1) {
				int j1;
				for (i1 = i; i1 <= i; ++i1) {
					for (k1 = k; k1 <= k; ++k1) {
						world.getBlock(i1, j - 1, k1).onPlantGrow(world, i1, j - 1, k1, i1, j, k1);
					}
				}
				int leafStart = 3;
				int leafRangeMin = 0;
				for (j1 = j - leafStart + height; j1 <= j + height; ++j1) {
					int j2 = j1 - (j + height);
					int leafRange = leafRangeMin + 1 - j2 / 2;
					for (int i13 = i - leafRange; i13 <= i + leafRange; ++i13) {
						for (int k13 = k - leafRange; k13 <= k + leafRange; ++k13) {
							int i2 = i13 - i;
							int k2 = k13 - k;
							if (i2 > 0) {
								i2 -= 0;
							}
							if (k2 > 0) {
								k2 -= 0;
							}
							Block block = world.getBlock(i13, j1, k13);
							if (Math.abs(i2) == leafRange && Math.abs(k2) == leafRange && (random.nextInt(2) == 0 || j2 == 0) || !block.isReplaceable(world, i13, j1, k13) && !block.isLeaves(world, i13, j1, k13)) {
								continue;
							}
							setBlockAndNotifyAdequately(world, i13, j1, k13, leafBlock, leafMeta);
						}
					}
				}
				for (j1 = j; j1 < j + height; ++j1) {
					for (int i14 = i; i14 <= i; ++i14) {
						for (int k14 = k; k14 <= k; ++k14) {
							Block block = world.getBlock(i14, j1, k14);
							if (!block.isReplaceable(world, i14, j1, k14) && !block.isLeaves(world, i14, j1, k14)) {
								continue;
							}
							setBlockAndNotifyAdequately(world, i14, j1, k14, woodBlock, woodMeta);
						}
					}
				}
				return true;
			}
		}
		return false;
	}
}