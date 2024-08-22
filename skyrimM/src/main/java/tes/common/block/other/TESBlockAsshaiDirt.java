package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockAsshaiDirt extends Block {
	public TESBlockAsshaiDirt() {
		super(Material.ground);
		setHardness(0.5f);
		setStepSound(soundTypeGravel);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
	}
}