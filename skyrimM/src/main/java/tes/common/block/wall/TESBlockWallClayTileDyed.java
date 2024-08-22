package tes.common.block.wall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.util.IIcon;

public class TESBlockWallClayTileDyed extends TESBlockWallBase {
	public TESBlockWallClayTileDyed() {
		super(TESBlocks.clayTileDyed, 16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return TESBlocks.clayTileDyed.getIcon(i, j);
	}
}