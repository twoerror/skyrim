package tes.client.render.animal;

import tes.client.model.TESModelFish;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.animal.TESEntityFish;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class TESRenderFish extends RenderLiving {
	private static final Map<String, TESRandomSkins> FISH_TEXTURES = new HashMap<>();

	public TESRenderFish() {
		super(new TESModelFish(), 0.0f);
	}

	private static TESRandomSkins getFishSkins(String s) {
		TESRandomSkins skins = FISH_TEXTURES.get(s);
		if (skins == null) {
			skins = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/fish/" + s);
			FISH_TEXTURES.put(s, skins);
		}
		return skins;
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityFish fish = (TESEntityFish) entity;
		String type = fish.getFishTextureDir();
		TESRandomSkins skins = getFishSkins(type);
		return skins.getRandomSkin(fish);
	}

	@Override
	public float handleRotationFloat(EntityLivingBase entity, float f) {
		return super.handleRotationFloat(entity, f);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		if (!entity.isInWater()) {
			GL11.glTranslatef(0.0f, -0.05f, 0.0f);
			GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
		}
	}
}