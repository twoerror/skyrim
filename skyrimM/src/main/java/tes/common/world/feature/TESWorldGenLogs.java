package tes.common.world.feature;

import tes.common.database.TESBlocks;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

public class TESWorldGenLogs extends WorldGenerator {
	public TESWorldGenLogs() {
		super(false);
	}

	private static boolean isSuitablePositionForLog(IBlockAccess world, int i, int j, int k) {
		return world.getBlock(i, j - 1, k).canSustainPlant(world, i, j - 1, k, ForgeDirection.UP, (IPlantable) Blocks.sapling) && world.getBlock(i, j, k).isReplaceable(world, i, j, k);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		if (!isSuitablePositionForLog(world, i, j, k)) {
			return false;
		}
		int logType = random.nextInt(5);
		if (logType == 0) {
			int length = 2 + random.nextInt(6);
			for (int i1 = i; i1 < i + length && isSuitablePositionForLog(world, i1, j, k); ++i1) {
				setBlockAndNotifyAdequately(world, i1, j, k, TESBlocks.rottenLog, 4);
				world.getBlock(i1, j - 1, k).onPlantGrow(world, i1, j - 1, k, i1, j, k);
			}
			return true;
		}
		if (logType == 1) {
			int length = 2 + random.nextInt(6);
			for (int k1 = k; k1 < k + length && isSuitablePositionForLog(world, i, j, k1); ++k1) {
				setBlockAndNotifyAdequately(world, i, j, k1, TESBlocks.rottenLog, 8);
				world.getBlock(i, j - 1, k1).onPlantGrow(world, i, j - 1, k1, i, j, k1);
			}
			return true;
		}
		setBlockAndNotifyAdequately(world, i, j, k, TESBlocks.rottenLog, 0);
		world.getBlock(i, j - 1, k).onPlantGrow(world, i, j - 1, k, i, j, k);
		return true;
	}
}