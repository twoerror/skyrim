package tes.client.render.animal;

import tes.client.render.other.TESRandomSkins;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderWhiteBison extends TESRenderBison {
	private static final TESRandomSkins W_BISON_TEXTURES = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/wbison");

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESRandomSkinEntity bison = (TESRandomSkinEntity) entity;
		return W_BISON_TEXTURES.getRandomSkin(bison);
	}
}