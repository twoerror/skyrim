package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockClayTile extends Block {
	public TESBlockClayTile() {
		super(Material.rock);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(1.25f);
		setResistance(7.0f);
		setStepSound(soundTypeStone);
	}
}