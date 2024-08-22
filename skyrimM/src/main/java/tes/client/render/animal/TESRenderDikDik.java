package tes.client.render.animal;

import tes.client.model.TESModelDikDik;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderDikDik extends RenderLiving {
	private static final TESRandomSkins TEXTURES = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/dikdik");

	public TESRenderDikDik() {
		super(new TESModelDikDik(), 0.8f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESRandomSkinEntity dikdik = (TESRandomSkinEntity) entity;
		return TEXTURES.getRandomSkin(dikdik);
	}
}