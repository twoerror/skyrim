package tes.client.render.other;

import tes.common.entity.other.inanimate.TESEntityPortal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class TESRenderPortal extends Render {
	@SuppressWarnings("StatementWithEmptyBody")
	private static float interpolateRotation(float prevRotation, float newRotation, float tick) {
		float interval;
		for (interval = newRotation - prevRotation; interval < -180.0f; interval += 360.0f) {
		}
		while (interval >= 180.0f) {
			interval -= 360.0f;
		}
		return prevRotation + tick * interval;
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		TESEntityPortal spawner = (TESEntityPortal) entity;
		GL11.glPushMatrix();
		GL11.glEnable(32826);
		GL11.glTranslatef((float) d, (float) d1, (float) d2);
		float rotation = interpolateRotation(spawner.getPrevPortalRotation(), spawner.getPortalRotation(), f1);
		float scale = 1.5f;
		GL11.glRotatef(rotation, 0.0f, 1.0f, 0.0f);
		GL11.glScalef(scale, scale, scale);
		ItemStack item = new ItemStack(Items.iron_sword);
		renderManager.itemRenderer.renderItem(renderManager.livingPlayer, item, 0, IItemRenderer.ItemRenderType.EQUIPPED);
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TextureMap.locationItemsTexture;
	}
}