package tes.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.StatCollector;

public abstract class TESGuiMenuBaseReturn extends TESGuiMenuBase {
	protected static final RenderItem ITEM_RENDERER = new RenderItem();

	protected GuiButton buttonMenuReturn;

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled && button == buttonMenuReturn) {
			mc.displayGuiScreen(new TESGuiMenu());
		}
		super.actionPerformed(button);
	}

	@Override
	public void initGui() {
		super.initGui();
		int buttonH = 20;
		int buttonGap = 35;
		int minGap = 10;
		buttonMenuReturn = new TESGuiButtonLeftRight(1000, true, 0, guiTop + (sizeY + buttonH) / 4, StatCollector.translateToLocal("tes.gui.menuButton"));
		buttonList.add(buttonMenuReturn);
		buttonMenuReturn.xPosition = Math.min(buttonGap, guiLeft - minGap - buttonMenuReturn.width);
	}
}