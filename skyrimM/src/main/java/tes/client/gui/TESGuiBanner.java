package tes.client.gui;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESBannerProtection;
import tes.common.entity.other.inanimate.TESEntityBanner;
import tes.common.entity.other.utils.TESBannerWhitelistEntry;
import tes.common.faction.TESAlignmentValues;
import tes.common.fellowship.TESFellowshipProfile;
import tes.common.network.TESPacketBannerRequestInvalidName;
import tes.common.network.TESPacketEditBanner;
import tes.common.network.TESPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.Arrays;
import java.util.function.Function;

public class TESGuiBanner extends TESGuiScreenBase {
	public static final ResourceLocation BANNER_TEXTURE = new ResourceLocation("tes:textures/gui/banner_edit.png");

	private static final int X_SIZE = 200;
	private static final int Y_SIZE = 250;
	private static final int SCROLL_BAR_WIDTH = 12;
	private static final int SCROLL_BAR_HEIGHT = 132;
	private static final int SCROLL_BAR_X = 181;
	private static final int SCROLL_BAR_Y = 68;
	private static final int SCROLL_WIDGET_HEIGHT = 17;
	private static final int PERM_WINDOW_BORDER = 4;
	private static final int PERM_WINDOW_WIDTH = 150;
	private static final int PERM_WINDOW_HEIGHT = 70;

	private final TESEntityBanner theBanner;

	private TESBannerProtection.Permission mouseOverPermission;
	private TESGuiButtonBanner buttonSelfProtection;
	private TESGuiButtonBanner buttonDefaultPermissions;
	private GuiTextField alignmentField;
	private GuiButton buttonMode;
	private GuiButton buttonAddSlot;
	private GuiButton buttonRemoveSlot;

	private GuiTextField[] allowedPlayers = {};

	private boolean[] invalidUsernames = {};
	private boolean[] validatedUsernames = {};
	private boolean[] checkUsernames = {};

	private boolean defaultPermissionsOpen;
	private boolean isScrolling;
	private boolean wasMouseDown;

	private float currentScroll;

	private int permissionsMouseoverIndex = -1;
	private int permissionsMouseoverY = -1;
	private int permissionsOpenIndex = -1;
	private int permissionsOpenY = -1;
	private int guiLeft;
	private int guiTop;

	public TESGuiBanner(TESEntityBanner banner) {
		theBanner = banner;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == buttonMode) {
				theBanner.setPlayerSpecificProtection(!theBanner.isPlayerSpecificProtection());
			}
			if (button == buttonSelfProtection) {
				theBanner.setSelfProtection(!theBanner.isSelfProtection());
			}
			if (button == buttonAddSlot) {
				theBanner.resizeWhitelist(theBanner.getWhitelistLength() + 1);
				refreshWhitelist();
			}
			if (button == buttonRemoveSlot) {
				theBanner.resizeWhitelist(theBanner.getWhitelistLength() - 1);
				refreshWhitelist();
			}
			if (button == buttonDefaultPermissions) {
				defaultPermissionsOpen = true;
			}
		}
	}

	private void buttonSound() {
		buttonMode.func_146113_a(mc.getSoundHandler());
	}

	private void checkUsernameValid(int index) {
		GuiTextField textBox = allowedPlayers[index];
		String username = textBox.getText();
		if (!StringUtils.isBlank(username) && !invalidUsernames[index]) {
			IMessage packet = new TESPacketBannerRequestInvalidName(theBanner, index, username);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	private void drawPermissionsWindow(int i, int j, int windowX, int windowY, String boxTitle, String boxSubtitle, Function<TESBannerProtection.Permission, Boolean> getEnabled, boolean includeFull) {
		drawRect(windowX, windowY, windowX + PERM_WINDOW_WIDTH, windowY + PERM_WINDOW_HEIGHT, -1442840576);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		fontRendererObj.drawString(boxTitle, windowX + 4, windowY + 4, 16777215);
		fontRendererObj.drawString(boxSubtitle, windowX + 4, windowY + 14, 11184810);
		mc.getTextureManager().bindTexture(BANNER_TEXTURE);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		int x = windowX + 4;
		int y = windowY + 32;
		mouseOverPermission = null;
		for (TESBannerProtection.Permission p : TESBannerProtection.Permission.values()) {
			if (includeFull || p != TESBannerProtection.Permission.FULL) {
				if (i >= x && i < x + 10 && j >= y && j < y + 10) {
					mouseOverPermission = p;
				}
				drawTexturedModalRect(x, y, 200 + (getEnabled.apply(p) ? 0 : 20) + (mouseOverPermission == p ? 10 : 0), 160 + p.ordinal() * 10, 10, 10);
				x += 14;
				if (p == TESBannerProtection.Permission.FULL) {
					x += 4;
				}
			}
		}
		if (mouseOverPermission != null) {
			String permName = StatCollector.translateToLocal("tes.gui.bannerEdit.perm." + mouseOverPermission.getCodeName());
			fontRendererObj.drawSplitString(permName, windowX + 4, windowY + 47, PERM_WINDOW_WIDTH - PERM_WINDOW_BORDER * 2, 16777215);
		}
	}

	@Override
	@SuppressWarnings("StringConcatenationMissingWhitespace")
	public void drawScreen(int i, int j, float f) {
		int windowY;
		String s;
		permissionsMouseoverIndex = -1;
		permissionsMouseoverY = -1;
		mouseOverPermission = null;
		setupScrollBar(i, j);
		alignmentField.setVisible(false);
		alignmentField.setEnabled(false);
		for (GuiTextField textBox : allowedPlayers) {
			textBox.setVisible(false);
			textBox.setEnabled(false);
		}
		drawDefaultBackground();
		mc.getTextureManager().bindTexture(BANNER_TEXTURE);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, X_SIZE, Y_SIZE);
		String title = StatCollector.translateToLocal("tes.gui.bannerEdit.title");
		fontRendererObj.drawString(title, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(title) / 2, guiTop + 6, 4210752);
		if (theBanner.isPlayerSpecificProtection()) {
			buttonMode.displayString = StatCollector.translateToLocal("tes.gui.bannerEdit.protectionMode.playerSpecific");
			s = StatCollector.translateToLocal("tes.gui.bannerEdit.protectionMode.playerSpecific.desc.1");
			fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 46, 4210752);
			s = StatCollector.translateToLocal("tes.gui.bannerEdit.protectionMode.playerSpecific.desc.2");
			fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 46 + fontRendererObj.FONT_HEIGHT, 4210752);
			s = TESFellowshipProfile.getFellowshipCodeHint();
			fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 206, 4210752);
			int start = Math.round(currentScroll * (allowedPlayers.length - 6));
			int end = start + 6 - 1;
			start = Math.max(start, 0);
			end = Math.min(end, allowedPlayers.length - 1);
			for (int index = start; index <= end; ++index) {
				int displayIndex = index - start;
				GuiTextField textBox = allowedPlayers[index];
				textBox.setVisible(true);
				textBox.setEnabled(index != 0);
				textBox.xPosition = guiLeft + X_SIZE / 2 - 70;
				textBox.yPosition = guiTop + 70 + displayIndex * (textBox.height + 4);
				textBox.drawTextBox();
				String number = index + 1 + ".";
				fontRendererObj.drawString(number, guiLeft + 24 - fontRendererObj.getStringWidth(number), textBox.yPosition + 6, 4210752);
				if (index > 0 && validatedUsernames[index]) {
					mc.getTextureManager().bindTexture(BANNER_TEXTURE);
					GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
					int permIconX = 3;
					int permIconY = 0;
					int permX = textBox.xPosition + textBox.width + permIconX;
					int permY = textBox.yPosition + permIconY;
					int permIconWidth = 10;
					boolean mouseOver = i >= permX && i < permX + permIconWidth && j >= permY && j < permY + permIconWidth;
					drawTexturedModalRect(permX, permY, 200 + (mouseOver ? permIconWidth : 0), 150, permIconWidth, permIconWidth);
					if (mouseOver) {
						permissionsMouseoverIndex = index;
						permissionsMouseoverY = textBox.yPosition;
					}
				}
			}
			if (hasScrollBar()) {
				mc.getTextureManager().bindTexture(BANNER_TEXTURE);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				drawTexturedModalRect(guiLeft + SCROLL_BAR_X, guiTop + SCROLL_BAR_Y, 200, 0, SCROLL_BAR_WIDTH, SCROLL_BAR_HEIGHT);
				int scrollBarBorder = 1;
				int scroll = (int) (currentScroll * (SCROLL_BAR_HEIGHT - scrollBarBorder * 2 - SCROLL_WIDGET_HEIGHT));
				int scrollWidgetWidth = 10;
				drawTexturedModalRect(guiLeft + SCROLL_BAR_X + scrollBarBorder, guiTop + SCROLL_BAR_Y + scrollBarBorder + scroll, 212, 0, scrollWidgetWidth, SCROLL_WIDGET_HEIGHT);
			}
		} else {
			permissionsOpenY = -1;
			permissionsOpenIndex = -1;
			buttonMode.displayString = StatCollector.translateToLocal("tes.gui.bannerEdit.protectionMode.faction");
			s = StatCollector.translateToLocalFormatted("tes.gui.bannerEdit.protectionMode.faction.desc.1");
			fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 46, 4210752);
			s = StatCollector.translateToLocalFormatted("tes.gui.bannerEdit.protectionMode.faction.desc.2", theBanner.getAlignmentProtection(), theBanner.getBannerType().getFaction().factionName());
			fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 46 + fontRendererObj.FONT_HEIGHT, 4210752);
			s = StatCollector.translateToLocal("tes.gui.bannerEdit.protectionMode.faction.desc.3");
			fontRendererObj.drawString(s, guiLeft + X_SIZE / 2 - fontRendererObj.getStringWidth(s) / 2, guiTop + 46 + fontRendererObj.FONT_HEIGHT * 2, 4210752);
			s = StatCollector.translateToLocal("tes.gui.bannerEdit.protectionMode.faction.alignment");
			fontRendererObj.drawString(s, alignmentField.xPosition, alignmentField.yPosition - fontRendererObj.FONT_HEIGHT - 3, 4210752);
			alignmentField.setVisible(true);
			alignmentField.setEnabled(true);
			alignmentField.drawTextBox();
		}
		if (permissionsOpenIndex >= 0) {
			int windowX = guiLeft + X_SIZE + PERM_WINDOW_BORDER;
			windowY = permissionsOpenY;
			String username = allowedPlayers[permissionsOpenIndex].getText();
			boolean isFellowship = TESFellowshipProfile.hasFellowshipCode(username);
			if (isFellowship) {
				username = TESFellowshipProfile.stripFellowshipCode(username);
			}
			String boxTitle = StatCollector.translateToLocal(isFellowship ? "tes.gui.bannerEdit.perms.fellowship" : "tes.gui.bannerEdit.perms.player");
			String boxSubtitle = StatCollector.translateToLocalFormatted("tes.gui.bannerEdit.perms.name", username);
			Function<TESBannerProtection.Permission, Boolean> getEnabled = p -> theBanner.getWhitelistEntry(permissionsOpenIndex).isPermissionEnabled(p);
			drawPermissionsWindow(i, j, windowX, windowY, boxTitle, boxSubtitle, getEnabled, true);
		}
		if (defaultPermissionsOpen) {
			int windowX = guiLeft + X_SIZE + PERM_WINDOW_BORDER;
			windowY = guiTop + Y_SIZE - PERM_WINDOW_HEIGHT;
			String boxTitle = StatCollector.translateToLocal("tes.gui.bannerEdit.perms.default");
			String boxSubtitle = StatCollector.translateToLocalFormatted("tes.gui.bannerEdit.perms.allPlayers");
			Function<TESBannerProtection.Permission, Boolean> getEnabled = theBanner::hasDefaultPermission;
			drawPermissionsWindow(i, j, windowX, windowY, boxTitle, boxSubtitle, getEnabled, false);
		}
		super.drawScreen(i, j, f);
		if (buttonSelfProtection.func_146115_a()) {
			float z = zLevel;
			String tooltip = StatCollector.translateToLocal("tes.gui.bannerEdit.selfProtection." + (buttonSelfProtection.isActivated() ? "on" : "off"));
			drawCreativeTabHoveringText(tooltip, i, j);
			GL11.glDisable(2896);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			zLevel = z;
		}
		if (buttonDefaultPermissions.func_146115_a()) {
			float z = zLevel;
			String tooltip = StatCollector.translateToLocal("tes.gui.bannerEdit.perms.default");
			drawCreativeTabHoveringText(tooltip, i, j);
			GL11.glDisable(2896);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			zLevel = z;
		}
		if (permissionsMouseoverIndex >= 0) {
			float z = zLevel;
			String username = allowedPlayers[permissionsMouseoverIndex].getText();
			boolean isFellowship = TESFellowshipProfile.hasFellowshipCode(username);
			String tooltip = StatCollector.translateToLocal(isFellowship ? "tes.gui.bannerEdit.perms.fellowship" : "tes.gui.bannerEdit.perms.player");
			drawCreativeTabHoveringText(tooltip, i, j);
			GL11.glDisable(2896);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			zLevel = z;
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int i = Mouse.getEventDWheel();
		if (i != 0) {
			int j = allowedPlayers.length - 6;
			if (i > 0) {
				i = 1;
			}
			if (i < 0) {
				i = -1;
			}
			currentScroll = (float) (currentScroll - (double) i / j);
			if (currentScroll < 0.0f) {
				currentScroll = 0.0f;
			}
			if (currentScroll > 1.0f) {
				currentScroll = 1.0f;
			}
		}
	}

	private boolean hasScrollBar() {
		return theBanner.isPlayerSpecificProtection();
	}

	@Override
	public void initGui() {
		guiLeft = (width - X_SIZE) / 2;
		guiTop = (height - Y_SIZE) / 2;
		buttonMode = new TESGuiButton(0, guiLeft + X_SIZE / 2 - 80, guiTop + 20, 160, 20, "");
		buttonList.add(buttonMode);
		buttonSelfProtection = new TESGuiButtonBanner(1, guiLeft + X_SIZE / 2 - 24, guiTop + 224, 212, 100);
		buttonList.add(buttonSelfProtection);
		buttonAddSlot = new TESGuiButtonBannerWhitelistSlots(0, guiLeft + 179, guiTop + 202);
		buttonList.add(buttonAddSlot);
		buttonRemoveSlot = new TESGuiButtonBannerWhitelistSlots(1, guiLeft + 187, guiTop + 202);
		buttonList.add(buttonRemoveSlot);
		buttonDefaultPermissions = new TESGuiButtonBanner(2, guiLeft + X_SIZE / 2 + 8, guiTop + 224, 200, 134);
		buttonList.add(buttonDefaultPermissions);
		buttonDefaultPermissions.setActivated(true);
		alignmentField = new GuiTextField(fontRendererObj, guiLeft + X_SIZE / 2 - 70, guiTop + 100, 130, 18);
		alignmentField.setText(String.valueOf(theBanner.getAlignmentProtection()));
		alignmentField.setEnabled(false);
		refreshWhitelist();
		for (int i = 0; i < allowedPlayers.length; ++i) {
			String name;
			GuiTextField textBox = allowedPlayers[i];
			textBox.setTextColor(16777215);
			GameProfile profile = theBanner.getWhitelistedPlayer(i);
			if (profile != null && !StringUtils.isBlank(name = profile.getName())) {
				textBox.setText(name);
				textBox.setTextColor(65280);
				validatedUsernames[i] = true;
			}
			allowedPlayers[i] = textBox;
		}
		allowedPlayers[0].setEnabled(false);
		Arrays.fill(checkUsernames, false);
	}

	@Override
	public void keyTyped(char c, int i) {
		if (alignmentField.getVisible() && alignmentField.textboxKeyTyped(c, i)) {
			return;
		}
		for (int l = 1; l < allowedPlayers.length; ++l) {
			GuiTextField textBox = allowedPlayers[l];
			if (textBox.getVisible() && textBox.textboxKeyTyped(c, i)) {
				validatedUsernames[l] = false;
				checkUsernames[l] = true;
				textBox.setTextColor(16777215);
				updateWhitelistedPlayer(l, null);
				return;
			}
		}
		if (permissionsOpenIndex >= 0 && (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode())) {
			permissionsOpenY = -1;
			permissionsOpenIndex = -1;
			return;
		}
		if (defaultPermissionsOpen && (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode())) {
			defaultPermissionsOpen = false;
			return;
		}
		super.keyTyped(c, i);
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		int dx;
		super.mouseClicked(i, j, k);
		if (alignmentField.getVisible()) {
			alignmentField.mouseClicked(i, j, k);
		}
		for (int l = 1; l < allowedPlayers.length; ++l) {
			GuiTextField textBox = allowedPlayers[l];
			if (textBox.getVisible()) {
				textBox.mouseClicked(i, j, k);
				if (!textBox.isFocused() && checkUsernames[l]) {
					checkUsernameValid(l);
					checkUsernames[l] = false;
				}
				if (textBox.isFocused() && invalidUsernames[l]) {
					invalidUsernames[l] = false;
					textBox.setTextColor(16777215);
					textBox.setText("");
				}
			}
		}
		if (permissionsMouseoverIndex >= 0) {
			permissionsOpenIndex = permissionsMouseoverIndex;
			permissionsOpenY = permissionsMouseoverY;
			permissionsMouseoverIndex = -1;
			permissionsMouseoverY = -1;
			defaultPermissionsOpen = false;
			buttonSound();
			return;
		}
		if (permissionsOpenIndex >= 0) {
			dx = i - (guiLeft + X_SIZE + PERM_WINDOW_BORDER);
			int dy = j - permissionsOpenY;
			if (dx < 0 || dx >= PERM_WINDOW_WIDTH || dy < 0 || dy >= PERM_WINDOW_HEIGHT) {
				permissionsOpenY = -1;
				permissionsOpenIndex = -1;
				return;
			}
			if (mouseOverPermission != null) {
				TESBannerWhitelistEntry entry = theBanner.getWhitelistEntry(permissionsOpenIndex);
				if (mouseOverPermission == TESBannerProtection.Permission.FULL) {
					if (entry.isPermissionEnabled(mouseOverPermission)) {
						entry.clearPermissions();
					} else {
						entry.clearPermissions();
						entry.addPermission(mouseOverPermission);
					}
				} else if (entry.isPermissionEnabled(mouseOverPermission)) {
					entry.removePermission(mouseOverPermission);
				} else {
					if (entry.isPermissionEnabled(TESBannerProtection.Permission.FULL)) {
						entry.removePermission(TESBannerProtection.Permission.FULL);
					}
					entry.addPermission(mouseOverPermission);
				}
				buttonSound();
				return;
			}
		}
		if (defaultPermissionsOpen) {
			dx = i - (guiLeft + X_SIZE + PERM_WINDOW_BORDER);
			int dy = j - (guiTop + Y_SIZE - PERM_WINDOW_HEIGHT);
			if ((dx < 0 || dx >= PERM_WINDOW_WIDTH || dy < 0 || dy >= PERM_WINDOW_HEIGHT) && !buttonDefaultPermissions.mousePressed(mc, i, j)) {
				defaultPermissionsOpen = false;
				return;
			}
			if (mouseOverPermission != null) {
				if (theBanner.hasDefaultPermission(mouseOverPermission)) {
					theBanner.removeDefaultPermission(mouseOverPermission);
				} else {
					theBanner.addDefaultPermission(mouseOverPermission);
				}
				sendBannerData(false);
				buttonSound();
			}
		}
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		sendBannerData(true);
	}

	private void refreshWhitelist() {
		int length = theBanner.getWhitelistLength();
		GuiTextField[] allowedPlayers_new = new GuiTextField[length];
		boolean[] invalidUsernames_new = new boolean[length];
		boolean[] validatedUsernames_new = new boolean[length];
		boolean[] checkUsernames_new = new boolean[length];
		for (int i = 0; i < length; ++i) {
			allowedPlayers_new[i] = i < allowedPlayers.length ? allowedPlayers[i] : new GuiTextField(fontRendererObj, 0, 0, 130, 18);
			if (i < invalidUsernames.length) {
				invalidUsernames_new[i] = invalidUsernames[i];
			}
			if (i < validatedUsernames.length) {
				validatedUsernames_new[i] = validatedUsernames[i];
			}
			if (i < checkUsernames.length) {
				checkUsernames_new[i] = checkUsernames[i];
			}
		}
		allowedPlayers = allowedPlayers_new;
		invalidUsernames = invalidUsernames_new;
		validatedUsernames = validatedUsernames_new;
		checkUsernames = checkUsernames_new;
	}

	private void sendBannerData(boolean sendWhitelist) {
		TESPacketEditBanner packet = new TESPacketEditBanner(theBanner);
		packet.setPlayerSpecificProtection(theBanner.isPlayerSpecificProtection());
		packet.setSelfProtection(theBanner.isSelfProtection());
		packet.setAlignmentProtection(theBanner.getAlignmentProtection());
		packet.setWhitelistLength(theBanner.getWhitelistLength());
		if (sendWhitelist) {
			String[] whitelistSlots = new String[allowedPlayers.length];
			int[] whitelistPerms = new int[allowedPlayers.length];
			for (int index = 1; index < allowedPlayers.length; ++index) {
				String text = allowedPlayers[index].getText();
				updateWhitelistedPlayer(index, text);
				TESBannerWhitelistEntry entry = theBanner.getWhitelistEntry(index);
				if (entry == null) {
					whitelistSlots[index] = null;
				} else {
					GameProfile profile = entry.getProfile();
					if (profile == null) {
						whitelistSlots[index] = null;
					} else {
						String username = profile.getName();
						if (StringUtils.isBlank(username)) {
							whitelistSlots[index] = null;
						} else {
							whitelistSlots[index] = username;
							whitelistPerms[index] = entry.encodePermBitFlags();
						}
					}
				}
			}
			packet.setWhitelistSlots(whitelistSlots);
			packet.setWhitelistPerms(whitelistPerms);
		}
		packet.setDefaultPerms(theBanner.getDefaultPermBitFlags());
		TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
	}

	private void setupScrollBar(int i, int j) {
		boolean isMouseDown = Mouse.isButtonDown(0);
		int i1 = guiLeft + SCROLL_BAR_X;
		int j1 = guiTop + SCROLL_BAR_Y;
		int i2 = i1 + SCROLL_BAR_WIDTH;
		int j2 = j1 + SCROLL_BAR_HEIGHT;
		if (!wasMouseDown && isMouseDown && i >= i1 && j >= j1 && i < i2 && j < j2) {
			isScrolling = true;
		}
		if (!isMouseDown) {
			isScrolling = false;
		}
		wasMouseDown = isMouseDown;
		if (isScrolling) {
			currentScroll = (j - j1 - SCROLL_WIDGET_HEIGHT / 2.0f) / ((float) (j2 - j1) - SCROLL_WIDGET_HEIGHT);
			if (currentScroll < 0.0f) {
				currentScroll = 0.0f;
			}
			if (currentScroll > 1.0f) {
				currentScroll = 1.0f;
			}
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		buttonSelfProtection.setActivated(theBanner.isSelfProtection());
		buttonAddSlot.visible = buttonRemoveSlot.visible = theBanner.isPlayerSpecificProtection();
		buttonAddSlot.enabled = theBanner.getWhitelistLength() < TESEntityBanner.WHITELIST_MAX;
		buttonRemoveSlot.enabled = theBanner.getWhitelistLength() > TESEntityBanner.WHITELIST_MIN;
		alignmentField.updateCursorCounter();
		alignmentField.setVisible(!theBanner.isPlayerSpecificProtection());
		alignmentField.setEnabled(alignmentField.getVisible());
		if (alignmentField.getVisible() && !alignmentField.isFocused()) {
			float prevAlignment = theBanner.getAlignmentProtection();
			float alignment = TESAlignmentValues.parseDisplayedAlign(alignmentField.getText());
			alignment = MathHelper.clamp_float(alignment, TESEntityBanner.ALIGNMENT_PROTECTION_MIN, TESEntityBanner.ALIGNMENT_PROTECTION_MAX);
			theBanner.setAlignmentProtection(alignment);
			alignmentField.setText(TESAlignmentValues.formatAlignForDisplay(alignment));
			if (alignment != prevAlignment) {
				sendBannerData(false);
			}
		}
		for (GuiTextField textBox : allowedPlayers) {
			textBox.updateCursorCounter();
		}
	}

	private void updateWhitelistedPlayer(int index, String username) {
		TESBannerWhitelistEntry prevEntry = theBanner.getWhitelistEntry(index);
		int prevPerms = -1;
		if (prevEntry != null) {
			prevPerms = prevEntry.encodePermBitFlags();
		}
		if (StringUtils.isBlank(username)) {
			theBanner.whitelistPlayer(index, null);
		} else {
			if (TESFellowshipProfile.hasFellowshipCode(username)) {
				String fsName = TESFellowshipProfile.stripFellowshipCode(username);
				if (StringUtils.isBlank(fsName)) {
					theBanner.whitelistPlayer(index, null);
				} else {
					theBanner.whitelistPlayer(index, new TESFellowshipProfile(null, fsName));
				}
			} else {
				theBanner.whitelistPlayer(index, new GameProfile(null, username));
			}
			if (prevPerms >= 0) {
				theBanner.getWhitelistEntry(index).decodePermBitFlags(prevPerms);
			}
		}
	}

	public void validateUsername(int index, String prevText, boolean valid) {
		GuiTextField textBox = allowedPlayers[index];
		String username = textBox.getText();
		if (username.equals(prevText)) {
			if (valid) {
				validatedUsernames[index] = true;
				invalidUsernames[index] = false;
				textBox.setTextColor(65280);
				updateWhitelistedPlayer(index, username);
			} else {
				invalidUsernames[index] = true;
				validatedUsernames[index] = false;
				textBox.setTextColor(16711680);
				textBox.setText(StatCollector.translateToLocal("tes.gui.bannerEdit.invalidUsername"));
				updateWhitelistedPlayer(index, null);
			}
		}
	}

	public TESEntityBanner getTheBanner() {
		return theBanner;
	}
}