package tes.client.gui;

import tes.client.TESTextures;
import tes.common.world.map.TESWaypoint;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESGuiRendererMap {
	private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");

	private boolean sepia;

	private double prevMapX;
	private double mapX;
	private double prevMapY;
	private double mapY;

	private float zoomExp;
	private float zoomStable;

	private static void renderVignette(GuiScreen gui, double zLevel) {
		renderVignette(gui, zLevel, 0, 0, gui.width, gui.height);
	}

	private static void renderVignette(GuiScreen gui, double zLevel, int x0, int y0, int x1, int y1) {
		GL11.glEnable(3042);
		OpenGlHelper.glBlendFunc(771, 769, 1, 0);
		float alpha = 1.0f;
		GL11.glColor4f(alpha, alpha, alpha, 1.0f);
		gui.mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
		double u0 = (double) x0 / gui.width;
		double u1 = (double) x1 / gui.width;
		double v0 = (double) y0 / gui.height;
		double v1 = (double) y1 / gui.height;
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x0, y1, zLevel, u0, v1);
		tessellator.addVertexWithUV(x1, y1, zLevel, u1, v1);
		tessellator.addVertexWithUV(x1, y0, zLevel, u1, v0);
		tessellator.addVertexWithUV(x0, y0, zLevel, u0, v0);
		tessellator.draw();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		OpenGlHelper.glBlendFunc(770, 771, 1, 0);
	}

	public static void renderVignettes(GuiScreen gui, double zLevel, int count) {
		for (int l = 0; l < count; ++l) {
			renderVignette(gui, zLevel);
		}
	}

	public static void renderVignettes(GuiScreen gui, double zLevel, int count, int x0, int y0, int x1, int y1) {
		for (int l = 0; l < count; ++l) {
			renderVignette(gui, zLevel, x0, y0, x1, y1);
		}
	}

	public void renderMap(GuiScreen gui, TESGuiMap mapGui, float f) {
		renderMap(mapGui, f, 0, 0, gui.width, gui.height);
	}

	public void renderMap(TESGuiMap mapGui, float f, int x0, int y0, int x1, int y1) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		int oceanColor = TESTextures.getMapOceanColor(sepia);
		Gui.drawRect(x0, y0, x1, y1, oceanColor);
		float zoom = (float) Math.pow(2.0, zoomExp);
		double mapPosX = prevMapX + (mapX - prevMapX) * f;
		double mapPosY = prevMapY + (mapY - prevMapY) * f;
		mapGui.setFakeMapProperties((float) mapPosX, (float) mapPosY, zoom, zoomExp, zoomStable);
		int[] statics = TESGuiMap.setFakeStaticProperties(x1 - x0, y1 - y0, x0, x1, y0, y1);
		mapGui.setEnableZoomOutWPFading(false);
		mapGui.renderMapAndOverlay(sepia, 1.0f, true);
		mapGui.renderBeziers(false);
		mapGui.renderWaypoints(TESWaypoint.listAllWaypoints(), 0, 0, 0, false, true);
		TESGuiMap.setFakeStaticProperties(statics[0], statics[1], statics[2], statics[3], statics[4], statics[5]);
	}

	public void updateTick() {
		prevMapX = mapX;
		prevMapY = mapY;
	}

	public void setSepia(boolean flag) {
		sepia = flag;
	}

	public void setPrevMapX(double prevMapX) {
		this.prevMapX = prevMapX;
	}

	public double getMapX() {
		return mapX;
	}

	public void setMapX(double mapX) {
		this.mapX = mapX;
	}

	public void setPrevMapY(double prevMapY) {
		this.prevMapY = prevMapY;
	}

	public double getMapY() {
		return mapY;
	}

	public void setMapY(double mapY) {
		this.mapY = mapY;
	}

	public float getZoomExp() {
		return zoomExp;
	}

	public void setZoomExp(float zoomExp) {
		this.zoomExp = zoomExp;
	}

	public void setZoomStable(float zoomStable) {
		this.zoomStable = zoomStable;
	}
}