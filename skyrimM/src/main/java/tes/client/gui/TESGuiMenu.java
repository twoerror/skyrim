package tes.client.gui;

import tes.client.TESTickHandlerClient;
import tes.common.TESDimension;
import tes.common.network.TESPacketCheckMenuPrompt;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketMenuPrompt;
import tes.common.quest.TESMiniQuestWelcome;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class TESGuiMenu extends TESGuiScreenBase {
	public static final ResourceLocation MENU_ICONS_TEXTURE = new ResourceLocation("tes:textures/gui/menu_icons.png");

	private static Class<? extends TESGuiMenuBase> lastMenuScreen;

	private boolean sentCheckPacket;

	public static GuiScreen openMenu(EntityPlayer entityplayer) {
		//boolean[] map_factions = TESMiniQuestWelcome.forceMenuMapFactions(entityplayer);
		//if (map_factions[0]) {
	//		return new TESGuiMap();
		//}
		//if (map_factions[1]) {
		//	return new TESGuiFactions();
		//}
		if (lastMenuScreen != null) {
			try {
				return lastMenuScreen.getConstructor().newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return new TESGuiMenu();
	}

	public static void resetLastMenuScreen() {
		lastMenuScreen = null;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		TESGuiMenuBase screen;
		if (button.enabled && button instanceof TESGuiButtonMenu && (screen = ((TESGuiButtonMenu) button).openMenu()) != null) {
			mc.displayGuiScreen(screen);
			lastMenuScreen = screen.getClass();
			return;
		}
		super.actionPerformed(button);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		String title = StatCollector.translateToLocalFormatted("tes.gui.menu", TESDimension.GAME_OF_THRONES.getTranslatedDimensionName());
		fontRendererObj.drawStringWithShadow(title, width / 2 - fontRendererObj.getStringWidth(title) / 2, height / 2 - 80, 16777215);
		super.drawScreen(i, j, f);
		for (Object obj : buttonList) {
			TESGuiButtonMenu button;
			if (obj instanceof TESGuiButtonMenu && (button = (TESGuiButtonMenu) obj).func_146115_a() && button.displayString != null) {
				float z = zLevel;
				drawCreativeTabHoveringText(button.displayString, i, j);
				GL11.glDisable(2896);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				zLevel = z;
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		resetLastMenuScreen();
		int midX = width / 2;
		int midY = height / 2;
		buttonList.add(new TESGuiButtonMenu(2, 0, 0, TESGuiAchievements.class, StatCollector.translateToLocal("tes.gui.achievements"), 30));
		buttonList.add(new TESGuiButtonMenu(3, 0, 0, TESGuiMap.class, StatCollector.translateToLocal("tes.gui.map"), 50));
		buttonList.add(new TESGuiButtonMenu(4, 0, 0, TESGuiFactions.class, StatCollector.translateToLocal("tes.gui.factions"), 33));
		buttonList.add(new TESGuiButtonMenu(8, 0, 0, TESGuiLanguages.class, StatCollector.translateToLocal("tes.gui.languages"), 20));
		buttonList.add(new TESGuiButtonMenu(6, 0, 0, TESGuiFellowships.class, StatCollector.translateToLocal("tes.gui.fellowships"), 25));
		buttonList.add(new TESGuiButtonMenu(7, 0, 0, TESGuiTitles.class, StatCollector.translateToLocal("tes.gui.titles"), 20));
		buttonList.add(new TESGuiButtonMenu(5, 0, 0, TESGuiShields.class, StatCollector.translateToLocal("tes.gui.atribute"), 31));
		buttonList.add(new TESGuiButtonMenu(1, 0, 0, TESGuiOptions.class, StatCollector.translateToLocal("tes.gui.options"), 24));
		ArrayList<TESGuiButtonMenu> menuButtons = new ArrayList<>();
		for (Object obj : buttonList) {
			if (obj instanceof TESGuiButtonMenu) {
				TESGuiButtonMenu button = (TESGuiButtonMenu) obj;
				button.enabled = button.canDisplayMenu();
				menuButtons.add(button);
			}
		}
		int numButtons = menuButtons.size();
		int numTopRowButtons = (numButtons - 1) / 2 + 1;
		int numBtmRowButtons = numButtons - numTopRowButtons;
		int topRowLeft = midX - (numTopRowButtons * 32 + (numTopRowButtons - 1) * 10) / 2;
		int btmRowLeft = midX - (numBtmRowButtons * 32 + (numBtmRowButtons - 1) * 10) / 2;
		for (int l = 0; l < numButtons; ++l) {
			TESGuiButtonMenu button2 = menuButtons.get(l);
			if (l < numTopRowButtons) {
				button2.xPosition = topRowLeft + l * 42;
				button2.yPosition = midY - 5 - 32;
			} else {
				button2.xPosition = btmRowLeft + (l - numTopRowButtons) * 42;
				button2.yPosition = midY + 5;
			}
		}
	}

	@Override
	public void keyTyped(char c, int i) {
		for (Object obj : buttonList) {
			if (obj instanceof TESGuiButtonMenu) {
				TESGuiButtonMenu button = (TESGuiButtonMenu) obj;
				if (button.visible && button.enabled && button.getMenuKeyCode() >= 0 && i == button.getMenuKeyCode()) {
					actionPerformed(button);
					return;
				}
			}
		}
		super.keyTyped(c, i);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int i, int j) {
		super.setWorldAndResolution(mc, i, j);
		if (mc.thePlayer != null) {
			TESPacketCheckMenuPrompt packet;
			if (!sentCheckPacket) {
				TESTickHandlerClient.setRenderMenuPrompt(false);
				packet = new TESPacketCheckMenuPrompt(TESPacketMenuPrompt.Type.MENU);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
				sentCheckPacket = true;
			}
		}
	}
}