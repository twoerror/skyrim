package tes.client.render.other;

import tes.client.TESClientProxy;
import tes.client.model.TESModelPortal;
import tes.common.entity.other.inanimate.TESEntityPortal;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderCompass extends Render {
	public static final ResourceLocation RING_TEXTURE = new ResourceLocation("tes:textures/misc/portal.png");

	private static final ModelBase RINTESEL = new TESModelPortal(0);

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		TESEntityPortal portal = (TESEntityPortal) entity;
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glTranslatef((float) d, (float) d1 + 0.75f, (float) d2);
		GL11.glNormal3f(0.0f, 0.0f, 0.0f);
		GL11.glEnable(32826);
		GL11.glScalef(1.0f, -1.0f, 1.0f);
		float portalScale = portal.getScale();
		if (portalScale < TESEntityPortal.MAX_SCALE) {
			portalScale += f1;
			GL11.glScalef(portalScale /= TESEntityPortal.MAX_SCALE, portalScale, portalScale);
		}
		GL11.glRotatef(portal.getPortalRotation(f1), 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(10.0f, 1.0f, 0.0f, 0.0f);
		bindTexture(getEntityTexture(portal));
		float scale = 0.0625f;
		RINTESEL.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, scale);
		GL11.glDisable(2896);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		Tessellator.instance.setBrightness(TESClientProxy.TESSELLATOR_MAX_BRIGHTNESS);
		GL11.glEnable(2896);
		GL11.glDisable(32826);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return RING_TEXTURE;
	}
}