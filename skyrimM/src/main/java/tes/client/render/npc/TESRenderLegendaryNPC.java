package tes.client.render.npc;

import tes.client.TESSpeechClient;
import tes.client.model.TESModelHuman;
import tes.client.render.other.TESRenderBiped;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderLegendaryNPC extends TESRenderBiped {
	private final String name;
	private final float height;

	public TESRenderLegendaryNPC(String texture) {
		super(new TESModelHuman(), 0.5f);
		name = texture;
		height = 1.0f;
	}

	public TESRenderLegendaryNPC(String texture, float size) {
		super(new TESModelHuman(), 0.5f);
		name = texture;
		height = size;
	}

	@Override
	public void doRender(EntityLiving entity, double d, double d1, double d2, float f, float f1) {
		TESEntityNPC legend = (TESEntityNPC) entity;
		super.doRender(legend, d, d1, d2, f, f1);
		if (Minecraft.isGuiEnabled() && !TESSpeechClient.hasSpeech(legend)) {
			func_147906_a(legend, legend.getCommandSenderName(), d, d1 + 0.15, d2, 64);
		}
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return new ResourceLocation("TES:textures/entity/legendary/" + name + ".png");
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		GL11.glScalef(height, height, height);
	}
}