package tes.client.render.animal;

import tes.client.model.TESModelGiraffe;
import tes.common.entity.animal.TESEntityGiraffe;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderGiraffe extends RenderLiving {
	public static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/entity/animal/giraffe/giraffe.png");

	private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation("tes:textures/entity/animal/giraffe/saddle.png");

	public TESRenderGiraffe() {
		super(new TESModelGiraffe(0.0f), 0.5f);
		setRenderPassModel(new TESModelGiraffe(0.5f));
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TEXTURE;
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityGiraffe animal = (TESEntityGiraffe) entity;
		if (animal.isChild()) {
			GL11.glScalef(0.5f, 0.5f, 0.5f);
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