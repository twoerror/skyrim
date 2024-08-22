package tes.client.gui;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.info.TESHireableInfo;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.StatCollector;

public class TESGuiHiredFarmer extends TESGuiHiredNPC {
	private TESGuiButtonOptions buttonGuardMode;
	private TESGuiSlider sliderGuardRange;

	public TESGuiHiredFarmer(TESEntityNPC npc) {
		super(npc);
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button instanceof TESGuiSlider) {
			return;
		}
		if (button.enabled) {
			sendActionPacket(button.id);
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		super.drawScreen(i, j, f);
		String s = theNPC.getHireableInfo().getStatusString();
		fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 50, 4210752);
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonGuardMode = new TESGuiButtonOptions(0, guiLeft + X_SIZE / 2 - 80, guiTop + 70, 160, 20, StatCollector.translateToLocal("tes.gui.farmer.mode"));
		buttonList.add(buttonGuardMode);
		buttonGuardMode.setState(theNPC.getHireableInfo().isGuardMode());
		sliderGuardRange = new TESGuiSlider(1, guiLeft + X_SIZE / 2 - 80, guiTop + 94, 160, 20, StatCollector.translateToLocal("tes.gui.farmer.range"));
		buttonList.add(sliderGuardRange);
		sliderGuardRange.setMinMaxValues(TESHireableInfo.GUARD_RANGE_MIN, TESHireableInfo.GUARD_RANGE_MAX);
		sliderGuardRange.setSliderValue(theNPC.getHireableInfo().getGuardRange());
		sliderGuardRange.visible = theNPC.getHireableInfo().isGuardMode();
		buttonList.add(new TESGuiButtonOptions(2, guiLeft + X_SIZE / 2 - 80, guiTop + 142, 160, 20, StatCollector.translateToLocal("tes.gui.farmer.openInv")));
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonGuardMode.setState(theNPC.getHireableInfo().isGuardMode());
		sliderGuardRange.visible = theNPC.getHireableInfo().isGuardMode();
		if (sliderGuardRange.isDragging()) {
			int i = sliderGuardRange.getSliderValue();
			theNPC.getHireableInfo().setGuardRange(i);
			sendActionPacket(sliderGuardRange.id, i);
		}
	}
}