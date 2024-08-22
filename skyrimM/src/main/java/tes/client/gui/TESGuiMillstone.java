package tes.client.gui;

import tes.common.inventory.TESContainerMillstone;
import tes.common.tileentity.TESTileEntityMillstone;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiMillstone extends GuiContainer {
	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/millstone.png");

	private final TESTileEntityMillstone theMillstone;

	public TESGuiMillstone(InventoryPlayer inv, TESTileEntityMillstone millstone) {
		super(new TESContainerMillstone(inv, millstone));
		theMillstone = millstone;
		ySize = 182;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if (theMillstone.isMilling()) {
			int k = theMillstone.getMillProgressScaled(14);
			drawTexturedModalRect(guiLeft + 85, guiTop + 47, 176, 0, 14, k);
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		String s = theMillstone.getInventoryName();
		fontRendererObj.drawString(s, xSize / 2 - fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, 88, 4210752);
	}
}