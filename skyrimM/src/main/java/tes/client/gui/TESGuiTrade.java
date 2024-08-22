package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.database.TESTradeEntries;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.entity.other.utils.TESTradeSellResult;
import tes.common.inventory.TESContainerTrade;
import tes.common.inventory.TESSlotTrade;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketSell;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TESGuiTrade extends GuiContainer {
	public static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/npc/trade.png");

	private final TESEntityNPC theEntity;
	private final TESContainerTrade containerTrade;

	private GuiButton buttonSell;

	public TESGuiTrade(InventoryPlayer inv, TESTradeable trader, World world) {
		super(new TESContainerTrade(inv, trader, world));
		containerTrade = (TESContainerTrade) inventorySlots;
		theEntity = (TESEntityNPC) trader;
		ySize = 270;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled && button == buttonSell) {
			IMessage packet = new TESPacketSell();
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	private void drawCenteredString(String s, int i, int j, int k) {
		fontRendererObj.drawString(s, i - fontRendererObj.getStringWidth(s) / 2, j, k);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(GUI_TEXTURE);
		func_146110_a(guiLeft, guiTop, 0.0f, 0.0f, xSize, ySize, 512.0f, 512.0f);
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		TESTradeEntry trade;
		int y;
		int x;
		int cost;
		int l;
		drawCenteredString(theEntity.getNPCName(), 89, 11, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("tes.container.trade.buy"), 8, 28, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("tes.container.trade.sell"), 8, 79, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("tes.container.trade.sellOffer"), 8, 129, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8, 176, 4210752);
		int lockedTradeColor = -1610612736;
		for (l = 0; l < containerTrade.getTradeInvBuy().getSizeInventory(); ++l) {
			TESSlotTrade slotBuy = (TESSlotTrade) containerTrade.getSlotFromInventory(containerTrade.getTradeInvBuy(), l);
			trade = slotBuy.getTrade();
			if (trade != null) {
				if (trade.isAvailable()) {
					cost = slotBuy.cost();
					if (cost <= 0) {
						continue;
					}
					renderCost(Integer.toString(cost), slotBuy.xDisplayPosition + 8, slotBuy.yDisplayPosition + 22);
				} else {
					GL11.glTranslatef(0.0f, 0.0f, 200.0f);
					x = slotBuy.xDisplayPosition;
					y = slotBuy.yDisplayPosition;
					drawRect(x, y, x + 16, y + 16, lockedTradeColor);
					GL11.glTranslatef(0.0f, 0.0f, -200.0f);
					drawCenteredString(StatCollector.translateToLocal("tes.container.trade.locked"), slotBuy.xDisplayPosition + 8, slotBuy.yDisplayPosition + 22, 4210752);
				}
			}
		}
		for (l = 0; l < containerTrade.getTradeInvSell().getSizeInventory(); ++l) {
			TESSlotTrade slotSell = (TESSlotTrade) containerTrade.getSlotFromInventory(containerTrade.getTradeInvSell(), l);
			trade = slotSell.getTrade();
			if (trade != null) {
				if (trade.isAvailable()) {
					cost = slotSell.cost();
					if (cost <= 0) {
						continue;
					}
					renderCost(Integer.toString(cost), slotSell.xDisplayPosition + 8, slotSell.yDisplayPosition + 22);
				} else {
					GL11.glTranslatef(0.0f, 0.0f, 200.0f);
					x = slotSell.xDisplayPosition;
					y = slotSell.yDisplayPosition;
					drawRect(x, y, x + 16, y + 16, lockedTradeColor);
					GL11.glTranslatef(0.0f, 0.0f, -200.0f);
					drawCenteredString(StatCollector.translateToLocal("tes.container.trade.locked"), slotSell.xDisplayPosition + 8, slotSell.yDisplayPosition + 22, 4210752);
				}
			}
		}
		int totalSellPrice = 0;
		for (int l2 = 0; l2 < containerTrade.getTradeInvSellOffer().getSizeInventory(); ++l2) {
			TESTradeSellResult sellResult;
			Slot slotSell = containerTrade.getSlotFromInventory(containerTrade.getTradeInvSellOffer(), l2);
			ItemStack item = slotSell.getStack();
			if (item != null && (sellResult = TESTradeEntries.getItemSellResult(item, theEntity)) != null) {
				totalSellPrice += sellResult.getTotalSellValue();
			}
		}
		if (totalSellPrice > 0) {
			fontRendererObj.drawString(StatCollector.translateToLocalFormatted("tes.container.trade.sellPrice", totalSellPrice), 100, 169, 4210752);
		}
		buttonSell.enabled = totalSellPrice > 0;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonSell = new TESGuiTradeButton(0, guiLeft + 79, guiTop + 164);
		buttonSell.enabled = false;
		buttonList.add(buttonSell);
	}

	private void renderCost(String s, int x, int y) {
		int x1 = x;
		int y1 = y;
		int l = fontRendererObj.getStringWidth(s);
		boolean halfSize = l > 20;
		if (halfSize) {
			GL11.glPushMatrix();
			GL11.glScalef(0.5f, 0.5f, 1.0f);
			x1 *= 2;
			y1 *= 2;
			y1 += fontRendererObj.FONT_HEIGHT / 2;
		}
		drawCenteredString(s, x1, y1, 4210752);
		if (halfSize) {
			GL11.glPopMatrix();
		}
	}
}