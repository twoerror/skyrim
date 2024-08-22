package tes.common.block.other;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;

public class TESBlockFenceVanilla extends TESBlockFence {
	public TESBlockFenceVanilla() {
		super(Blocks.planks);
		setCreativeTab(CreativeTabs.tabDecorations);
	}
}