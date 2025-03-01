package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.inventory.TESContainerPouch;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketRenamePouch;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiPouch extends GuiContainer {
	public static final ResourceLocation TEXTURE = new ResourceLocation("tes:textures/gui/pouch.png");

	private final TESContainerPouch thePouch;

	private final int pouchRows;

	private GuiTextField theGuiTextField;

	public TESGuiPouch(EntityPlayer entityplayer, int slot) {
		super(new TESContainerPouch(entityplayer, slot));
		thePouch = (TESContainerPouch) inventorySlots;
		pouchRows = thePouch.getCapacity() / 9;
		ySize = 180;
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		for (int l = 0; l < pouchRows; ++l) {
			drawTexturedModalRect(guiLeft + 7, guiTop + 29 + l * 18, 0, 180, 162, 18);
		}
		GL11.glDisable(2896);
		theGuiTextField.drawTextBox();
		GL11.glEnable(2896);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, ySize - 96 + 2, 4210752);
	}

	@Override
	public void initGui() {
		super.initGui();
		theGuiTextField = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 80, guiTop + 7, 160, 20);
		theGuiTextField.setText(thePouch.getDisplayName());
	}

	@Override
	public void keyTyped(char c, int i) {
		if (theGuiTextField.textboxKeyTyped(c, i)) {
			renamePouch();
		} else {
			super.keyTyped(c, i);
		}
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		theGuiTextField.mouseClicked(i, j, k);
	}

	private void renamePouch() {
		String name = theGuiTextField.getText();
		thePouch.renamePouch(name);
		IMessage packet = new TESPacketRenamePouch(name);
		TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		theGuiTextField.updateCursorCounter();
	}
}