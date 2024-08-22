package tes.client.render.animal;

import tes.client.model.TESModelGiraffeRug;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderGiraffeRug extends TESRenderRugBase {
	public TESRenderGiraffeRug() {
		super(new TESModelGiraffeRug());
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TESRenderGiraffe.TEXTURE;
	}

	@Override
	public void preRenderCallback() {
	}
}