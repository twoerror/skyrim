package tes.common.block.other;

import net.minecraft.item.Item;

import java.util.Random;

public class TESBlockObsidianGravel extends TESBlockGravel {
	@Override
	public Item getItemDropped(int i, Random rand, int j) {
		return Item.getItemFromBlock(this);
	}
}