package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;

public class TESBlockPressurePlate extends BlockPressurePlate {
	public TESBlockPressurePlate(String iconPath, Material material, BlockPressurePlate.Sensitivity triggerType) {
		super(iconPath, material, triggerType);
		setCreativeTab(TESCreativeTabs.TAB_MISC);
	}
}