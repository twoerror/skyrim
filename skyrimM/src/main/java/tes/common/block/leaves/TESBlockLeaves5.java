package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeaves5 extends TESBlockLeavesBase {
	public TESBlockLeaves5() {
		leafNames = new String[]{"pine", "lemon", "orange", "lime"};
	}

	@Override
	public void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
		if ((meta & 3) == 1 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.lemon));
		}
		if ((meta & 3) == 2 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.orange));
		}
		if ((meta & 3) == 3 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.lime));
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling5);
	}
}