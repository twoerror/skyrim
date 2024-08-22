package tes.client.render.animal;

import tes.client.model.TESModelBoar;
import tes.common.entity.animal.TESEntityBoar;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderBoar extends RenderLiving {
	private static final ResourceLocation BOAR_TEXTURE = new ResourceLocation("tes:textures/entity/animal/boar/boar.png");
	private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation("tes:textures/entity/animal/boar/saddle.png");

	public TESRenderBoar() {
		super(new TESModelBoar(), 0.7f);
		setRenderPassModel(new TESModelBoar(0.5f));
	}

	private static boolean isRobert(TESEntityBoar boar) {
		return boar.hasCustomNameTag() && "robert".equalsIgnoreCase(boar.getCustomNameTag());
	}

	@Override
	public void doRender(EntityLiving entity, double d, double d1, double d2, float f, float f1) {
		TESEntityBoar boar = (TESEntityBoar) entity;
		super.doRender(boar, d, d1, d2, f, f1);
		if (Minecraft.isGuiEnabled() && isRobert(boar)) {
			func_147906_a(boar, boar.getCommandSenderName(), d, d1 + 1, d2, 64);
		}
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESNPCMount boar = (TESNPCMount) entity;
		return TESRenderHorse.getLayeredMountTexture(boar, BOAR_TEXTURE);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityBoar boar = (TESEntityBoar) entity;
		if (boar.isChild()) {
			GL11.glScalef(0.5f, 0.5f, 0.5f);
		} else if (isRobert(boar)) {
			GL11.glScalef(2.0f, 2.0f, 2.0f);
		} else {
			GL11.glScalef(1.0f, 1.0f, 1.0f);
		}
	}

	@Override
	public int shouldRenderPass(EntityLivingBase entity, int pass, float f) {
		if (pass == 0 && ((TESNPCMount) entity).isMountSaddled()) {
			bindTexture(SADDLE_TEXTURE);
			return 1;
		}
		return super.shouldRenderPass(entity, pass, f);
	}
}