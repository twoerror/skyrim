package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeaves8 extends TESBlockLeavesBase {
	public TESBlockLeaves8() {
		leafNames = new String[]{"plum", "redwood", "pomegranate", "palm"};
	}

	@Override
	public void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
		if ((meta & 3) == 0 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.plum));
		}
		if ((meta & 3) == 2 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.pomegranate));
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling8);
	}
}