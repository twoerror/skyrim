package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;

public class TESBlockSand extends BlockFalling {
	public TESBlockSand() {
		super(Material.sand);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(0.5f);
		setStepSound(soundTypeSand);
	}
}