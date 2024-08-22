package tes.client.render.animal;

import tes.client.model.TESModelLionRug;
import tes.common.entity.other.inanimate.TESEntityLionRug;
import tes.common.item.other.TESItemLionRug;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class TESRenderLionRug extends TESRenderRugBase {
	public TESRenderLionRug() {
		super(new TESModelLionRug());
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityLionRug rug = (TESEntityLionRug) entity;
		TESItemLionRug.LionRugType type = rug.getRugType();
		if (type == TESItemLionRug.LionRugType.LION) {
			return TESRenderLion.TEXTURE_LION;
		}
		if (type == TESItemLionRug.LionRugType.LIONESS) {
			return TESRenderLion.TEXTURE_LIONESS;
		}
		return new ResourceLocation("");
	}

	@Override
	public void preRenderCallback() {
	}
}