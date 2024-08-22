package tes.client.model;

import tes.client.TESTickHandlerClient;
import tes.client.render.other.TESRenderBlocks;
import tes.common.database.TESBlocks;
import tes.common.entity.other.utils.TESPlateFallingInfo;
import tes.common.item.other.TESItemPlate;
import tes.common.tileentity.TESTileEntityPlate;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

public class TESModelPlateHead extends TESModelHuman {
	private static final RenderBlocks BLOCK_RENDERER = new RenderBlocks();
	private static final TESTileEntityPlate PLATE_TE = new TESTileEntityPlate();

	private Block plateBlock;

	@Override
	public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5) {
		ItemStack heldItem;
		float tick = TESTickHandlerClient.getRenderTick();
		setRotationAngles(f, f1, f2, f3, f4, f5, entity);
		TESPlateFallingInfo fallingInfo = entity == null ? null : TESPlateFallingInfo.getOrCreateFor(entity, false);
		float fallOffset = fallingInfo == null ? 0.0f : fallingInfo.getPlateOffsetY(tick);
		GL11.glEnable(32826);
		GL11.glPushMatrix();
		GL11.glScalef(1.0f, -1.0f, 1.0f);
		GL11.glRotatef(f3, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(0.0f, 1.0f - bipedHead.rotationPointY / 16.0f, 0.0f);
		GL11.glTranslatef(0.0f, fallOffset * 0.5f, 0.0f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
		TESRenderBlocks.renderEntityPlate(plateBlock, BLOCK_RENDERER);
		if (entity instanceof EntityLivingBase && (heldItem = ((EntityLivingBase) entity).getHeldItem()) != null && TESTileEntityPlate.isValidFoodItem(heldItem)) {
			ItemStack heldItemMinusOne = heldItem.copy();
			--heldItemMinusOne.stackSize;
			if (heldItemMinusOne.stackSize > 0) {
				PLATE_TE.setFoodItem(heldItemMinusOne);
				PLATE_TE.setPlateFallInfo(fallingInfo);
				TileEntityRendererDispatcher.instance.renderTileEntityAt(PLATE_TE, -0.5, -0.5, -0.5, tick);
				PLATE_TE.setPlateFallInfo(null);
				GL11.glDisable(2884);
			}
		}
		GL11.glPopMatrix();
		GL11.glDisable(32826);
	}

	public void setPlateItem(ItemStack itemstack) {
		plateBlock = itemstack.getItem() instanceof TESItemPlate ? ((TESItemPlate) itemstack.getItem()).getPlateBlock() : TESBlocks.plate;
	}
}