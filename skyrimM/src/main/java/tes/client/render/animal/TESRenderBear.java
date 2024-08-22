package tes.client.render.animal;

import tes.client.model.TESModelBear;
import tes.common.entity.animal.TESEntityBear;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class TESRenderBear extends RenderLiving {
	private static final Map<String, ResourceLocation> BEAR_TEXTURES = new HashMap<>();

	public TESRenderBear() {
		super(new TESModelBear(), 0.5f);
	}

	public static ResourceLocation getBearSkin(TESEntityBear.BearType type) {
		String s = type.textureName();
		ResourceLocation skin = BEAR_TEXTURES.get(s);
		if (skin == null) {
			skin = new ResourceLocation("tes:textures/entity/animal/bear/" + s + ".png");
			BEAR_TEXTURES.put(s, skin);
		}
		return skin;
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityBear bear = (TESEntityBear) entity;
		return getBearSkin(bear.getBearType());
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityBear animal = (TESEntityBear) entity;
		if (animal.isChild()) {
			GL11.glScalef(0.6f, 0.6f, 0.6f);
		} else {
			GL11.glScalef(1.2f, 1.2f, 1.2f);
		}
	}
}