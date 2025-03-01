package tes.common.world.biome.variant;

import com.google.common.math.IntMath;
import tes.common.world.map.TESBeziers;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

import java.util.Random;

public class TESBiomeVariantOrchard extends TESBiomeVariant {
	public TESBiomeVariantOrchard(int i, String s) {
		super(i, s);
		setHeight(0.0f, 0.4f);
		setTrees(0.0f);
		disableSettlements();
	}

	@Override
	public void decorateVariant(World world, Random random, int i, int k) {
		for (int i1 : new int[]{i + 3, i + 11}) {
			int k1 = k + 8;
			int j1 = world.getHeightValue(i1, k1);
			WorldGenAbstractTree treeGen = getRandomTree(random).create(false, random);
			treeGen.generate(world, random, i1, j1, k1);
		}
	}

	@Override
	public void generateVariantTerrain(Block[] blocks, byte[] meta, int i, int k) {
		int chunkX = i & 0xF;
		int chunkZ = k & 0xF;
		int xzIndex = chunkX * 16 + chunkZ;
		int ySize = blocks.length / 256;
		boolean roadAt = TESBeziers.isBezierAt(i, k, TESBeziers.Type.ROAD);
		boolean wallAt = TESBeziers.isBezierAt(i, k, TESBeziers.Type.WALL);
		boolean linkerAt = TESBeziers.isBezierAt(i, k, TESBeziers.Type.LINKER);
		if (!roadAt && !wallAt && !linkerAt) {
			for (int j = 128; j >= 0; --j) {
				int index = xzIndex * ySize + j;
				Block above = blocks[index + 1];
				Block block = blocks[index];
				if (!block.isOpaqueCube() || above.getMaterial() != Material.air) {
					continue;
				}
				int i1 = IntMath.mod(i, 32);
				int k1 = IntMath.mod(k, 16);
				if (i1 == 6 || i1 == 7 || i1 == 8 || k1 != 0) {
					break;
				}
				blocks[index + 1] = Blocks.fence;
				meta[index + 1] = 0;
				break;
			}
		}
	}
}