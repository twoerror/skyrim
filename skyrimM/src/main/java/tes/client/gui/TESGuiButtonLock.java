package tes.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TESGuiButtonLock extends GuiButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/gui/widgets.png");

	public TESGuiButtonLock(int i, int j, int k) {
		super(i, j, k, 20, 20, "");
	}

	@Override
	public void drawButton(Minecraft mc, int i, int j) {
		if (visible) {
			mc.getTextureManager().bindTexture(TEXTURE);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			field_146123_n = i >= xPosition && j >= yPosition && i < xPosition + width && j < yPosition + height;
			int k = getHoverState(field_146123_n);
			drawTexturedModalRect(xPosition, yPosition, 0, 196 + k * 20, width, height);
			mouseDragged(mc, i, j);
		}
	}
}