package tes.client.gui;

import tes.common.inventory.TESContainerAlloyForge;
import tes.common.tileentity.TESTileEntityAlloyForge;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiAlloyForge extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/forge.png");

	private final TESTileEntityAlloyForge theForge;

	public TESGuiAlloyForge(InventoryPlayer inv, TESTileEntityAlloyForge forge) {
		super(new TESContainerAlloyForge(inv, forge));
		theForge = forge;
		ySize = 233;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if (theForge.isSmelting()) {
			int k = theForge.getSmeltTimeRemainingScaled(12);
			drawTexturedModalRect(guiLeft + 80, guiTop + 112 + 12 - k, 176, 12 - k, 14, k + 2);
		}
		int l = theForge.getSmeltProgressScaled(24);
		drawTexturedModalRect(guiLeft + 80, guiTop + 58, 176, 14, 16, l + 1);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		String s = theForge.getInventoryName();
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, 139, 4210752);
	}
}