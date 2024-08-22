package tes.client.render.animal;

import tes.client.model.TESModelScorpion;
import tes.common.entity.animal.TESEntityDesertScorpion;
import tes.common.entity.animal.TESEntityScorpionBig;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderScorpion extends RenderLiving {
	private static final ResourceLocation JUNGLE_TEXTURE = new ResourceLocation("tes:textures/entity/animal/scorpion/jungle.png");
	private static final ResourceLocation DESERT_TEXTURE = new ResourceLocation("tes:textures/entity/animal/scorpion/desert.png");

	public TESRenderScorpion() {
		super(new TESModelScorpion(), 1.0f);
		setRenderPassModel(new TESModelScorpion());
	}

	@Override
	public float getDeathMaxRotation(EntityLivingBase entity) {
		return 180.0f;
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		if (entity instanceof TESEntityDesertScorpion) {
			return DESERT_TEXTURE;
		}
		return JUNGLE_TEXTURE;
	}

	@Override
	public float handleRotationFloat(EntityLivingBase entity, float f) {
		float strikeTime = ((TESEntityScorpionBig) entity).getStrikeTime();
		if (strikeTime > 0.0f) {
			strikeTime -= f;
		}
		return strikeTime / 20.0f;
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		float scale = ((TESEntityScorpionBig) entity).getScorpionScaleAmount();
		GL11.glScalef(scale, scale, scale);
	}
}