package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockBone extends Block {
	public TESBlockBone() {
		super(Material.rock);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(1.0f);
		setResistance(5.0f);
		setStepSound(soundTypeStone);
	}
}