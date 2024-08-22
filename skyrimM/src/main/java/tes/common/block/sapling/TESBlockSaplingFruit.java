package tes.common.block.sapling;

import tes.common.world.feature.TESTreeType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class TESBlockSaplingFruit extends TESBlockSaplingBase {
	public TESBlockSaplingFruit() {
		saplingNames = new String[]{"apple", "pear", "cherry", "mango"};
	}

	@Override
	public void growTree(World world, int i, int j, int k, Random random) {
		int meta = world.getBlockMetadata(i, j, k) & 7;
		WorldGenAbstractTree treeGen = null;
		switch (meta) {
			case 0:
				treeGen = TESTreeType.APPLE.create(true, random);
				break;
			case 1:
				treeGen = TESTreeType.PEAR.create(true, random);
				break;
			case 2:
				treeGen = TESTreeType.CHERRY.create(true, random);
				break;
			case 3:
				treeGen = TESTreeType.MANGO.create(true, random);
				break;
		}
		world.setBlock(i, j, k, Blocks.air, 0, 4);
		if (treeGen != null && !treeGen.generate(world, random, i, j, k)) {
			world.setBlock(i, j, k, this, meta, 4);
		}
	}
}