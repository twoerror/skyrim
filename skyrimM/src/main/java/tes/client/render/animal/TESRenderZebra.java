package tes.client.render.animal;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderZebra extends TESRenderHorse {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/zebra.png");

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}