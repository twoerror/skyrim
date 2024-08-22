package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.item.ItemSeeds;

public class TESItemSeeds extends ItemSeeds {
	public TESItemSeeds(Block crop, Block soil) {
		super(crop, soil);
		setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
	}
}