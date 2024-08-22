package tes.client.render.animal;

import tes.client.model.TESModelBearRug;
import tes.common.entity.other.inanimate.TESEntityBearRug;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderBearRug extends TESRenderRugBase {
	public TESRenderBearRug() {
		super(new TESModelBearRug());
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityBearRug rug = (TESEntityBearRug) entity;
		return TESRenderBear.getBearSkin(rug.getRugType());
	}

	@Override
	public void preRenderCallback() {
		GL11.glScalef(1.2f, 1.2f, 1.2f);
	}
}