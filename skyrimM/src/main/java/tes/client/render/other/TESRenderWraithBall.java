package tes.client.render.other;

import tes.common.entity.other.inanimate.TESEntityMarshWraithBall;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderWraithBall extends Render {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/essos/mossovy/wraith/marshWraith_ball.png");

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		GL11.glEnable(32826);
		bindEntityTexture(entity);
		Tessellator tessellator = Tessellator.instance;
		GL11.glEnable(3042);
		GL11.glBlendFunc(770, 771);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.75f);
		drawSprite(tessellator, ((TESEntityMarshWraithBall) entity).getAnimationTick());
		GL11.glDisable(3042);
		GL11.glDisable(32826);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glPopMatrix();
	}

	private void drawSprite(Tessellator tessellator, int index) {
		float var3 = index % 4 * 16 / 64.0f;
		float var4 = (index % 4 * 16 + 16) / 64.0f;
		float var5 = (float) index / 4 * 16 / 64.0f;
		float var6 = ((float) index / 4 * 16 + 16) / 64.0f;
		float var7 = 1.0f;
		float var8 = 0.5f;
		float var9 = 0.25f;
		GL11.glRotatef(180.0f - renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0f, 1.0f, 0.0f);
		tessellator.addVertexWithUV(0.0f - var8, 0.0f - var9, 0.0, var3, var6);
		tessellator.addVertexWithUV(var7 - var8, 0.0f - var9, 0.0, var4, var6);
		tessellator.addVertexWithUV(var7 - var8, var7 - var9, 0.0, var4, var5);
		tessellator.addVertexWithUV(0.0f - var8, var7 - var9, 0.0, var3, var5);
		tessellator.draw();
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}