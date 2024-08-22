package tes.client.render.animal;

import tes.client.model.TESModelButterfly;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.animal.TESEntityButterfly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public class TESRenderButterfly extends RenderLiving {
	private static final Map<TESEntityButterfly.ButterflyType, TESRandomSkins> BUTTERFLY_TEXTURES = new EnumMap<>(TESEntityButterfly.ButterflyType.class);

	public TESRenderButterfly() {
		super(new TESModelButterfly(), 0.2f);
		for (TESEntityButterfly.ButterflyType t : TESEntityButterfly.ButterflyType.values()) {
			BUTTERFLY_TEXTURES.put(t, TESRandomSkins.loadSkinsList("tes:textures/entity/animal/butterfly/" + t.getTextureDir()));
		}
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		TESEntityButterfly butterfly = (TESEntityButterfly) entity;
		if (butterfly.getButterflyType() == TESEntityButterfly.ButterflyType.QOHOR) {
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(2896);
		}
		super.doRender(entity, d, d1, d2, f, f1);
		GL11.glEnable(2896);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityButterfly butterfly = (TESEntityButterfly) entity;
		TESRandomSkins skins = BUTTERFLY_TEXTURES.get(butterfly.getButterflyType());
		return skins.getRandomSkin(butterfly);
	}

	@Override
	public float handleRotationFloat(EntityLivingBase entity, float f) {
		TESEntityButterfly butterfly = (TESEntityButterfly) entity;
		if (butterfly.isButterflyStill() && butterfly.getFlapTime() > 0) {
			return butterfly.getFlapTime() - f;
		}
		return super.handleRotationFloat(entity, f);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		GL11.glScalef(0.3f, 0.3f, 0.3f);
	}
}