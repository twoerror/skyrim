package tes.client.render.animal;

import tes.client.model.TESModelMammoth;
import tes.common.entity.animal.TESEntityMammoth;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderMammoth extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/mammoth.png");

	public TESRenderMammoth() {
		super(new TESModelMammoth(), 0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityMammoth animal = (TESEntityMammoth) entity;
		if (!animal.isChild()) {
			GL11.glScalef(2.0f, 2.0f, 2.0f);
		}
	}
}