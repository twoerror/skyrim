package tes.client.render.animal;

import tes.client.model.TESModelBison;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.animal.TESEntityBison;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderBison extends RenderLiving {
	private static final TESRandomSkins TEXTURES = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/bison");

	public TESRenderBison() {
		super(new TESModelBison(), 0.5f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESRandomSkinEntity bison = (TESRandomSkinEntity) entity;
		return TEXTURES.getRandomSkin(bison);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityBison animal = (TESEntityBison) entity;
		if (animal.isChild()) {
			GL11.glScalef(0.5f, 0.5f, 0.5f);
		} else {
			GL11.glScalef(1.0f, 1.0f, 1.0f);
		}
	}
}