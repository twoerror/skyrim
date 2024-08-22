package tes.client.render.animal;

import tes.client.render.other.TESRandomSkins;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderWhiteOryx extends TESRenderGemsbok {
	private static final TESRandomSkins ORYX_TEXTURES = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/whiteOryx");

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return ORYX_TEXTURES.getRandomSkin((TESRandomSkinEntity) entity);
	}
}