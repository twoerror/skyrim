package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.material.Material;

public class TESBlockTrapdoor extends BlockTrapDoor {
	public TESBlockTrapdoor() {
		super(Material.wood);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setStepSound(soundTypeWood);
		setHardness(3.0f);
	}
}