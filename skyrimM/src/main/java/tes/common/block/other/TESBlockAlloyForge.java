package tes.common.block.other;

import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.tileentity.TESTileEntityAlloyForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TESBlockAlloyForge extends TESBlockForgeBase {
	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityAlloyForge();
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		if (!world.isRemote) {
			entityplayer.openGui(TES.instance, TESGuiId.ALLOY_FORGE.ordinal(), world, i, j, k);
		}
		return true;
	}

	@Override
	public boolean useLargeSmoke() {
		return true;
	}
}