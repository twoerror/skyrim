package tes.client.gui;

import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.inanimate.TESEntityNPCRespawner;
import tes.common.network.TESPacketEditNPCRespawner;
import tes.common.network.TESPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;

public class TESGuiNPCRespawner extends TESGuiScreenBase {
	private static final int X_SIZE = 256;

	private final TESEntityNPCRespawner theSpawner;

	private TESGuiButtonOptions buttonMounts;

	private GuiTextField textSpawnClass1;
	private GuiTextField textSpawnClass2;

	private TESGuiSlider sliderCheckHorizontal;
	private TESGuiSlider sliderCheckVerticalMin;
	private TESGuiSlider sliderCheckVerticalMax;
	private TESGuiSlider sliderSpawnCap;
	private TESGuiSlider sliderBlockEnemy;
	private TESGuiSlider sliderSpawnHorizontal;
	private TESGuiSlider sliderSpawnVerticalMin;
	private TESGuiSlider sliderSpawnVerticalMax;
	private TESGuiSlider sliderHomeRange;
	private TESGuiSlider sliderSpawnIntervalM;
	private TESGuiSlider sliderSpawnIntervalS;
	private TESGuiSlider sliderNoPlayerRange;

	private GuiButton buttonDestroy;

	private boolean destroySpawner;
	private int guiLeft;
	private int guiTop;

	public TESGuiNPCRespawner(TESEntityNPCRespawner entity) {
		theSpawner = entity;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button instanceof TESGuiSlider) {
			return;
		}
		if (button.enabled) {
			if (button == buttonMounts) {
				theSpawner.toggleMountSetting();
			}
			if (button == buttonDestroy) {
				destroySpawner = true;
				mc.thePlayer.closeScreen();
			}
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		int i1 = i;
		int j1 = j;
		drawDefaultBackground();
		String s = StatCollector.translateToLocal("tes.gui.npcRespawner.title");
		fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop, 16777215);
		textSpawnClass1.drawTextBox();
		textSpawnClass2.drawTextBox();
		s = StatCollector.translateToLocal("tes.gui.npcRespawner.spawnClass1");
		fontRendererObj.drawString(s, textSpawnClass1.xPosition + 3, textSpawnClass1.yPosition - fontRendererObj.FONT_HEIGHT - 3, 13421772);
		s = StatCollector.translateToLocal("tes.gui.npcRespawner.spawnClass2");
		fontRendererObj.drawString(s, textSpawnClass2.xPosition + 3, textSpawnClass2.yPosition - fontRendererObj.FONT_HEIGHT - 3, 13421772);
		if (theSpawner.getMountSetting() == 0) {
			buttonMounts.setState(StatCollector.translateToLocal("tes.gui.npcRespawner.mounts.0"));
		} else if (theSpawner.getMountSetting() == 1) {
			buttonMounts.setState(StatCollector.translateToLocal("tes.gui.npcRespawner.mounts.1"));
		} else {
			buttonMounts.setState(StatCollector.translateToLocal("tes.gui.npcRespawner.mounts.2"));
		}
		if (theSpawner.getBlockEnemySpawns() > 0) {
			sliderBlockEnemy.setOverrideStateString(null);
		} else {
			sliderBlockEnemy.setOverrideStateString(StatCollector.translateToLocal("tes.gui.npcRespawner.blockEnemy.off"));
		}
		if (theSpawner.getHomeRange() >= 0) {
			sliderHomeRange.setOverrideStateString(null);
		} else {
			sliderHomeRange.setOverrideStateString(StatCollector.translateToLocal("tes.gui.npcRespawner.homeRange.off"));
		}
		String timepre = StatCollector.translateToLocal("tes.gui.npcRespawner.spawnInterval");
		int timepreX = sliderSpawnIntervalM.xPosition - 5 - fontRendererObj.getStringWidth(timepre);
		int timepreY = sliderSpawnIntervalM.yPosition + sliderSpawnIntervalM.height / 2 - fontRendererObj.FONT_HEIGHT / 2;
		fontRendererObj.drawString(timepre, timepreX, timepreY, 16777215);
		String timesplit = ":";
		int timesplitX = (sliderSpawnIntervalM.xPosition + sliderSpawnIntervalM.width + sliderSpawnIntervalS.xPosition) / 2 - fontRendererObj.getStringWidth(timesplit) / 2;
		int timesplitY = sliderSpawnIntervalM.yPosition + sliderSpawnIntervalM.height / 2 - fontRendererObj.FONT_HEIGHT / 2;
		fontRendererObj.drawString(timesplit, timesplitX, timesplitY, 16777215);
		super.drawScreen(i1, j1, f);
		updateSliders();
		if (sliderBlockEnemy.enabled && sliderBlockEnemy.func_146115_a() && !sliderBlockEnemy.isDragging()) {
			String tooltip = StatCollector.translateToLocal("tes.gui.npcRespawner.blockEnemy.tooltip");
			int border = 3;
			int stringWidth = mc.fontRenderer.getStringWidth(tooltip);
			int stringHeight = mc.fontRenderer.FONT_HEIGHT;
			int offset = 10;
			drawRect(i1 += offset, j1 += offset, i1 + stringWidth + border * 2, j1 + stringHeight + border * 2, -1073741824);
			mc.fontRenderer.drawString(tooltip, i1 + border, j1 + border, 16777215);
		}
	}

	@Override
	public void initGui() {
		guiLeft = (width - X_SIZE) / 2;
		int ySize = 280;
		guiTop = (height - ySize) / 2;
		textSpawnClass1 = new GuiTextField(fontRendererObj, guiLeft + X_SIZE / 2 - 190, guiTop + 35, 180, 20);
		if (theSpawner.getSpawnClass1() != null) {
			textSpawnClass1.setText(TESEntityRegistry.getStringFromClass(theSpawner.getSpawnClass1()));
		}
		textSpawnClass2 = new GuiTextField(fontRendererObj, guiLeft + X_SIZE / 2 + 10, guiTop + 35, 180, 20);
		if (theSpawner.getSpawnClass2() != null) {
			textSpawnClass2.setText(TESEntityRegistry.getStringFromClass(theSpawner.getSpawnClass2()));
		}
		sliderCheckHorizontal = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 180, guiTop + 70, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.checkHorizontal"));
		buttonList.add(sliderCheckHorizontal);
		sliderCheckHorizontal.setMinMaxValues(0, 64);
		sliderCheckHorizontal.setSliderValue(theSpawner.getCheckHorizontalRange());
		sliderCheckVerticalMin = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 180, guiTop + 95, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.checkVerticalMin"));
		buttonList.add(sliderCheckVerticalMin);
		sliderCheckVerticalMin.setMinMaxValues(-64, 64);
		sliderCheckVerticalMin.setSliderValue(theSpawner.getCheckVerticalMin());
		sliderCheckVerticalMax = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 180, guiTop + 120, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.checkVerticalMax"));
		buttonList.add(sliderCheckVerticalMax);
		sliderCheckVerticalMax.setMinMaxValues(-64, 64);
		sliderCheckVerticalMax.setSliderValue(theSpawner.getCheckVerticalMax());
		sliderSpawnCap = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 180, guiTop + 145, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.spawnCap"));
		buttonList.add(sliderSpawnCap);
		sliderSpawnCap.setMinMaxValues(0, 64);
		sliderSpawnCap.setSliderValue(theSpawner.getSpawnCap());
		sliderBlockEnemy = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 180, guiTop + 170, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.blockEnemy"));
		buttonList.add(sliderBlockEnemy);
		sliderBlockEnemy.setMinMaxValues(0, 64);
		sliderBlockEnemy.setSliderValue(theSpawner.getBlockEnemySpawns());
		sliderSpawnHorizontal = new TESGuiSlider(0, guiLeft + X_SIZE / 2 + 20, guiTop + 70, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.spawnHorizontal"));
		buttonList.add(sliderSpawnHorizontal);
		sliderSpawnHorizontal.setMinMaxValues(0, 64);
		sliderSpawnHorizontal.setSliderValue(theSpawner.getSpawnHorizontalRange());
		sliderSpawnVerticalMin = new TESGuiSlider(0, guiLeft + X_SIZE / 2 + 20, guiTop + 95, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.spawnVerticalMin"));
		buttonList.add(sliderSpawnVerticalMin);
		sliderSpawnVerticalMin.setMinMaxValues(-64, 64);
		sliderSpawnVerticalMin.setSliderValue(theSpawner.getSpawnVerticalMin());
		sliderSpawnVerticalMax = new TESGuiSlider(0, guiLeft + X_SIZE / 2 + 20, guiTop + 120, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.spawnVerticalMax"));
		buttonList.add(sliderSpawnVerticalMax);
		sliderSpawnVerticalMax.setMinMaxValues(-64, 64);
		sliderSpawnVerticalMax.setSliderValue(theSpawner.getSpawnVerticalMax());
		sliderHomeRange = new TESGuiSlider(0, guiLeft + X_SIZE / 2 + 20, guiTop + 145, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.homeRange"));
		buttonList.add(sliderHomeRange);
		sliderHomeRange.setMinMaxValues(-1, 64);
		sliderHomeRange.setSliderValue(theSpawner.getHomeRange());
		buttonMounts = new TESGuiButtonOptions(0, guiLeft + X_SIZE / 2 + 20, guiTop + 170, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.mounts"));
		buttonList.add(buttonMounts);
		sliderSpawnIntervalM = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 100 - 5, guiTop + 195, 100, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.spawnIntervalM"));
		buttonList.add(sliderSpawnIntervalM);
		sliderSpawnIntervalM.setMinMaxValues(0, 60);
		sliderSpawnIntervalM.setValueOnly();
		sliderSpawnIntervalM.setSliderValue(theSpawner.getSpawnInterval() / 20 / 60);
		sliderSpawnIntervalS = new TESGuiSlider(0, guiLeft + X_SIZE / 2 + 5, guiTop + 195, 100, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.spawnIntervalS"));
		buttonList.add(sliderSpawnIntervalS);
		sliderSpawnIntervalS.setMinMaxValues(0, 59);
		sliderSpawnIntervalS.setValueOnly();
		sliderSpawnIntervalS.setNumberDigits(2);
		sliderSpawnIntervalS.setSliderValue(theSpawner.getSpawnInterval() / 20 % 60);
		sliderNoPlayerRange = new TESGuiSlider(0, guiLeft + X_SIZE / 2 - 80, guiTop + 220, 160, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.noPlayerRange"));
		buttonList.add(sliderNoPlayerRange);
		sliderNoPlayerRange.setMinMaxValues(0, 64);
		sliderNoPlayerRange.setSliderValue(theSpawner.getNoPlayerRange());
		buttonDestroy = new TESGuiButton(0, guiLeft + X_SIZE / 2 - 50, guiTop + 255, 100, 20, StatCollector.translateToLocal("tes.gui.npcRespawner.destroy"));
		buttonList.add(buttonDestroy);
	}

	@Override
	public void keyTyped(char c, int i) {
		if (textSpawnClass1.getVisible() && textSpawnClass1.textboxKeyTyped(c, i) || textSpawnClass2.getVisible() && textSpawnClass2.textboxKeyTyped(c, i)) {
			return;
		}
		super.keyTyped(c, i);
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		textSpawnClass1.mouseClicked(i, j, k);
		textSpawnClass2.mouseClicked(i, j, k);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		sendSpawnerData();
	}

	private void sendSpawnerData() {
		String s1 = textSpawnClass1.getText();
		String s2 = textSpawnClass2.getText();
		if (!StringUtils.isNullOrEmpty(s1)) {
			theSpawner.setSpawnClass1(TESEntityRegistry.getClassFromString(s1));
		}
		if (!StringUtils.isNullOrEmpty(s2)) {
			theSpawner.setSpawnClass2(TESEntityRegistry.getClassFromString(s2));
		}
		TESPacketEditNPCRespawner packet = new TESPacketEditNPCRespawner(theSpawner);
		packet.setDestroy(destroySpawner);
		TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		textSpawnClass1.updateCursorCounter();
		textSpawnClass2.updateCursorCounter();
	}

	private void updateSliders() {
		if (sliderCheckHorizontal.isDragging()) {
			theSpawner.setCheckHorizontalRange(sliderCheckHorizontal.getSliderValue());
		}
		if (sliderCheckVerticalMin.isDragging()) {
			theSpawner.setCheckVerticalMin(sliderCheckVerticalMin.getSliderValue());
			if (theSpawner.getCheckVerticalMax() < theSpawner.getCheckVerticalMin()) {
				theSpawner.setCheckVerticalMax(theSpawner.getCheckVerticalMin());
				sliderCheckVerticalMax.setSliderValue(theSpawner.getCheckVerticalMax());
			}
		}
		if (sliderCheckVerticalMax.isDragging()) {
			theSpawner.setCheckVerticalMax(sliderCheckVerticalMax.getSliderValue());
			if (theSpawner.getCheckVerticalMin() > theSpawner.getCheckVerticalMax()) {
				theSpawner.setCheckVerticalMin(theSpawner.getCheckVerticalMax());
				sliderCheckVerticalMin.setSliderValue(theSpawner.getCheckVerticalMin());
			}
		}
		if (sliderSpawnCap.isDragging()) {
			theSpawner.setSpawnCap(sliderSpawnCap.getSliderValue());
		}
		if (sliderBlockEnemy.isDragging()) {
			theSpawner.setBlockEnemySpawns(sliderBlockEnemy.getSliderValue());
		}
		if (sliderSpawnHorizontal.isDragging()) {
			theSpawner.setSpawnHorizontalRange(sliderSpawnHorizontal.getSliderValue());
		}
		if (sliderSpawnVerticalMin.isDragging()) {
			theSpawner.setSpawnVerticalMin(sliderSpawnVerticalMin.getSliderValue());
			if (theSpawner.getSpawnVerticalMax() < theSpawner.getSpawnVerticalMin()) {
				theSpawner.setSpawnVerticalMax(theSpawner.getSpawnVerticalMin());
				sliderSpawnVerticalMax.setSliderValue(theSpawner.getSpawnVerticalMax());
			}
		}
		if (sliderSpawnVerticalMax.isDragging()) {
			theSpawner.setSpawnVerticalMax(sliderSpawnVerticalMax.getSliderValue());
			if (theSpawner.getSpawnVerticalMin() > theSpawner.getSpawnVerticalMax()) {
				theSpawner.setSpawnVerticalMin(theSpawner.getSpawnVerticalMax());
				sliderSpawnVerticalMin.setSliderValue(theSpawner.getSpawnVerticalMin());
			}
		}
		if (sliderHomeRange.isDragging()) {
			theSpawner.setHomeRange(sliderHomeRange.getSliderValue());
		}
		if (sliderSpawnIntervalM.isDragging() || sliderSpawnIntervalS.isDragging()) {
			if (sliderSpawnIntervalM.getSliderValue() == 0) {
				int s = sliderSpawnIntervalS.getSliderValue();
				s = Math.max(s, 1);
				sliderSpawnIntervalS.setSliderValue(s);
			}
			theSpawner.setSpawnInterval((sliderSpawnIntervalM.getSliderValue() * 60 + sliderSpawnIntervalS.getSliderValue()) * 20);
		}
		if (sliderNoPlayerRange.isDragging()) {
			theSpawner.setNoPlayerRange(sliderNoPlayerRange.getSliderValue());
		}
	}
}