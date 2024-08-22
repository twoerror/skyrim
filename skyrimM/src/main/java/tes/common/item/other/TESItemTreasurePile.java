package tes.common.item.other;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class TESItemTreasurePile extends ItemBlock {
	public TESItemTreasurePile(Block block) {
		super(block);
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	public int getMetadata(int i) {
		return i;
	}
}