package tes.common.world.feature;

import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

public class TESWorldGenBiomeFlowers extends WorldGenerator {
	@Override
	public boolean generate(World world, Random random, int i, int j, int k) {
		BiomeGenBase.FlowerEntry flower = ((TESBiome) TESCrashHandler.getBiomeGenForCoords(world, i, k)).getRandomFlower(random);
		Block block = flower.block;
		int meta = flower.metadata;
		for (int l = 0; l < 64; ++l) {
			int j1;
			int k1;
			int i1 = i + random.nextInt(8) - random.nextInt(8);
			if (!world.isAirBlock(i1, j1 = j + random.nextInt(4) - random.nextInt(4), k1 = k + random.nextInt(8) - random.nextInt(8)) || world.provider.hasNoSky && j1 >= 255 || !block.canBlockStay(world, i1, j1, k1)) {
				continue;
			}
			world.setBlock(i1, j1, k1, block, meta, 2);
		}
		return true;
	}
}