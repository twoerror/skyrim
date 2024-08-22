package tes.client.render.other;

import net.minecraft.client.renderer.entity.RenderArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderArrowFire extends RenderArrow {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/model/arrow_fire.png");

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}
}