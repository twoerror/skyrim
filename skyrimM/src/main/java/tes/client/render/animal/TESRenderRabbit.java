package tes.client.render.animal;

import tes.client.model.TESModelRabbit;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderRabbit extends RenderLiving {
	private static final TESRandomSkins TEXTURES = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/rabbit");

	public TESRenderRabbit() {
		super(new TESModelRabbit(), 0.3f);
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESRandomSkinEntity rabbit = (TESRandomSkinEntity) entity;
		return TEXTURES.getRandomSkin(rabbit);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		GL11.glScalef(0.75f, 0.75f, 0.75f);
	}
}