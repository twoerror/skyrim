package tes.common.block.wall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.util.IIcon;

public class TESBlockWallBone extends TESBlockWallBase {
	public TESBlockWallBone() {
		super(TESBlocks.boneBlock, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (j == 0) {
			return TESBlocks.boneBlock.getIcon(i, 0);
		}
		return super.getIcon(i, j);
	}
}