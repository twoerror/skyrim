package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketSetOption;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TESGuiOptions extends TESGuiMenuBase {
	private TESGuiButtonOptions buttonFriendlyFire;
	private TESGuiButtonOptions buttonHiredDeathMessages;
	private TESGuiButtonOptions buttonAlignment;
	private TESGuiButtonOptions buttonMapLocation;
	private TESGuiButtonOptions buttonConquest;
	private TESGuiButtonOptions buttonFeminineRank;

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button instanceof TESGuiButtonOptions) {
				IMessage packet = new TESPacketSetOption(button.id);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else if (button == goBack) {
				mc.displayGuiScreen(new TESGuiMenu());
			} else {
				super.actionPerformed(button);
			}
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		String s = StatCollector.translateToLocal("tes.gui.options.worldSettings");
		fontRendererObj.drawString(s, guiLeft + 100 - fontRendererObj.getStringWidth(s) / 2, guiTop + 10, 16777215);
		TESPlayerData pd = TESLevelData.getData(mc.thePlayer);
		buttonFriendlyFire.setState(pd.getFriendlyFire());
		buttonHiredDeathMessages.setState(pd.getEnableHiredDeathMessages());
		buttonAlignment.setState(!pd.getHideAlignment());
		buttonMapLocation.setState(!pd.getHideMapLocation());
		buttonConquest.setState(pd.getEnableConquestKills());
		buttonFeminineRank.setState(pd.getFeminineRanks());
		super.drawScreen(i, j, f);
		for (GuiButton element : (List<GuiButton>) buttonList) {
			if (element instanceof TESGuiButtonOptions) {
				((TESGuiButtonOptions) element).drawTooltip(mc, i, j);
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		guiTop = (height - sizeY) / 2 + 10;
		int buttonX = guiLeft + sizeX / 2 - 100;
		int buttonY = guiTop + 40;
		buttonFriendlyFire = new TESGuiButtonOptions(0, buttonX, buttonY, 200, 20, "tes.gui.options.friendlyFire");
		buttonList.add(buttonFriendlyFire);
		buttonHiredDeathMessages = new TESGuiButtonOptions(1, buttonX, buttonY + 24, 200, 20, "tes.gui.options.hiredDeathMessages");
		buttonList.add(buttonHiredDeathMessages);
		buttonAlignment = new TESGuiButtonOptions(2, buttonX, buttonY + 48, 200, 20, "tes.gui.options.showAlignment");
		buttonList.add(buttonAlignment);
		buttonMapLocation = new TESGuiButtonOptions(3, buttonX, buttonY + 72, 200, 20, "tes.gui.options.showMapLocation");
		buttonList.add(buttonMapLocation);
		buttonConquest = new TESGuiButtonOptions(5, buttonX, buttonY + 96, 200, 20, "tes.gui.options.conquest");
		buttonList.add(buttonConquest);
		buttonFeminineRank = new TESGuiButtonOptions(4, buttonX, buttonY + 120, 200, 20, "tes.gui.options.femRank");
		buttonList.add(buttonFeminineRank);
		goBack = new TESGuiButton(7, buttonX, buttonY + 144, 200, 20, StatCollector.translateToLocal("tes.gui.menuButton"));
		buttonList.add(goBack);
	}
}