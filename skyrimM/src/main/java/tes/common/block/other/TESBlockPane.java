package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockPane;
import net.minecraft.block.material.Material;

public abstract class TESBlockPane extends BlockPane {
	protected TESBlockPane(String s, String s1, Material material, boolean flag) {
		super(s, s1, material, flag);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
	}
}