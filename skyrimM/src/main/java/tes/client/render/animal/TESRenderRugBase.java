package tes.client.render.animal;

import tes.common.entity.other.inanimate.TESEntityRugBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

public abstract class TESRenderRugBase extends Render {
	private final ModelBase rutesel;

	protected TESRenderRugBase(ModelBase m) {
		rutesel = m;
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		TESEntityRugBase rug = (TESEntityRugBase) entity;
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		bindEntityTexture(rug);
		GL11.glScalef(-1.0f, -1.0f, 1.0f);
		GL11.glRotatef(180.0f - rug.rotationYaw, 0.0f, 1.0f, 0.0f);
		preRenderCallback();
		rutesel.render(rug, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
	}

	protected abstract void preRenderCallback();
}