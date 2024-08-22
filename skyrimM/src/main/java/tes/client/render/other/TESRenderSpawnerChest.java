package tes.client.render.other;

import tes.common.block.other.TESBlockSpawnerChest;
import tes.common.tileentity.TESTileEntitySpawnerChest;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TESRenderSpawnerChest extends TileEntitySpecialRenderer {
	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		TESTileEntitySpawnerChest chest = (TESTileEntitySpawnerChest) tileentity;
		Block block = chest.getBlockType();
		if (block instanceof TESBlockSpawnerChest) {
			TESBlockSpawnerChest scBlock = (TESBlockSpawnerChest) block;
			Block model = scBlock.getChestModel();
			if (model instanceof ITileEntityProvider) {
				ITileEntityProvider itep = (ITileEntityProvider) model;
				TileEntity modelTE = itep.createNewTileEntity(chest.getWorldObj(), 0);
				modelTE.setWorldObj(chest.getWorldObj());
				modelTE.xCoord = chest.xCoord;
				modelTE.yCoord = chest.yCoord;
				modelTE.zCoord = chest.zCoord;
				TileEntityRendererDispatcher.instance.getSpecialRenderer(modelTE).renderTileEntityAt(modelTE, d, d1, d2, f);
			}
		}
	}
}