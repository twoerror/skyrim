package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeaves2 extends TESBlockLeavesBase {
	public TESBlockLeaves2() {
		leafNames = new String[]{"aramant", "beech", "holly", "banana"};
	}

	@Override
	protected void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling2);
	}

	@Override
	public int getSaplingChance(int meta) {
		if (meta == 3) {
			return 9;
		}
		return super.getSaplingChance(meta);
	}
}