package tes.common.block.wall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.util.IIcon;

public class TESBlockWallClayTile extends TESBlockWallBase {
	public TESBlockWallClayTile() {
		super(TESBlocks.clayTile, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return TESBlocks.clayTile.getIcon(i, j);
	}
}