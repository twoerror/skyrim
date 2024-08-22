package tes.client.render.other;

import tes.client.model.TESModelBanner;
import tes.common.item.other.TESItemBanner;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class TESRenderBannerItem implements IItemRenderer {
	private static final TESModelBanner MODEL = new TESModelBanner();
	private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	@Override
	public boolean handleRenderType(ItemStack itemstack, IItemRenderer.ItemRenderType type) {
		return type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemstack, Object... data) {
		GL11.glDisable(2884);
		Entity holder = (Entity) data[1];
		boolean isFirstPerson = holder == MINECRAFT.thePlayer && MINECRAFT.gameSettings.thirdPersonView == 0;
		TextureManager textureManager = MINECRAFT.getTextureManager();
		if (isFirstPerson) {
			GL11.glTranslatef(1.0f, 1.0f, 0.0f);
			GL11.glScalef(-1.0f, 1.0f, 1.0f);
		} else {
			GL11.glTranslatef(-1.5f, 0.85f, -0.1f);
			GL11.glRotatef(75.0f, 0.0f, 0.0f, 1.0f);
		}
		GL11.glScalef(1.0f, -1.0f, 1.0f);
		TESItemBanner.BannerType bannerType = TESItemBanner.getBannerType(itemstack);
		textureManager.bindTexture(TESRenderBanner.STAND_TEXTURE);
		MODEL.renderPost(0.0625f);
		MODEL.renderLowerPost(0.0625f);
		textureManager.bindTexture(TESRenderBanner.getBannerTexture(bannerType));
		MODEL.renderBanner(0.0625f);
	}

	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack itemstack, IItemRenderer.ItemRendererHelper helper) {
		return false;
	}
}