package tes.client.render.other;

import tes.common.database.TESCapes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESRenderCape {
	private TESRenderCape() {
	}

	public static void renderCape(TESCapes cape, ModelBiped model) {
		Minecraft mc = Minecraft.getMinecraft();
		ResourceLocation capeTexture = cape.getCapeTexture();
		GL11.glPushMatrix();
		GL11.glTranslatef(0.0f, 0.0f, 0.125f);
		GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-10.0f, 1.0f, 0.0f, 0.0f);
		mc.getTextureManager().bindTexture(capeTexture);
		model.renderCloak(0.0625f);
		GL11.glPopMatrix();
	}
}