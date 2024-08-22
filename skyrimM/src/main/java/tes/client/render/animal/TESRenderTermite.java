package tes.client.render.animal;

import tes.client.model.TESModelTermite;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderTermite extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/termite.png");

	public TESRenderTermite() {
		super(new TESModelTermite(), 0.2f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		float scale = 0.25f;
		GL11.glScalef(scale, scale, scale);
	}
}