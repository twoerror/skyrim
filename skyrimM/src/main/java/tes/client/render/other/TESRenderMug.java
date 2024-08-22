package tes.client.render.other;

import tes.client.model.*;
import tes.common.database.TESBlocks;
import tes.common.item.other.TESItemMug;
import tes.common.tileentity.TESTileEntityMug;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderMug extends TileEntitySpecialRenderer {
	private static final ResourceLocation MUG_TEXTURE = new ResourceLocation("tes:textures/model/mug.png");
	private static final ResourceLocation MUG_CLAY_TEXTURE = new ResourceLocation("tes:textures/model/mug_clay.png");
	private static final ResourceLocation GOBLET_GOLD_TEXTURE = new ResourceLocation("tes:textures/model/goblet_gold.png");
	private static final ResourceLocation GOBLET_SILVER_TEXTURE = new ResourceLocation("tes:textures/model/goblet_silver.png");
	private static final ResourceLocation GOBLET_COPPER_TEXTURE = new ResourceLocation("tes:textures/model/goblet_copper.png");
	private static final ResourceLocation GOBLET_WOOD_TEXTURE = new ResourceLocation("tes:textures/model/goblet_wood.png");
	private static final ResourceLocation SKULL_TEXTURE = new ResourceLocation("tes:textures/model/skull_cup.png");
	private static final ResourceLocation GLASS_TEXTURE = new ResourceLocation("tes:textures/model/wine_glass.png");
	private static final ResourceLocation BOTTLE_TEXTURE = new ResourceLocation("tes:textures/model/glass_bottle.png");
	private static final ResourceLocation HORN_TEXTURE = new ResourceLocation("tes:textures/model/ale_horn.png");
	private static final ResourceLocation HORN_GOLD_TEXTURE = new ResourceLocation("tes:textures/model/ale_horn_gold.png");

	private static final TESModelAleHorn HORN_MODEL = new TESModelAleHorn();
	private static final RenderBlocks BLOCK_RENDERER = new RenderBlocks();

	private static final ModelBase MUTESEL = new TESModelMug();
	private static final ModelBase GOBLET_MODEL = new TESModelGoblet();
	private static final ModelBase SKULL_MODEL = new TESModelSkullCup();
	private static final ModelBase GLASS_MODEL = new TESModelWineGlass();
	private static final ModelBase BOTTLE_MODEL = new TESModelGlassBottle();

	private static void renderLiquid(IIcon icon, int uvMin, int uvMax, double yMin, double yMax, float scale) {
		double yMin1 = yMin;
		double yMax1 = yMax;
		double edge = 0.001;
		double xzMin = (double) uvMin * scale;
		double xzMax = (double) uvMax * scale;
		float dxz = 0.5f - (uvMin + uvMax) / 2.0f * scale;
		yMin1 = 16.0 - yMin1;
		yMax1 = 16.0 - yMax1;
		yMin1 *= scale;
		yMax1 *= scale;
		GL11.glPushMatrix();
		GL11.glTranslatef(dxz, -0.5f, dxz);
		BLOCK_RENDERER.setOverrideBlockTexture(icon);
		TESRenderBlocks.renderStandardInvBlock(BLOCK_RENDERER, TESBlocks.mug, xzMin += edge, yMax1 - edge, xzMin, xzMax -= edge, yMin1 + edge, xzMax);
		BLOCK_RENDERER.clearOverrideBlockTexture();
		GL11.glPopMatrix();
	}

	private static void renderMeniscus(IIcon icon, int uvMin, int uvMax, double width, double height, float scale) {
		double width1 = width;
		double height1 = height;
		float minU = icon.getInterpolatedU(uvMin);
		float maxU = icon.getInterpolatedU(uvMax);
		float minV = icon.getInterpolatedV(uvMin);
		float maxV = icon.getInterpolatedV(uvMax);
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(-(width1 *= scale), -(height1 *= scale), width1, minU, maxV);
		tessellator.addVertexWithUV(width1, -height1, width1, maxU, maxV);
		tessellator.addVertexWithUV(width1, -height1, -width1, maxU, minV);
		tessellator.addVertexWithUV(-width1, -height1, -width1, minU, minV);
		tessellator.draw();
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		TESTileEntityMug mug = (TESTileEntityMug) tileentity;
		ItemStack mugItemstack = mug.getMugItemForRender();
		Item mugItem = mugItemstack.getItem();
		boolean full = !mug.isEmpty();
		TESItemMug.Vessel vessel = mug.getVessel();
		GL11.glEnable(32826);
		GL11.glDisable(2884);
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5f, (float) d1, (float) d2 + 0.5f);
		GL11.glScalef(-1.0f, -1.0f, 1.0f);
		float mugScale = 0.75f;
		GL11.glScalef(mugScale, mugScale, mugScale);
		float scale = 0.0625f;
		switch (mug.getBlockMetadata()) {
			case 0:
				GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
				break;
			case 1:
				GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				break;
			case 2:
				GL11.glRotatef(270.0f, 0.0f, 1.0f, 0.0f);
				break;
			case 3:
				GL11.glRotatef(0.0f, 0.0f, 1.0f, 0.0f);
		}
		if (vessel == TESItemMug.Vessel.SKULL || vessel == TESItemMug.Vessel.HORN || vessel == TESItemMug.Vessel.HORN_GOLD) {
			GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
		}
		if (full) {
			GL11.glDisable(2896);
			GL11.glPushMatrix();
			bindTexture(TextureMap.locationItemsTexture);
			IIcon liquidIcon = mugItem.getIconFromDamage(-1);
			if (vessel == TESItemMug.Vessel.MUG || vessel == TESItemMug.Vessel.MUG_CLAY) {
				renderMeniscus(liquidIcon, 6, 10, 2.0, 7.0, scale);
			} else if (vessel == TESItemMug.Vessel.GOBLET_GOLD || vessel == TESItemMug.Vessel.GOBLET_SILVER || vessel == TESItemMug.Vessel.GOBLET_COPPER || vessel == TESItemMug.Vessel.GOBLET_WOOD) {
				renderMeniscus(liquidIcon, 6, 9, 1.5, 8.0, scale);
			} else if (vessel == TESItemMug.Vessel.SKULL) {
				renderMeniscus(liquidIcon, 5, 11, 3.0, 9.0, scale);
			} else if (vessel == TESItemMug.Vessel.GLASS) {
				renderLiquid(liquidIcon, 6, 9, 6.0, 9.0, scale);
			} else if (vessel == TESItemMug.Vessel.BOTTLE) {
				renderLiquid(liquidIcon, 6, 10, 1.0, 5.0, scale);
			} else if (vessel == TESItemMug.Vessel.HORN || vessel == TESItemMug.Vessel.HORN_GOLD) {
				HORN_MODEL.prepareLiquid(scale);
				renderMeniscus(liquidIcon, 6, 9, -1.5, 5.0, scale);
			}
			GL11.glPopMatrix();
			GL11.glEnable(2896);
		}
		GL11.glPushMatrix();
		ModelBase model = null;
		if (vessel == TESItemMug.Vessel.MUG) {
			bindTexture(MUG_TEXTURE);
			model = MUTESEL;
		} else if (vessel == TESItemMug.Vessel.MUG_CLAY) {
			bindTexture(MUG_CLAY_TEXTURE);
			model = MUTESEL;
		} else if (vessel == TESItemMug.Vessel.GOBLET_GOLD) {
			bindTexture(GOBLET_GOLD_TEXTURE);
			model = GOBLET_MODEL;
		} else if (vessel == TESItemMug.Vessel.GOBLET_SILVER) {
			bindTexture(GOBLET_SILVER_TEXTURE);
			model = GOBLET_MODEL;
		} else if (vessel == TESItemMug.Vessel.GOBLET_COPPER) {
			bindTexture(GOBLET_COPPER_TEXTURE);
			model = GOBLET_MODEL;
		} else if (vessel == TESItemMug.Vessel.GOBLET_WOOD) {
			bindTexture(GOBLET_WOOD_TEXTURE);
			model = GOBLET_MODEL;
		} else if (vessel == TESItemMug.Vessel.SKULL) {
			bindTexture(SKULL_TEXTURE);
			model = SKULL_MODEL;
		} else if (vessel == TESItemMug.Vessel.GLASS) {
			bindTexture(GLASS_TEXTURE);
			model = GLASS_MODEL;
			GL11.glEnable(2884);
		} else if (vessel == TESItemMug.Vessel.BOTTLE) {
			bindTexture(BOTTLE_TEXTURE);
			model = BOTTLE_MODEL;
			GL11.glEnable(2884);
		} else if (vessel == TESItemMug.Vessel.HORN) {
			bindTexture(HORN_TEXTURE);
			model = HORN_MODEL;
		} else if (vessel == TESItemMug.Vessel.HORN_GOLD) {
			bindTexture(HORN_GOLD_TEXTURE);
			model = HORN_MODEL;
		}
		if (model != null) {
			model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, scale);
		}
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		GL11.glDisable(3042);
		GL11.glEnable(2884);
		GL11.glDisable(32826);
	}
}