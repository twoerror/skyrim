package tes.client.render.animal;

import tes.client.model.TESModelFlamingo;
import tes.common.entity.animal.TESEntityFlamingo;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class TESRenderFlamingo extends RenderLiving {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/flamingo/flamingo.png");
	private static final ResourceLocation TEXTURE_CHICK = new ResourceLocation("tes:textures/entity/animal/flamingo/chick.png");

	public TESRenderFlamingo() {
		super(new TESModelFlamingo(), 0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return ((EntityLivingBase) entity).isChild() ? TEXTURE_CHICK : TEXTURE;
	}

	@Override
	public float handleRotationFloat(EntityLivingBase entity, float f) {
		TESEntityFlamingo flamingo = (TESEntityFlamingo) entity;
		float f1 = flamingo.getCoef1() + (flamingo.getCoef2() - flamingo.getCoef1()) * f;
		float f2 = flamingo.getCoef3() + (flamingo.getDestPos() - flamingo.getCoef3()) * f;
		return (MathHelper.sin(f1) + 1.0f) * f2;
	}
}