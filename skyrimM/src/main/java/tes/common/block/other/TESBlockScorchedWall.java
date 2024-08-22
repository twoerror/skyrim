package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.block.wall.TESBlockWallBase;
import tes.common.database.TESBlocks;
import net.minecraft.util.IIcon;

public class TESBlockScorchedWall extends TESBlockWallBase {
	public TESBlockScorchedWall() {
		super(TESBlocks.scorchedStone, 1);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return TESBlocks.scorchedStone.getIcon(i, j);
	}
}