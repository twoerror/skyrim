package tes.client.gui;

import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESItems;
import tes.common.item.other.TESItemCoin;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketMoney;
import tes.common.network.TESPacketMoneyGet;
import tes.common.network.TESPacketMoneyGive;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class TESGuiIronBank extends GuiScreen {
	private final GuiButton[] button = new GuiButton[16];

	@Override
	public void actionPerformed(GuiButton B) {
		Minecraft.getMinecraft().thePlayer.getDisplayName();
		TESPacketMoney packet;
		for (int i = 0; i <= 15; i++) {
			if (B == button[i]) {
				if (i <= 7) {
					packet = new TESPacketMoneyGive(new ItemStack(TESItems.coin, 1, i));
				} else {
					packet = new TESPacketMoneyGet(new ItemStack(TESItems.coin, 1, i - 8));
				}
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			}
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		drawDefaultBackground();
		int widthHalf = width / 2;
		int heightHalf = height / 2;
		int xOrigin = widthHalf - 128;
		int yOrigin = heightHalf - 94;
		mc.renderEngine.bindTexture(new ResourceLocation("tes:textures/gui/bank.png"));
		drawTexturedModalRect(xOrigin, yOrigin, 0, 0, 256, 188);
		TESPlayerData pd = TESLevelData.getData(mc.thePlayer);
		String balance = StatCollector.translateToLocalFormatted("tes.gui.money.balance", pd.getBalance());
		String instructions = StatCollector.translateToLocal("tes.gui.money.press");
		drawCenteredString(fontRendererObj, balance, xOrigin + 65 + 64, yOrigin - 15, 16777215);
		drawCenteredString(fontRendererObj, instructions, xOrigin + 65 + 64, yOrigin + 200, 10066329);
		String withdraw = StatCollector.translateToLocal("tes.gui.money.withdraw");
		drawCenteredString(fontRendererObj, withdraw, xOrigin + 65 + 64, yOrigin + 35, 16777215);
		String transfer = StatCollector.translateToLocal("tes.gui.money.transfer");
		drawCenteredString(fontRendererObj, transfer, xOrigin + 65 + 64, yOrigin + 105, 16777215);
		int x = xOrigin + 10;
		for (int i = 0; i < TESItemCoin.VALUES.length; i++) {
			RenderHelper.enableGUIStandardItemLighting();
			mc.renderEngine.bindTexture(new ResourceLocation("tes:textures/gui/bank.png"));
			drawTexturedModalRect(x, yOrigin + 50, 0, 188, 27, 27);
			itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(TESItems.coin, 0, i), x + 5, yOrigin + 50 + 5);
			mc.renderEngine.bindTexture(new ResourceLocation("tes:textures/gui/bank.png"));
			RenderHelper.enableGUIStandardItemLighting();
			drawTexturedModalRect(x, yOrigin + 120, 27, 188, 27, 27);
			itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.renderEngine, new ItemStack(TESItems.coin, 0, i), x + 5, yOrigin + 120 + 5);
			RenderHelper.disableStandardItemLighting();
			x += 30;
		}
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		int widthHalf = width / 2;
		int heightHalf = height / 2;
		int xOrigin = widthHalf - 118;
		int row1 = xOrigin;
		int row2 = xOrigin;
		for (int i = 0; i <= 15; i++) {
			if (i <= 7) {
				button[i] = new TESGuiButtonIronBank(i, row1, heightHalf - 44);
				row1 += 30;
			} else {
				button[i] = new TESGuiButtonIronBank(i, row2, heightHalf + 26);
				row2 += 30;
			}
			buttonList.add(button[i]);
		}
	}
}