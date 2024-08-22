package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeaves6 extends TESBlockLeavesBase {
	public TESBlockLeaves6() {
		leafNames = new String[]{"mahogany", "willow", "cypress", "olive"};
	}

	@Override
	public void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
		if ((meta & 3) == 3 && world.rand.nextInt(calcFortuneModifiedDropChance(10, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.olive));
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling6);
	}
}