package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeaves3 extends TESBlockLeavesBase {
	public TESBlockLeaves3() {
		leafNames = new String[]{"maple", "larch", "date_palm", "mangrove"};
	}

	@Override
	protected void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling3);
	}

	@Override
	public int getSaplingChance(int meta) {
		if (meta == 2) {
			return 15;
		}
		return super.getSaplingChance(meta);
	}
}