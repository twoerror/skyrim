package tes.client.render.animal;

import tes.client.model.TESModelBear;
import tes.common.entity.animal.TESEntitySnowBear;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderSnowBear extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/polarbear.png");

	public TESRenderSnowBear() {
		super(new TESModelBear(), 0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntitySnowBear animal = (TESEntitySnowBear) entity;
		if (animal.isChild()) {
			GL11.glScalef(0.65f, 0.65f, 0.65f);
		} else {
			GL11.glScalef(1.3f, 1.3f, 1.3f);
		}
	}
}