package tes.client.render.other;

import tes.common.database.TESItems;
import tes.common.entity.other.inanimate.TESEntityTraderRespawn;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderTraderRespawn extends Render {
	private ItemStack theItemStack;

	@SuppressWarnings("StatementWithEmptyBody")
	private static float interpolateRotation(float prevRotation, float newRotation, float tick) {
		float interval;
		for (interval = newRotation - prevRotation; interval < -180.0f; interval += 360.0f) {
		}
		while (interval >= 180.0f) {
			interval -= 360.0f;
		}
		return prevRotation + tick * interval;
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		if (theItemStack == null) {
			theItemStack = new ItemStack(TESItems.coin, 1, 6);
		}
		TESEntityTraderRespawn traderRespawn = (TESEntityTraderRespawn) entity;
		bindEntityTexture(traderRespawn);
		GL11.glPushMatrix();
		GL11.glEnable(32826);
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		float rotation = interpolateRotation(traderRespawn.getPrevSpawnerSpin(), traderRespawn.getSpawnerSpin(), f1);
		float scale = traderRespawn.getScaleFloat(f1);
		GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-0.5f * scale, traderRespawn.getBobbingOffset(f1), 0.03125f * scale);
		GL11.glScalef(scale, scale, scale);
		IIcon icon = theItemStack.getIconIndex();
		if (icon == null) {
			icon = ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationItemsTexture)).getAtlasSprite("missingno");
		}
		Tessellator tessellator = Tessellator.instance;
		float f2 = icon.getMinU();
		float f3 = icon.getMaxU();
		float f4 = icon.getMinV();
		float f5 = icon.getMaxV();
		ItemRenderer.renderItemIn2D(tessellator, f3, f4, f2, f5, icon.getIconWidth(), icon.getIconHeight(), 0.0625f);
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TextureMap.locationItemsTexture;
	}
}