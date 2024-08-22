package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeavesFruit extends TESBlockLeavesBase {
	public TESBlockLeavesFruit() {
		leafNames = new String[]{"apple", "pear", "cherry", "mango"};
	}

	@Override
	public void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
		if ((meta & 3) == 0 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			if (world.rand.nextBoolean()) {
				drops.add(new ItemStack(Items.apple));
			} else {
				drops.add(new ItemStack(TESItems.appleGreen));
			}
		}
		if ((meta & 3) == 1 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.pear));
		}
		if ((meta & 3) == 2 && world.rand.nextInt(calcFortuneModifiedDropChance(8, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.cherry));
		}
		if ((meta & 3) == 3 && world.rand.nextInt(calcFortuneModifiedDropChance(16, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.mango));
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.fruitSapling);
	}
}