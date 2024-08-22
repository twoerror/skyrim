package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.database.TESCapes;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketSelectCape;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class TESGuiCapes extends TESGuiMenuBase {
	private static final ModelBiped PLAYER_MODEL = new ModelBiped();

	private static int currentCapeTypeID;
	private static int currentCapeID;

	static {
		PLAYER_MODEL.isChild = false;
	}

	private GuiButton capeLeft;
	private GuiButton capeRight;
	private GuiButton capeSelect;
	private GuiButton capeRemove;
	private GuiButton goToShield;
	private GuiButton changeCategory;

	private TESCapes.CapeType currentCapeType;
	private TESCapes currentCape;

	private float modelRotation;
	private float modelRotationPrev;

	private int isMouseDown;
	private int prevMouseX;
	private int modelX;
	private int modelY;
	private int mouseX;
	private int mouseY;

	public TESGuiCapes() {
		modelRotationPrev = modelRotation = -140.0f;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == capeLeft) {
				updateCurrentCape(-1, 0);
			} else if (button == goBack) {
				mc.displayGuiScreen(new TESGuiMenu());
			} else if (button == goToShield) {
				mc.displayGuiScreen(new TESGuiShields());
			} else if (button == capeSelect) {
				updateCurrentCape(0, 0);
				IMessage packet = new TESPacketSelectCape(currentCape);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else if (button == capeRight) {
				updateCurrentCape(1, 0);
			} else if (button == capeRemove) {
				updateCurrentCape(0, 0);
				IMessage packet = new TESPacketSelectCape(null);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else if (button == changeCategory) {
				updateCurrentCape(0, 1);
			} else {
				super.actionPerformed(button);
			}
		}
	}

	private boolean canGoLeft() {
		for (int i = 0; i <= currentCapeID - 1; ++i) {
			TESCapes cape = currentCapeType.getCapes().get(i);
			if (cape.canDisplay(mc.thePlayer)) {
				return true;
			}
		}
		return false;
	}

	private boolean canGoRight() {
		for (int i = currentCapeID + 1; i <= currentCapeType.getCapes().size() - 1; ++i) {
			TESCapes cape = currentCapeType.getCapes().get(i);
			if (cape.canDisplay(mc.thePlayer)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		mouseX = i;
		mouseY = j;
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glEnable(2903);
		RenderHelper.enableStandardItemLighting();
		GL11.glPushMatrix();
		GL11.glDisable(2884);
		GL11.glEnable(32826);
		GL11.glEnable(3008);
		GL11.glTranslatef(modelX, modelY, 50.0f);
		float scale = 55.0f;
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(-30.0f, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(90 + modelRotationPrev + (modelRotation - modelRotationPrev) * f, 0.0f, 1.0f, 0.0f);
		mc.getTextureManager().bindTexture(mc.thePlayer.getLocationSkin());
		PLAYER_MODEL.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
		mc.getTextureManager().bindTexture(currentCape.getCapeTexture());
		GL11.glTranslatef(0.0f, 0.0f, 0.125f);
		GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-10.0f, 1.0f, 0.0f, 0.0f);
		PLAYER_MODEL.renderCloak(0.0625f);
		GL11.glDisable(32826);
		GL11.glEnable(2884);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(3553);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		int x = guiLeft + sizeX / 2;
		int y = guiTop + 145;
		String s = currentCape.getCapeName();
		drawCenteredString(s, x, y, 16777215);
		y += fontRendererObj.FONT_HEIGHT * 2;
		List<String> desc = fontRendererObj.listFormattedStringToWidth(currentCape.getCapeDesc(), 220);
		for (String element : desc) {
			s = element;
			drawCenteredString(s, x, y, 16777215);
			y += fontRendererObj.FONT_HEIGHT;
		}
		capeLeft.enabled = canGoLeft();
		capeSelect.enabled = currentCape.canPlayerWear(mc.thePlayer);
		capeSelect.displayString = getPlayerEquippedCape() == currentCape ? StatCollector.translateToLocal("tes.gui.capes.selected") : StatCollector.translateToLocal("tes.gui.capes.select");
		capeRight.enabled = canGoRight();
		capeRemove.enabled = getPlayerEquippedCape() != null && getPlayerEquippedCape() == currentCape;
		changeCategory.displayString = currentCapeType.getDisplayName();
		super.drawScreen(i, j, f);
	}

	private TESCapes getPlayerEquippedCape() {
		return TESLevelData.getData(mc.thePlayer).getCape();
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - sizeX) / 2 + 100;
		guiTop = (height - sizeY) / 2;
		modelX = guiLeft + sizeX / 2;
		modelY = guiTop + 40;
		capeLeft = new TESGuiButtonArrows(0, true, guiLeft + sizeX / 2 - 64, guiTop + 207);
		buttonList.add(capeLeft);
		capeSelect = new TESGuiButton(1, guiLeft + sizeX / 2 - 40, guiTop + 195, 80, 20, StatCollector.translateToLocal("tes.gui.capes.select"));
		buttonList.add(capeSelect);
		capeRight = new TESGuiButtonArrows(2, false, guiLeft + sizeX / 2 + 44, guiTop + 207);
		buttonList.add(capeRight);
		capeRemove = new TESGuiButton(3, guiLeft + sizeX / 2 - 40, guiTop + 219, 80, 20, StatCollector.translateToLocal("tes.gui.capes.remove"));
		buttonList.add(capeRemove);
		changeCategory = new TESGuiButton(4, guiLeft + sizeX / 2 - 290, guiTop + 90, 160, 20, "");
		buttonList.add(changeCategory);
		goBack = new TESGuiButton(5, guiLeft + sizeX / 2 - 290, guiTop + 150, 160, 20, StatCollector.translateToLocal("tes.gui.menuButton"));
		buttonList.add(goBack);
		goToShield = new TESGuiButton(6, guiLeft + sizeX / 2 - 290, guiTop + 120, 160, 20, StatCollector.translateToLocal("tes.gui.shields"));
		buttonList.add(goToShield);
		TESCapes equippedCape = getPlayerEquippedCape();
		if (equippedCape != null) {
			currentCapeTypeID = equippedCape.getCapeType().ordinal();
			currentCapeID = equippedCape.getCapeID();
		}
		updateCurrentCape(0, 0);
	}

	private void updateCurrentCape(int cape, int type) {
		if (cape != 0) {
			currentCapeID += cape;
			currentCapeID = Math.max(currentCapeID, 0);
			currentCapeID = Math.min(currentCapeID, currentCapeType.getCapes().size() - 1);
		}
		if (type != 0) {
			currentCapeTypeID += type;
			if (currentCapeTypeID > TESCapes.CapeType.values().length - 1) {
				currentCapeTypeID = 0;
			}
			if (currentCapeTypeID < 0) {
				currentCapeTypeID = TESCapes.CapeType.values().length - 1;
			}
			currentCapeID = 0;
		}
		currentCapeType = TESCapes.CapeType.values()[currentCapeTypeID];
		currentCape = currentCapeType.getCapes().get(currentCapeID);
		while (!currentCape.canDisplay(mc.thePlayer)) {
			if ((cape < 0 || type != 0) && canGoLeft()) {
				updateCurrentCape(-1, 0);
			} else if ((cape > 0 || type != 0) && canGoRight()) {
				updateCurrentCape(1, 0);
			} else {
				updateCurrentCape(0, 1);
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		modelRotationPrev = modelRotation;
		modelRotationPrev = MathHelper.wrapAngleTo180_float(modelRotationPrev);
		modelRotation = MathHelper.wrapAngleTo180_float(modelRotation);
		boolean mouseWithinModel = Math.abs(mouseX - modelX) <= 60 && Math.abs(mouseY - modelY) <= 80;
		if (Mouse.isButtonDown(0)) {
			if (isMouseDown == 0 || isMouseDown == 1) {
				if (isMouseDown == 0) {
					if (mouseWithinModel) {
						isMouseDown = 1;
					}
				} else if (mouseX != prevMouseX) {
					float move = -(mouseX - prevMouseX) * 1.0f;
					modelRotation += move;
				}
				prevMouseX = mouseX;
			}
		} else {
			isMouseDown = 0;
		}
	}
}