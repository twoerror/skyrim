package tes.client.render.other;

import tes.client.model.TESModelBannerWall;
import tes.common.entity.other.inanimate.TESEntityBannerWall;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderBannerWall extends Render {
	private static final TESModelBannerWall MODEL = new TESModelBannerWall();

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		TESEntityBannerWall banner = (TESEntityBannerWall) entity;
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glScalef(-1.0f, -1.0f, 1.0f);
		GL11.glRotatef(f, 0.0f, 1.0f, 0.0f);
		bindTexture(TESRenderBanner.STAND_TEXTURE);
		MODEL.renderPost(0.0625f);
		bindTexture(TESRenderBanner.getBannerTexture(banner.getBannerType()));
		MODEL.renderBanner(0.0625f);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
		if (RenderManager.debugBoundingBox) {
			GL11.glPushMatrix();
			GL11.glDepthMask(false);
			GL11.glDisable(3553);
			GL11.glDisable(2896);
			GL11.glDisable(2884);
			GL11.glDisable(3042);
			AxisAlignedBB aabb = banner.boundingBox.copy().offset(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			RenderGlobal.drawOutlinedBoundingBox(aabb, 16777215);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glEnable(3553);
			GL11.glEnable(2896);
			GL11.glEnable(2884);
			GL11.glDisable(3042);
			GL11.glDepthMask(true);
			GL11.glPopMatrix();
		}
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TESRenderBanner.STAND_TEXTURE;
	}
}