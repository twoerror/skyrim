package tes.client.render.animal;

import tes.client.model.TESModelBird;
import tes.client.render.other.TESRandomSkins;
import tes.common.entity.animal.TESEntityBird;
import tes.common.entity.animal.TESEntityGorcrow;
import tes.common.entity.animal.TESEntitySeagull;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;

public class TESRenderBird extends RenderLiving {
	private static final Map<String, TESRandomSkins> BIRD_TEXTURES = new HashMap<>();

	private static boolean renderStolenItem = true;

	public TESRenderBird() {
		super(new TESModelBird(), 0.2f);
	}

	public static void setRenderStolenItem(boolean renderStolenItem) {
		TESRenderBird.renderStolenItem = renderStolenItem;
	}

	private static TESRandomSkins getBirdSkins(String s) {
		TESRandomSkins skins = BIRD_TEXTURES.get(s);
		if (skins == null) {
			skins = TESRandomSkins.loadSkinsList("tes:textures/entity/animal/bird/" + s);
			BIRD_TEXTURES.put(s, skins);
		}
		return skins;
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		TESEntityBird bird = (TESEntityBird) entity;
		String type = bird.getBirdTextureDir();
		TESRandomSkins skins = getBirdSkins(type);
		return skins.getRandomSkin(bird);
	}

	@Override
	public float handleRotationFloat(EntityLivingBase entity, float f) {
		TESEntityBird bird = (TESEntityBird) entity;
		if (bird.isBirdStill() && bird.getFlapTime() > 0) {
			return bird.getFlapTime() - f;
		}
		return super.handleRotationFloat(entity, f);
	}

	@Override
	public void preRenderCallback(EntityLivingBase entity, float f) {
		if (entity instanceof TESEntityGorcrow) {
			float scale = TESEntityGorcrow.GORCROW_SCALE;
			GL11.glScalef(scale, scale, scale);
		} else if (entity instanceof TESEntitySeagull) {
			float scale = TESEntitySeagull.SEAGULL_SCALE;
			GL11.glScalef(scale, scale, scale);
		}
	}

	@Override
	public void renderEquippedItems(EntityLivingBase entity, float f) {
		TESEntityBird bird = (TESEntityBird) entity;
		if (renderStolenItem) {
			GL11.glColor3f(1.0f, 1.0f, 1.0f);
			ItemStack stolenItem = bird.getStolenItem();
			if (stolenItem != null) {
				GL11.glPushMatrix();
				((TESModelBird) mainModel).getHead().postRender(0.0625f);
				GL11.glTranslatef(0.05f, 1.4f, -0.1f);
				float scale = 0.25f;
				GL11.glScalef(scale, scale, scale);
				renderManager.itemRenderer.renderItem(entity, stolenItem, 0);
				GL11.glPopMatrix();
			}
		}
	}
}