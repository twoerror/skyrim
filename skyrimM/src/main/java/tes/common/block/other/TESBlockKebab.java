package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockKebab extends Block {
	public TESBlockKebab() {
		super(Material.sand);
		setCreativeTab(TESCreativeTabs.TAB_FOOD);
		setHardness(0.5f);
		setStepSound(soundTypeWood);
	}
}