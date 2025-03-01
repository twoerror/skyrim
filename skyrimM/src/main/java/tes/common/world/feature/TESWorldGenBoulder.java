package tes.common.world.feature;

import tes.common.util.TESCrashHandler;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESWorldGenBoulder extends WorldGenerator {
	private final Block id;
	private final int meta;
	private final int minWidth;
	private final int maxWidth;
	private int heightCheck = 3;

	public TESWorldGenBoulder(Block i, int j, int k, int l) {
		super(false);
		id = i;
		meta = j;
		minWidth = k;
		maxWidth = l;
	}

	@Override
	@SuppressWarnings("StatementWithEmptyBody")
	public boolean generate(World world, Random random, int i, int j, int k) {
		TESCrashHandler.getBiomeGenForCoords(world, i, k);
		int boulderWidth = MathHelper.getRandomIntegerInRange(random, minWidth, maxWidth);
		int highestHeight = j;
		int lowestHeight = j;
		for (int i1 = i - boulderWidth; i1 <= i + boulderWidth; ++i1) {
			for (int k1 = k - boulderWidth; k1 <= k + boulderWidth; ++k1) {
				int heightValue = world.getHeightValue(i1, k1);
				if (heightValue > highestHeight) {
					highestHeight = heightValue;
				}
				if (heightValue >= lowestHeight) {
					continue;
				}
				lowestHeight = heightValue;
			}
		}
		if (highestHeight - lowestHeight > heightCheck) {
			return false;
		}
		int spheres = 1 + random.nextInt(boulderWidth + 1);
		for (int l = 0; l < spheres; ++l) {
			int posX = i + MathHelper.getRandomIntegerInRange(random, -boulderWidth, boulderWidth);
			int posZ = k + MathHelper.getRandomIntegerInRange(random, -boulderWidth, boulderWidth);
			int posY = world.getTopSolidOrLiquidBlock(posX, posZ);
			int sphereWidth = MathHelper.getRandomIntegerInRange(random, minWidth, maxWidth);
			for (int i1 = posX - sphereWidth; i1 <= posX + sphereWidth; ++i1) {
				for (int j1 = posY - sphereWidth; j1 <= posY + sphereWidth; ++j1) {
					for (int k1 = posZ - sphereWidth; k1 <= posZ + sphereWidth; ++k1) {
						int j3;
						int i2 = i1 - posX;
						int j2 = j1 - posY;
						int k2 = k1 - posZ;
						int dist = i2 * i2 + j2 * j2 + k2 * k2;
						if (dist >= sphereWidth * sphereWidth && (dist >= (sphereWidth + 1) * (sphereWidth + 1) || random.nextInt(3) != 0)) {
							continue;
						}
						for (j3 = j1; j3 >= 0 && !world.getBlock(i1, j3 - 1, k1).isOpaqueCube(); --j3) {
						}
						setBlockAndNotifyAdequately(world, i1, j3, k1, id, meta);
						world.getBlock(i1, j3 - 1, k1).onPlantGrow(world, i1, j3 - 1, k1, i1, j3 - 1, k1);
					}
				}
			}
		}
		return true;
	}

	public TESWorldGenBoulder setHeightCheck(int i) {
		heightCheck = i;
		return this;
	}
}