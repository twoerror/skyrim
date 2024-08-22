package tes.client.render.other;

import tes.client.TESTextures;
import tes.common.tileentity.TESTileEntityCommandTable;
import tes.common.world.genlayer.TESGenLayerWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TESRenderCommandTable extends TileEntitySpecialRenderer {
	private static void renderTableAt(double d, double d1, double d2, double viewerX, double viewerZ, int zoomExp) {
		GL11.glEnable(32826);
		GL11.glDisable(2884);
		GL11.glDisable(2896);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d + 0.5f, (float) d1 + 1.1f, (float) d2 + 0.5f);
		float posX = Math.round(viewerX / TESGenLayerWorld.SCALE) + TESGenLayerWorld.ORIGIN_X;
		float posY = Math.round(viewerZ / TESGenLayerWorld.SCALE) + TESGenLayerWorld.ORIGIN_Z;
		int viewportWidth = 400;
		viewportWidth = (int) Math.round(viewportWidth * Math.pow(2.0, zoomExp));
		double radius = 0.9;
		float minX = posX - (float) viewportWidth / 2;
		float maxX = posX + (float) viewportWidth / 2;
		if (minX < 0.0f) {
			posX = (float) viewportWidth / 2;
		}
		if (maxX >= TESGenLayerWorld.getImageWidth()) {
			posX = TESGenLayerWorld.getImageWidth() - (float) viewportWidth / 2;
		}
		float minY = posY - (float) viewportWidth / 2;
		float maxY = posY + (float) viewportWidth / 2;
		if (minY < 0.0f) {
			posY = (float) viewportWidth / 2;
		}
		if (maxY >= TESGenLayerWorld.getImageHeight()) {
			posY = TESGenLayerWorld.getImageHeight() - (float) viewportWidth / 2;
		}
		double minU = (double) (posX - (float) viewportWidth / 2) / TESGenLayerWorld.getImageWidth();
		double maxU = (double) (posX + (float) viewportWidth / 2) / TESGenLayerWorld.getImageWidth();
		double minV = (double) (posY - (float) viewportWidth / 2) / TESGenLayerWorld.getImageHeight();
		double maxV = (double) (posY + (float) viewportWidth / 2) / TESGenLayerWorld.getImageHeight();
		GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		TESTextures.drawMap(true, -radius, radius, -radius, radius, 0.0, minU, maxU, minV, maxV, 1.0f);
		TESTextures.drawMapOverlay(-radius, radius, -radius, radius, 0.0);
		double compassInset = radius * 0.05;
		TESTextures.drawMapCompassBottomLeft(-radius + compassInset, radius - compassInset, -0.01, 0.15 * radius * 0.0625);
		GL11.glDisable(3553);
		Tessellator tess = Tessellator.instance;
		double rRed = radius + 0.015;
		double rBlack = rRed + 0.015;
		GL11.glTranslatef(0.0f, 0.0f, 0.01f);
		tess.startDrawingQuads();
		tess.setColorOpaque_I(-6156032);
		tess.addVertex(-rRed, rRed, 0.0);
		tess.addVertex(rRed, rRed, 0.0);
		tess.addVertex(rRed, -rRed, 0.0);
		tess.addVertex(-rRed, -rRed, 0.0);
		tess.draw();
		GL11.glTranslatef(0.0f, 0.0f, 0.01f);
		tess.startDrawingQuads();
		tess.setColorOpaque_I(-16777216);
		tess.addVertex(-rBlack, rBlack, 0.0);
		tess.addVertex(rBlack, rBlack, 0.0);
		tess.addVertex(rBlack, -rBlack, 0.0);
		tess.addVertex(-rBlack, -rBlack, 0.0);
		tess.draw();
		GL11.glEnable(3553);
		GL11.glPopMatrix();
		GL11.glEnable(2896);
	}

	public void renderInvTable() {
		GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
		GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
		EntityLivingBase viewer = Minecraft.getMinecraft().renderViewEntity;
		renderTableAt(0.0, 0.0, 0.0, viewer.posX, viewer.posZ, 0);
		bindTexture(TextureMap.locationBlocksTexture);
	}

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d, double d1, double d2, float f) {
		TESTileEntityCommandTable table = (TESTileEntityCommandTable) tileentity;
		renderTableAt(d, d1, d2, tileentity.xCoord + 0.5, tileentity.zCoord + 0.5, table.getZoomExp());
	}
}