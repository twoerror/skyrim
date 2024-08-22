package tes.client.render.animal;

import tes.client.model.TESModelCrocodile;
import tes.common.entity.animal.TESEntityCrocodile;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class TESRenderCrocodile extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/crocodile.png");

	public TESRenderCrocodile() {
		super(new TESModelCrocodile(), 0.75f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	public float handleRotationFloat(EntityLivingBase entity, float f) {
		float snapTime = ((TESEntityCrocodile) entity).getSnapTime();
		if (snapTime > 0.0f) {
			snapTime -= f;
		}
		return snapTime / 20.0f;
	}
}