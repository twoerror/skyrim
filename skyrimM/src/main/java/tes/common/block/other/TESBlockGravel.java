package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockGravel;

public class TESBlockGravel extends BlockGravel {
	public TESBlockGravel() {
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(0.6f);
		setStepSound(soundTypeGravel);
	}
}