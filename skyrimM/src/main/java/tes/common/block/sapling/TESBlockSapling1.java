package tes.common.block.sapling;

import tes.common.world.feature.TESTreeType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class TESBlockSapling1 extends TESBlockSaplingBase {
	public TESBlockSapling1() {
		saplingNames = new String[]{"ibbinia", "catalpa", "ulthos", "ulthos_red"};
	}

	@Override
	public void growTree(World world, int i, int j, int k, Random random) {
		int k1;
		int i1;
		int meta = world.getBlockMetadata(i, j, k) & 7;
		WorldGenAbstractTree treeGen = null;
		int trunkNeg = 0;
		int trunkPos = 0;
		int xOffset = 0;
		int zOffset = 0;
		if (meta == 0) {
			treeGen = TESTreeType.IBBEN_PINE.create(true, random);
		}
		if (meta == 1) {
			int[] partyTree = findPartyTree(world, i, j, k, this, 1);
			if (partyTree != null) {
				treeGen = TESTreeType.CATALPA_PARTY.create(true, random);
				trunkPos = 1;
				trunkNeg = 1;
				xOffset = partyTree[0];
				zOffset = partyTree[1];
			}
			if (treeGen == null) {
				trunkPos = 0;
				trunkNeg = 0;
				xOffset = 0;
				zOffset = 0;
				treeGen = TESTreeType.CATALPA.create(true, random);
			}
		}
		if (meta == 2) {
			int[] partyTree = findPartyTree(world, i, j, k, this, 2);
			if (partyTree != null) {
				treeGen = TESTreeType.ULTHOS_OAK_LARGE.create(true, random);
				trunkPos = 1;
				trunkNeg = 1;
				xOffset = partyTree[0];
				zOffset = partyTree[1];
			}
			if (treeGen == null) {
				treeGen = TESTreeType.ULTHOS_OAK.create(true, random);
				trunkPos = 0;
				trunkNeg = 0;
				xOffset = 0;
				zOffset = 0;
			}
		}
		if (meta == 3) {
			int[] partyTree = findPartyTree(world, i, j, k, this, 3);
			if (partyTree != null) {
				treeGen = TESTreeType.ULTHOS_RED_OAK_LARGE.create(true, random);
				trunkPos = 1;
				trunkNeg = 1;
				xOffset = partyTree[0];
				zOffset = partyTree[1];
			}
			if (treeGen == null) {
				treeGen = TESTreeType.ULTHOS_RED_OAK.create(true, random);
				trunkPos = 0;
				trunkNeg = 0;
				xOffset = 0;
				zOffset = 0;
			}
		}
		for (i1 = -trunkNeg; i1 <= trunkPos; ++i1) {
			for (k1 = -trunkNeg; k1 <= trunkPos; ++k1) {
				world.setBlock(i + xOffset + i1, j, k + zOffset + k1, Blocks.air, 0, 4);
			}
		}
		if (treeGen != null && !treeGen.generate(world, random, i + xOffset, j, k + zOffset)) {
			for (i1 = -trunkNeg; i1 <= trunkPos; ++i1) {
				for (k1 = -trunkNeg; k1 <= trunkPos; ++k1) {
					world.setBlock(i + xOffset + i1, j, k + zOffset + k1, this, meta, 4);
				}
			}
		}
	}
}