package tes.client.render.animal;

import tes.client.model.TESModelDeer;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.animal.TESEntityDeer;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class TESRenderDeer extends RenderLiving {
	private static final TESRandomSkins ELK_TEXTURES = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/elk/elk");
	private static final ResourceLocation SADDLE_TEXTURE = new ResourceLocation("tes:textures/entity/animal/elk/saddle.png");

	public TESRenderDeer() {
		super(new TESModelDeer(), 0.5f);
		setRenderPassModel(new TESModelDeer(0.5f));
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityDeer elk = (TESEntityDeer) entity;
		ResourceLocation elkSkin = ELK_TEXTURES.getRandomSkin(elk);
		return TESRenderHorse.getLayeredMountTexture(elk, elkSkin);
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