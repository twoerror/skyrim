package tes.common.block.brick;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockBrickIce extends Block {
	public TESBlockBrickIce() {
		super(Material.packedIce);
		slipperiness = 0.98F;
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(15.0f);
		setResistance(60.0f);
		setStepSound(soundTypeGlass);
	}
}