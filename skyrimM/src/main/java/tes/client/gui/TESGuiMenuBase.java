package tes.client.gui;

import tes.client.TESKeyHandler;
import net.minecraft.client.gui.GuiButton;

public abstract class TESGuiMenuBase extends TESGuiScreenBase {
	protected GuiButton goBack;

	protected int sizeX = 200;
	protected int sizeY = 256;
	protected int guiLeft;
	protected int guiTop;

	@Override
	public void actionPerformed(GuiButton button) {
		super.actionPerformed(button);
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - sizeX) / 2;
		guiTop = (height - sizeY) / 2;
	}

	@Override
	public void keyTyped(char c, int i) {
		if (i == TESKeyHandler.KEY_BINDING_MENU.getKeyCode()) {
			mc.displayGuiScreen(new TESGuiMenu());
			return;
		}
		super.keyTyped(c, i);
	}

	protected int getSizeX() {
		return sizeX;
	}
}