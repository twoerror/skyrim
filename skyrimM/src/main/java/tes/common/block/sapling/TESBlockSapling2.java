package tes.common.block.sapling;

import tes.common.world.feature.TESTreeType;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class TESBlockSapling2 extends TESBlockSaplingBase {
	public TESBlockSapling2() {
		saplingNames = new String[]{"aramant", "beech", "holly", "banana"};
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
		switch (meta) {
			case 0:
				treeGen = TESTreeType.ARAMANT.create(true, random);
				break;
			case 1:
				int[] partyTree = findPartyTree(world, i, j, k, this, meta);
				if (partyTree != null) {
					treeGen = TESTreeType.BEECH_PARTY.create(true, random);
					trunkPos = 1;
					trunkNeg = 1;
					xOffset = partyTree[0];
					zOffset = partyTree[1];
				}
				if (treeGen == null) {
					treeGen = (random.nextInt(10) == 0 ? TESTreeType.BEECH_LARGE : TESTreeType.BEECH).create(true, random);
					trunkPos = 0;
					trunkNeg = 0;
					xOffset = 0;
					zOffset = 0;
				}
				break;
			case 2:
				for (int i12 = 0; i12 >= -1; --i12) {
					for (k1 = 0; k1 >= -1; --k1) {
						if (!isSameSapling(world, i + i12, j, k + k1, meta) || !isSameSapling(world, i + i12 + 1, j, k + k1, meta) || !isSameSapling(world, i + i12, j, k + k1 + 1, meta) || !isSameSapling(world, i + i12 + 1, j, k + k1 + 1, meta)) {
							continue;
						}
						treeGen = TESTreeType.HOLLY_LARGE.create(true, random);
						trunkNeg = 0;
						trunkPos = 1;
						xOffset = i12;
						zOffset = k1;
						break;
					}
					if (treeGen != null) {
						break;
					}
				}
				if (treeGen == null) {
					xOffset = 0;
					zOffset = 0;
					treeGen = TESTreeType.HOLLY.create(true, random);
				}
				break;
			case 3:
				treeGen = TESTreeType.BANANA.create(true, random);
				break;
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