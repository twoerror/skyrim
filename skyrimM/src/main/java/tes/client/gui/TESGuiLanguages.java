package tes.client.gui;

import tes.TES;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Util;
import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

public class TESGuiLanguages extends TESGuiMenuBase {
	private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			switch (button.id) {
				case 1:
					switch (Util.getOSType()) {
						case OSX:
							try {
								Runtime.getRuntime().exec(new String[]{"/usr/bin/open", new File(MINECRAFT.mcDataDir, "config").getAbsolutePath()});
								return;
							} catch (IOException var7) {
								var7.printStackTrace();
								break;
							}
						case WINDOWS:
							String var2 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new File(MINECRAFT.mcDataDir, "config").getAbsolutePath());
							try {
								Runtime.getRuntime().exec(var2);
								return;
							} catch (IOException var6) {
								var6.printStackTrace();
								break;
							}
						default:
							mc.displayGuiScreen(new TESGuiMenu());
					}
					boolean var8 = false;
					try {
						Class<?> var3 = Class.forName("java.awt.Desktop");
						Object var4 = var3.getMethod("getDesktop").invoke(null);
						var3.getMethod("browse", URI.class).invoke(var4, new File(mc.mcDataDir, "config").toURI());
					} catch (Throwable var5) {
						var5.printStackTrace();
						var8 = true;
					}
					if (!var8) {
						break;
					}
					System.out.println("Opening via system class!");
					Sys.openURL("file://" + new File(MINECRAFT.mcDataDir, "config").getAbsolutePath());
					break;
				case 2:
					mc.displayGuiScreen(new TESGuiMenu());
			}
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		String s1 = StatCollector.translateToLocal("tes.gui.languages");
		fontRendererObj.drawString(s1, guiLeft + 100 - fontRendererObj.getStringWidth(s1) / 2, guiTop + 10, 16777215);
		String s2 = StatCollector.translateToLocal("tes.gui.languages.guide1") + ' ' + TES.LANGUAGES + StatCollector.translateToLocal("tes.gui.languages.guide2");
		int x = guiLeft + sizeX / 2;
		int y = guiTop + 40;
		for (Object element : fontRendererObj.listFormattedStringToWidth(s2, 220)) {
			s2 = (String) element;
			drawCenteredString(s2, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
		}
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
		TESGuiButton openFolder = new TESGuiButton(1, buttonX, buttonY + 100, 200, 20, StatCollector.translateToLocal("tes.gui.openFolder"));
		buttonList.add(openFolder);
		goBack = new TESGuiButton(2, buttonX, buttonY + 140, 200, 20, StatCollector.translateToLocal("tes.gui.menuButton"));
		buttonList.add(goBack);
	}
}