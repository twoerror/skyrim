package tes.client.render.animal;

import tes.client.model.TESModelRhino;
import tes.common.entity.animal.TESEntityRhino;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderRhino extends RenderLiving {
	private static final ResourceLocation RHINO_TEXTURE = new ResourceLocation("tes:textures/entity/animal/rhino/rhino.png");
	private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation("tes:textures/entity/animal/rhino/saddle.png");

	public TESRenderRhino() {
		super(new TESModelRhino(), 0.5f);
		setRenderPassModel(new TESModelRhino(0.5f));
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESNPCMount rhino = (TESNPCMount) entity;
		return TESRenderHorse.getLayeredMountTexture(rhino, RHINO_TEXTURE);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		super.preRenderCallback(entity, f);
		TESEntityRhino animal = (TESEntityRhino) entity;
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