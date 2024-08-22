package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.entity.other.TESEntityNPC;
import tes.common.inventory.TESContainerCoinExchange;
import tes.common.network.TESPacketCoinExchange;
import tes.common.network.TESPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiCoinExchange extends GuiContainer {
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/coin_exchange.png");

	private final TESContainerCoinExchange theContainer;

	private GuiButton buttonLeft;
	private GuiButton buttonRight;

	public TESGuiCoinExchange(EntityPlayer entityplayer, TESEntityNPC npc) {
		super(new TESContainerCoinExchange(entityplayer, npc));
		theContainer = (TESContainerCoinExchange) inventorySlots;
		ySize = 188;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == buttonLeft || button == buttonRight) {
				IMessage packet = new TESPacketCoinExchange(button.id);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else {
				super.actionPerformed(button);
			}
		}
	}

	private void drawCenteredString(String s, int i, int j, int k) {
		fontRendererObj.drawString(s, i - fontRendererObj.getStringWidth(s) / 2, j, k);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if (theContainer.isExchanged()) {
			for (int l = 0; l < theContainer.getExchangeInv().getSizeInventory(); ++l) {
				Slot slot = theContainer.getSlotFromInventory(theContainer.getExchangeInv(), l);
				if (slot.getHasStack()) {
					drawTexturedModalRect(guiLeft + slot.xDisplayPosition - 5, guiTop + slot.yDisplayPosition - 5, 176, 51, 26, 26);
				}
			}
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		drawCenteredString(StatCollector.translateToLocal("tes.container.coinExchange"), 89, 11, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, 94, 4210752);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		buttonLeft.enabled = !theContainer.isExchanged() && theContainer.getExchangeInv().getStackInSlot(0) != null;
		buttonRight.enabled = !theContainer.isExchanged() && theContainer.getExchangeInv().getStackInSlot(1) != null;
		super.drawScreen(i, j, f);
	}

	@Override
	public void initGui() {
		super.initGui();
		int i = guiLeft + xSize / 2;
		int j = 28;
		int k = 16;
		buttonLeft = new TESGuiButtonCoinExchange(0, i - j - k, guiTop + 45);
		buttonList.add(buttonLeft);
		buttonRight = new TESGuiButtonCoinExchange(1, i + j - k, guiTop + 45);
		buttonList.add(buttonRight);
	}
}