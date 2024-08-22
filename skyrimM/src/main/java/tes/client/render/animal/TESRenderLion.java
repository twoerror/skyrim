package tes.client.render.animal;

import tes.client.model.TESModelLion;
import tes.common.entity.animal.TESEntityLionBase;
import tes.common.entity.animal.TESEntityLioness;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderLion extends RenderLiving {
	public static final ResourceLocation TEXTURE_LION = new ResourceLocation("tes:textures/entity/animal/lion/lion.png");
	public static final ResourceLocation TEXTURE_LIONESS = new ResourceLocation("tes:textures/entity/animal/lion/lioness.png");

	public TESRenderLion() {
		super(new TESModelLion(), 0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityLionBase lion = (TESEntityLionBase) entity;
		return lion instanceof TESEntityLioness ? TEXTURE_LIONESS : TEXTURE_LION;
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityLionBase animal = (TESEntityLionBase) entity;
		if (animal.isChild()) {
			GL11.glScalef(0.5f, 0.5f, 0.5f);
		} else {
			GL11.glScalef(1.0f, 1.0f, 1.0f);
		}
	}
}