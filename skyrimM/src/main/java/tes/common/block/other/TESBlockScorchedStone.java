package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockScorchedStone extends Block {
	public TESBlockScorchedStone() {
		super(Material.rock);
		setHardness(2.0f);
		setResistance(10.0f);
		setStepSound(soundTypeStone);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
	}
}