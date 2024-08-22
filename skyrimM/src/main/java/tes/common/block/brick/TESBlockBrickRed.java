package tes.common.block.brick;

import net.minecraft.creativetab.CreativeTabs;

public class TESBlockBrickRed extends TESBlockBrickBase {
	public TESBlockBrickRed() {
		brickNames = new String[]{"mossy", "cracked"};
		setCreativeTab(CreativeTabs.tabBlock);
		setHardness(2.0f);
		setResistance(10.0f);
	}
}