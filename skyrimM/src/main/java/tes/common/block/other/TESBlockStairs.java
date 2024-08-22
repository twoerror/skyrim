package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;

public class TESBlockStairs extends BlockStairs {
	public TESBlockStairs(Block block, int metadata) {
		super(block, metadata);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		useNeighborBrightness = true;
	}
}