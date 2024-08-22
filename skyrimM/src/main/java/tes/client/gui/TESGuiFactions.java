package tes.client.gui;

import com.google.common.math.IntMath;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.client.TESClientProxy;
import tes.client.TESTextures;
import tes.client.TESTickHandlerClient;
import tes.common.TESConfig;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.*;
import tes.common.network.TESPacketClientMQEvent;
import tes.common.network.TESPacketHandler;
import tes.common.network.TESPacketPledgeSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Stream;

public class TESGuiFactions extends TESGuiMenuBase {
	public static final ResourceLocation FACTIONS_TEXTURE = new ResourceLocation("tes:textures/gui/factions.png");

	private static final ResourceLocation FACTIONS_TEXTURE_FULL = new ResourceLocation("tes:textures/gui/factions_full.png");

	private static final int PAGE_Y = 46;
	private static final int PAGE_WIDTH = 256;
	private static final int PAGE_HEIGHT = 128;
	private static final int PAGE_BORDER_TOP = 12;
	private static final int PAGE_MAP_X = 159;
	private static final int PAGE_MAP_Y = 22;
	private static final int PAGE_MAP_SIZE = 80;

	private static List<TESFaction> currentFactionList;

	private static Page currentPage = Page.FRONT;
	private static TESDimension currentDimension;
	private static TESDimension prevDimension;
	private static TESDimension.DimensionRegion currentRegion;
	private static TESDimension.DimensionRegion prevRegion;

	private final TESGuiMap mapDrawGui;
	private final TESGuiScrollPane scrollPaneAlliesEnemies;

	private final int scrollBarWidth;
	private final int scrollBarHeight;
	private final int scrollBarX;
	private final int scrollBarY;
	private final int scrollBarBorder;
	private final int scrollWidgetWidth;
	private final int scrollWidgetHeight;
	private final int scrollAlliesEnemiesX;

	private List<Object> currentAlliesEnemies;
	private Map<TESFaction, Float> playerAlignmentMap;

	private TESFaction currentFaction;

	private GuiButton buttonRegions;
	private GuiButton buttonPagePrev;
	private GuiButton buttonPageNext;
	private GuiButton buttonFactionMap;

	private TESGuiButtonPledge buttonPledge;
	private TESGuiButtonPledge buttonPledgeConfirm;
	private TESGuiButtonPledge buttonPledgeRevoke;

	private String otherPlayerName;

	private boolean isScrolling;
	private boolean wasMouseDown;
	private boolean isOtherPlayer;
	private boolean isPledging;
	private boolean isUnpledging;

	private float currentScroll;

	private int numDisplayedAlliesEnemies;
	private int currentFactionIndex;
	private int prevFactionIndex;

	public TESGuiFactions() {
		sizeX = PAGE_WIDTH;
		currentScroll = 0.0f;
		isScrolling = false;
		scrollBarWidth = 240;
		scrollBarHeight = 14;
		scrollBarX = sizeX / 2 - scrollBarWidth / 2;
		scrollBarY = 180;
		scrollBarBorder = 1;
		scrollWidgetWidth = 17;
		scrollWidgetHeight = 12;
		scrollPaneAlliesEnemies = new TESGuiScrollPane(7, 7).setColors(5521198, 8019267);
		scrollAlliesEnemiesX = 138;
		isOtherPlayer = false;
		isPledging = false;
		isUnpledging = false;
		mapDrawGui = new TESGuiMap();
	}

	private static boolean hasScrollBar() {
		return currentFactionList.size() > 1;
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == buttonRegions) {
				List<TESDimension.DimensionRegion> regionList = currentDimension.getDimensionRegions();
				if (!regionList.isEmpty()) {
					int i = regionList.indexOf(currentRegion);
					++i;
					i = IntMath.mod(i, regionList.size());
					currentRegion = regionList.get(i);
					updateCurrentDimensionAndFaction();
					setCurrentScrollFromFaction();
					scrollPaneAlliesEnemies.resetScroll();
					isPledging = false;
					isUnpledging = false;
				}
			} else if (button == goBack) {
				mc.displayGuiScreen(new TESGuiMenu());
			} else if (button == buttonPagePrev) {
				Page newPage = currentPage.prev();
				if (newPage != null) {
					currentPage = newPage;
					scrollPaneAlliesEnemies.resetScroll();
					isPledging = false;
					isUnpledging = false;
				}
			} else if (button == buttonPageNext) {
				Page newPage = currentPage.next();
				if (newPage != null) {
					currentPage = newPage;
					scrollPaneAlliesEnemies.resetScroll();
					isPledging = false;
					isUnpledging = false;
				}
			} else if (button == buttonFactionMap) {
				TESGuiMap factionGuiMap = new TESGuiMap();
				factionGuiMap.setControlZone(currentFaction);
				mc.displayGuiScreen(factionGuiMap);
			} else if (button == buttonPledge) {
				if (TESLevelData.getData(mc.thePlayer).isPledgedTo(currentFaction)) {
					isUnpledging = true;
				} else {
					isPledging = true;
				}
			} else if (button == buttonPledgeConfirm) {
				IMessage packet = new TESPacketPledgeSet(currentFaction);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
				isPledging = false;
			} else if (button == buttonPledgeRevoke) {
				IMessage packet = new TESPacketPledgeSet(null);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
				isUnpledging = false;
				mc.displayGuiScreen(null);
			} else {
				super.actionPerformed(button);
			}
		}
	}

	public void drawButtonHoveringText(List<String> list, int i, int j) {
		func_146283_a(list, i, j);
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		List<String> desc;
		int stringWidth;
		TESPlayerData clientPD = TESLevelData.getData(mc.thePlayer);
		boolean mouseOverWarCrimes = false;
		if (!isPledging && !isUnpledging) {
			buttonPagePrev.enabled = currentPage.prev() != null;
			buttonPageNext.enabled = currentPage.next() != null;
			buttonFactionMap.enabled = currentPage != Page.RANKS && currentFaction.isPlayableAlignmentFaction() && currentFaction.getFactionDimension() == TESDimension.GAME_OF_THRONES;
			buttonFactionMap.visible = buttonFactionMap.enabled;
			if (!TESFaction.controlZonesEnabled(mc.theWorld)) {
				buttonFactionMap.enabled = false;
				buttonFactionMap.visible = false;
			}
			if (!isOtherPlayer && currentPage == Page.FRONT) {
				if (clientPD.isPledgedTo(currentFaction)) {
					buttonPledge.setBroken(buttonPledge.func_146115_a());
					buttonPledge.enabled = true;
					buttonPledge.visible = true;
					buttonPledge.setDisplayLines(StatCollector.translateToLocal("tes.gui.factions.unpledge"));
				} else {
					buttonPledge.setBroken(false);
					buttonPledge.visible = clientPD.getPledgeFaction() == null && currentFaction.isPlayableAlignmentFaction() && clientPD.getAlignment(currentFaction) >= 0.0f;
					buttonPledge.enabled = buttonPledge.visible && clientPD.hasPledgeAlignment(currentFaction);
					String desc1 = StatCollector.translateToLocal("tes.gui.factions.pledge");
					String desc2 = StatCollector.translateToLocalFormatted("tes.gui.factions.pledgeReq", TESAlignmentValues.formatAlignForDisplay(currentFaction.getPledgeAlignment()));
					buttonPledge.setDisplayLines(desc1, desc2);
				}
			} else {
				buttonPledge.enabled = false;
				buttonPledge.visible = false;
			}
			buttonPledgeConfirm.enabled = false;
			buttonPledgeConfirm.visible = false;
			buttonPledgeRevoke.enabled = false;
			buttonPledgeRevoke.visible = false;
		} else {
			buttonPagePrev.enabled = false;
			buttonPageNext.enabled = false;
			buttonFactionMap.enabled = false;
			buttonFactionMap.visible = false;
			buttonPledge.enabled = false;
			buttonPledge.visible = false;
			if (isPledging) {
				buttonPledgeConfirm.visible = true;
				buttonPledgeConfirm.enabled = clientPD.canMakeNewPledge() && clientPD.canPledgeTo(currentFaction);
				buttonPledgeConfirm.setDisplayLines(StatCollector.translateToLocal("tes.gui.factions.pledge"));
				buttonPledgeRevoke.enabled = false;
				buttonPledgeRevoke.visible = false;
			} else {
				buttonPledgeConfirm.enabled = false;
				buttonPledgeConfirm.visible = false;
				buttonPledgeRevoke.enabled = true;
				buttonPledgeRevoke.visible = true;
				buttonPledgeRevoke.setDisplayLines(StatCollector.translateToLocal("tes.gui.factions.unpledge"));
			}
		}
		setupScrollBar(i, j);
		drawDefaultBackground();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		if (useFullPageTexture()) {
			mc.getTextureManager().bindTexture(FACTIONS_TEXTURE_FULL);
		} else {
			mc.getTextureManager().bindTexture(FACTIONS_TEXTURE);
		}
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		drawTexturedModalRect(guiLeft, guiTop + PAGE_Y, 0, 0, PAGE_WIDTH, PAGE_HEIGHT);
		if (currentRegion != null && currentDimension.getDimensionRegions().size() > 1) {
			buttonRegions.displayString = currentRegion.getRegionName();
			buttonRegions.enabled = true;
			buttonRegions.visible = true;
		} else {
			buttonRegions.displayString = "";
			buttonRegions.enabled = false;
			buttonRegions.visible = false;
		}
		if (currentFaction != null) {
			float alignment = isOtherPlayer && playerAlignmentMap != null ? playerAlignmentMap.get(currentFaction) : clientPD.getAlignment(currentFaction);
			int x = guiLeft + sizeX / 2;
			int y = guiTop;
			TESTickHandlerClient.renderAlignmentBar(alignment, currentFaction, x, y, true, false, true, true);
			String s = currentFaction.factionSubtitle();
			drawCenteredString(s, x, y + fontRendererObj.FONT_HEIGHT + 22, 16777215);
			if (!useFullPageTexture()) {
				if (currentFaction.getFactionMapInfo() != null) {
					TESMapRegion mapInfo = currentFaction.getFactionMapInfo();
					int mapX = mapInfo.getMapX();
					int mapY = mapInfo.getMapY();
					int mapR = mapInfo.getRadius();
					int xMin = guiLeft + PAGE_MAP_X;
					int xMax = xMin + PAGE_MAP_SIZE;
					int yMin = guiTop + PAGE_Y + PAGE_MAP_Y;
					int yMax = yMin + PAGE_MAP_SIZE;
					int mapBorder = 1;
					drawRect(xMin - mapBorder, yMin - mapBorder, xMax + mapBorder, yMax + mapBorder, -16777216);
					float zoom = (float) PAGE_MAP_SIZE / (mapR * 2);
					float zoomExp = (float) Math.log(zoom) / (float) Math.log(2.0);
					mapDrawGui.setFakeMapProperties(mapX, mapY, zoom, zoomExp, zoom);
					int[] statics = TESGuiMap.setFakeStaticProperties(PAGE_MAP_SIZE, PAGE_MAP_SIZE, xMin, xMax, yMin, yMax);
					mapDrawGui.setEnableZoomOutWPFading(false);
					boolean sepia = TESConfig.enableSepiaMap;
					mapDrawGui.renderMapAndOverlay(sepia, 1.0f, true);
					TESGuiMap.setFakeStaticProperties(statics[0], statics[1], statics[2], statics[3], statics[4], statics[5]);
				}
				int wcX = guiLeft + PAGE_MAP_X + 3;
				int wcY = guiTop + PAGE_Y + PAGE_MAP_Y + PAGE_MAP_SIZE + 5;
				int wcWidth = 8;
				mc.getTextureManager().bindTexture(FACTIONS_TEXTURE);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				if (currentFaction.isApprovesWarCrimes()) {
					drawTexturedModalRect(wcX, wcY, 33, 142, wcWidth, wcWidth);
				} else {
					drawTexturedModalRect(wcX, wcY, 41, 142, wcWidth, wcWidth);
				}
				if (i >= wcX && i < wcX + wcWidth && j >= wcY && j < wcY + wcWidth) {
					mouseOverWarCrimes = true;
				}
			}
			int pageBorderLeft = 16;
			x = guiLeft + pageBorderLeft;
			y = guiTop + PAGE_Y + PAGE_BORDER_TOP;
			if (!isPledging && !isUnpledging) {
				int index;
				switch (currentPage) {
					case ALLIES:
					case ENEMIES:
						int avgBgColor = TESTextures.computeAverageFactionPageColor(FACTIONS_TEXTURE, 20, 20, 120, 80);
						int[] minMax = scrollPaneAlliesEnemies.getMinMaxIndices(currentAlliesEnemies, numDisplayedAlliesEnemies);
						for (index = minMax[0]; index <= minMax[1]; ++index) {
							Object listObj = currentAlliesEnemies.get(index);
							if (listObj instanceof TESFactionRelations.Relation) {
								TESFactionRelations.Relation rel = (TESFactionRelations.Relation) listObj;
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.relationHeader", rel.getDisplayName());
								fontRendererObj.drawString(s, x, y, 8019267);
							} else if (listObj instanceof TESFaction) {
								TESFaction fac = (TESFaction) listObj;
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.list", fac.factionName());
								fontRendererObj.drawString(s, x, y, TESTextures.findContrastingColor(fac.getFactionColor(), avgBgColor));
							}
							y += fontRendererObj.FONT_HEIGHT;
						}
						break;
					case FRONT:
						if (isOtherPlayer) {
							s = StatCollector.translateToLocalFormatted("tes.gui.factions.pageOther", otherPlayerName);
							fontRendererObj.drawString(s, x, y, 8019267);
							y += fontRendererObj.FONT_HEIGHT * 2;
						}
						String alignmentInfo = StatCollector.translateToLocal("tes.gui.factions.alignment");
						fontRendererObj.drawString(alignmentInfo, x, y, 8019267);
						String alignmentString = TESAlignmentValues.formatAlignForDisplay(alignment);
						TESTickHandlerClient.drawAlignmentText(fontRendererObj, x + fontRendererObj.getStringWidth(alignmentInfo) + 5, y, alignmentString, 1.0f);
						x = guiLeft + pageBorderLeft;
						TESFactionRank curRank = currentFaction.getRank(alignment);
						String rankName = curRank.getFullNameWithGender(clientPD);
						fontRendererObj.drawString(rankName, x, y += fontRendererObj.FONT_HEIGHT, 8019267);
						y += fontRendererObj.FONT_HEIGHT * 2;
						if (!isOtherPlayer) {
							TESFactionData factionData = clientPD.getFactionData(currentFaction);
							if (alignment >= 0.0f) {
								float conq;
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.data.enemiesKilled", factionData.getEnemiesKilled());
								fontRendererObj.drawString(s, x, y, 8019267);
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.data.trades", factionData.getTradeCount());
								fontRendererObj.drawString(s, x, y += fontRendererObj.FONT_HEIGHT, 8019267);
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.data.hires", factionData.getHireCount());
								fontRendererObj.drawString(s, x, y += fontRendererObj.FONT_HEIGHT, 8019267);
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.data.miniquests", factionData.getMiniQuestsCompleted());
								fontRendererObj.drawString(s, x, y += fontRendererObj.FONT_HEIGHT, 8019267);
								y += fontRendererObj.FONT_HEIGHT;
								if (clientPD.isPledgedTo(currentFaction) && (conq = factionData.getConquestEarned()) != 0.0f) {
									int conqInt = Math.round(conq);
									s = StatCollector.translateToLocalFormatted("tes.gui.factions.data.conquest", conqInt);
									fontRendererObj.drawString(s, x, y, 8019267);
									y += fontRendererObj.FONT_HEIGHT;
								}
							}
							if (alignment <= 0.0f) {
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.data.npcsKilled", factionData.getNPCsKilled());
								fontRendererObj.drawString(s, x, y, 8019267);
							}
							if (buttonPledge.visible && clientPD.isPledgedTo(currentFaction)) {
								s = StatCollector.translateToLocal("tes.gui.factions.pledged");
								int px = buttonPledge.xPosition + buttonPledge.width + 8;
								int py = buttonPledge.yPosition + buttonPledge.height / 2 - fontRendererObj.FONT_HEIGHT / 2;
								fontRendererObj.drawString(s, px, py, 16711680);
							}
						}
						break;
					case RANKS:
						TESFactionRank curRank1 = currentFaction.getRank(clientPD);
						int[] minMax1 = scrollPaneAlliesEnemies.getMinMaxIndices(currentAlliesEnemies, numDisplayedAlliesEnemies);
						for (index = minMax1[0]; index <= minMax1[1]; ++index) {
							Object listObj = currentAlliesEnemies.get(index);
							if (listObj instanceof String) {
								s = (String) listObj;
								fontRendererObj.drawString(s, x, y, 8019267);
							} else if (listObj instanceof TESFactionRank) {
								TESFactionRank rank = (TESFactionRank) listObj;
								String rankName1 = rank.getShortNameWithGender(clientPD);
								String rankAlign = TESAlignmentValues.formatAlignForDisplay(rank.getAlignment());
								if (rank == TESFactionRank.RANK_ENEMY) {
									rankAlign = "-";
								}
								boolean hiddenRankName = !clientPD.isPledgedTo(currentFaction) && rank.getAlignment() > currentFaction.getPledgeAlignment() && rank.getAlignment() > currentFaction.getRankAbove(curRank1).getAlignment();
								if (hiddenRankName) {
									rankName1 = StatCollector.translateToLocal("tes.gui.factions.rank?");
								}
								s = StatCollector.translateToLocalFormatted("tes.gui.factions.listRank", rankName1, rankAlign);
								if (rank == curRank1) {
									TESTickHandlerClient.drawAlignmentText(fontRendererObj, x, y, s, 1.0f);
								} else {
									fontRendererObj.drawString(s, x, y, 8019267);
								}
							}
							y += fontRendererObj.FONT_HEIGHT;
						}
						break;
				}
				if (scrollPaneAlliesEnemies.isHasScrollBar()) {
					scrollPaneAlliesEnemies.drawScrollBar();
				}
			} else {
				int stringWidth2 = PAGE_WIDTH - pageBorderLeft * 2;
				Collection<String> displayLines = new ArrayList<>();
				if (isPledging) {
					if (clientPD.canMakeNewPledge()) {
						if (clientPD.canPledgeTo(currentFaction)) {
							String desc2 = StatCollector.translateToLocalFormatted("tes.gui.factions.pledgeDesc1", currentFaction.factionName());
							displayLines.addAll(fontRendererObj.listFormattedStringToWidth(desc2, stringWidth2));
							displayLines.add("");
							desc2 = StatCollector.translateToLocalFormatted("tes.gui.factions.pledgeDesc2");
							displayLines.addAll(fontRendererObj.listFormattedStringToWidth(desc2, stringWidth2));
						}
					} else {
						TESFaction brokenPledge = clientPD.getBrokenPledgeFaction();
						String brokenPledgeName = brokenPledge == null ? StatCollector.translateToLocal("tes.gui.factions.pledgeUnknown") : brokenPledge.factionName();
						String desc3 = StatCollector.translateToLocalFormatted("tes.gui.factions.pledgeBreakCooldown", currentFaction.factionName(), brokenPledgeName);
						displayLines.addAll(fontRendererObj.listFormattedStringToWidth(desc3, stringWidth2));
						displayLines.add("");
						GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
						mc.getTextureManager().bindTexture(FACTIONS_TEXTURE);
						drawTexturedModalRect(guiLeft + PAGE_WIDTH / 2 - 97, guiTop + PAGE_Y + 56, 0, 240, 194, 16);
						float cdFrac = (float) clientPD.getPledgeBreakCooldown() / clientPD.getPledgeBreakCooldownStart();
						drawTexturedModalRect(guiLeft + PAGE_WIDTH / 2 - 75, guiTop + PAGE_Y + 60, 22, 232, MathHelper.ceiling_float_int(cdFrac * 150.0f), 8);
					}
				} else {
					String desc5 = StatCollector.translateToLocalFormatted("tes.gui.factions.unpledgeDesc1", currentFaction.factionName());
					displayLines.addAll(fontRendererObj.listFormattedStringToWidth(desc5, stringWidth2));
					displayLines.add("");
					desc5 = StatCollector.translateToLocalFormatted("tes.gui.factions.unpledgeDesc2");
					displayLines.addAll(fontRendererObj.listFormattedStringToWidth(desc5, stringWidth2));
				}
				for (String line : displayLines) {
					fontRendererObj.drawString(line, x, y, 8019267);
					y += mc.fontRenderer.FONT_HEIGHT;
				}
			}
		}
		if (hasScrollBar()) {
			mc.getTextureManager().bindTexture(FACTIONS_TEXTURE);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			drawTexturedModalRect(guiLeft + scrollBarX, guiTop + scrollBarY, 0, 128, scrollBarWidth, scrollBarHeight);
			int factions = currentFactionList.size();
			for (int index = 0; index < factions; ++index) {
				TESFaction faction = currentFactionList.get(index);
				float[] factionColors = faction.getFactionRGB();
				float shade = 0.6f;
				GL11.glColor4f(factionColors[0] * shade, factionColors[1] * shade, factionColors[2] * shade, 1.0f);
				float xMin = (float) index / factions;
				float xMax = (float) (index + 1) / factions;
				xMin = guiLeft + scrollBarX + scrollBarBorder + xMin * (scrollBarWidth - scrollBarBorder * 2);
				xMax = guiLeft + scrollBarX + scrollBarBorder + xMax * (scrollBarWidth - scrollBarBorder * 2);
				float yMin = guiTop + scrollBarY + scrollBarBorder;
				float yMax = guiTop + scrollBarY + scrollBarHeight - scrollBarBorder;
				float minU = scrollBarBorder / 256.0f;
				float maxU = (scrollBarWidth - scrollBarBorder) / 256.0f;
				float minV = (128 + scrollBarBorder) / 256.0f;
				float maxV = (128 + scrollBarHeight - scrollBarBorder) / 256.0f;
				Tessellator tessellator = Tessellator.instance;
				tessellator.startDrawingQuads();
				tessellator.addVertexWithUV(xMin, yMax, zLevel, minU, maxV);
				tessellator.addVertexWithUV(xMax, yMax, zLevel, maxU, maxV);
				tessellator.addVertexWithUV(xMax, yMin, zLevel, maxU, minV);
				tessellator.addVertexWithUV(xMin, yMin, zLevel, minU, minV);
				tessellator.draw();
			}
			mc.getTextureManager().bindTexture(FACTIONS_TEXTURE);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			int scroll = (int) (currentScroll * (scrollBarWidth - scrollBarBorder * 2 - scrollWidgetWidth));
			drawTexturedModalRect(guiLeft + scrollBarX + scrollBarBorder + scroll, guiTop + scrollBarY + scrollBarBorder, 0, 142, scrollWidgetWidth, scrollWidgetHeight);
		}
		super.drawScreen(i, j, f);
		if (buttonFactionMap.enabled && buttonFactionMap.func_146115_a()) {
			float z = zLevel;
			String s = StatCollector.translateToLocal("tes.gui.factions.viewMap");
			stringWidth = 200;
			desc = fontRendererObj.listFormattedStringToWidth(s, stringWidth);
			func_146283_a(desc, i, j);
			GL11.glDisable(2896);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			zLevel = z;
		}
		if (mouseOverWarCrimes) {
			float z = zLevel;
			String warCrimes = currentFaction.isApprovesWarCrimes() ? "tes.gui.factions.warCrimesYes" : "tes.gui.factions.warCrimesNo";
			warCrimes = StatCollector.translateToLocal(warCrimes);
			stringWidth = 200;
			desc = fontRendererObj.listFormattedStringToWidth(warCrimes, stringWidth);
			func_146283_a(desc, i, j);
			GL11.glDisable(2896);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			zLevel = z;
		}
	}

	@Override
	public void handleMouseInput() {
		super.handleMouseInput();
		int k = Mouse.getEventDWheel();
		if (k != 0) {
			k = Integer.signum(k);
			if (scrollPaneAlliesEnemies.isHasScrollBar() && scrollPaneAlliesEnemies.isMouseOver()) {
				int l = currentAlliesEnemies.size() - numDisplayedAlliesEnemies;
				scrollPaneAlliesEnemies.mouseWheelScroll(k, l);
			} else {
				if (k < 0) {
					currentFactionIndex = Math.min(currentFactionIndex + 1, Math.max(0, currentFactionList.size() - 1));
				}
				if (k > 0) {
					currentFactionIndex = Math.max(currentFactionIndex - 1, 0);
				}
				setCurrentScrollFromFaction();
				scrollPaneAlliesEnemies.resetScroll();
				isPledging = false;
				isUnpledging = false;
			}
		}
	}

	@Override
	public void initGui() {
		super.initGui();
		guiLeft = (width - sizeX) / 2;
		guiTop = (height - sizeY) / 2 + 20;
		buttonRegions = new TESGuiButton(0, guiLeft + sizeX / 2 + 5, guiTop + 200, 120, 20, "");
		buttonList.add(buttonRegions);
		goBack = new TESGuiButton(0, guiLeft + sizeX / 2 - 125, guiTop + 200, 120, 20, StatCollector.translateToLocal("tes.gui.menuButton"));
		buttonList.add(goBack);
		buttonPagePrev = new TESGuiButtonFactionsPage(1, guiLeft + 8, guiTop + PAGE_Y + 104, false);
		buttonList.add(buttonPagePrev);
		buttonPageNext = new TESGuiButtonFactionsPage(2, guiLeft + 232, guiTop + PAGE_Y + 104, true);
		buttonList.add(buttonPageNext);
		buttonFactionMap = new TESGuiButtonFactionsMap(3, guiLeft + PAGE_MAP_X + PAGE_MAP_SIZE - 3 - 8, guiTop + PAGE_Y + PAGE_MAP_Y + 3);
		buttonList.add(buttonFactionMap);
		buttonPledge = new TESGuiButtonPledge(this, 4, guiLeft + 14, guiTop + PAGE_Y + PAGE_HEIGHT - 42, "");
		buttonList.add(buttonPledge);
		buttonPledgeConfirm = new TESGuiButtonPledge(this, 5, guiLeft + PAGE_WIDTH / 2 - 16, guiTop + PAGE_Y + PAGE_HEIGHT - 50, "");
		buttonList.add(buttonPledgeConfirm);
		buttonPledgeRevoke = new TESGuiButtonPledge(this, 6, guiLeft + PAGE_WIDTH / 2 - 16, guiTop + PAGE_Y + PAGE_HEIGHT - 50, "");
		buttonList.add(buttonPledgeRevoke);
		buttonPledgeRevoke.setBroken(true);
		prevDimension = currentDimension = TESDimension.GAME_OF_THRONES;
		currentFaction = TESLevelData.getData(mc.thePlayer).getViewingFaction();
		prevRegion = currentRegion = currentFaction.getFactionRegion();
		currentFactionList = currentRegion.getFactionList();
		prevFactionIndex = currentFactionIndex = currentFactionList.indexOf(currentFaction);
		setCurrentScrollFromFaction();
		if (mc.currentScreen == this) {
			IMessage packet = new TESPacketClientMQEvent(TESPacketClientMQEvent.ClientMQEvent.FACTIONS);
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
		}
	}

	@Override
	public void keyTyped(char c, int i) {
		if (i == 1 || i == mc.gameSettings.keyBindInventory.getKeyCode()) {
			if (isPledging) {
				isPledging = false;
				return;
			}
			if (isUnpledging) {
				isUnpledging = false;
				return;
			}
			if (isOtherPlayer) {
				mc.thePlayer.closeScreen();
				return;
			}
		}
		super.keyTyped(c, i);
	}

	private void setCurrentScrollFromFaction() {
		currentScroll = (float) currentFactionIndex / (currentFactionList.size() - 1);
	}

	public void setOtherPlayer(String name, Map<TESFaction, Float> alignments) {
		isOtherPlayer = true;
		otherPlayerName = name;
		playerAlignmentMap = alignments;
	}

	private void setupScrollBar(int i, int j) {
		boolean isMouseDown = Mouse.isButtonDown(0);
		int i1 = guiLeft + scrollBarX;
		int j1 = guiTop + scrollBarY;
		int i2 = i1 + scrollBarWidth;
		int j2 = j1 + scrollBarHeight;
		if (!wasMouseDown && isMouseDown && i >= i1 && j >= j1 && i < i2 && j < j2) {
			isScrolling = true;
		}
		if (!isMouseDown) {
			isScrolling = false;
		}
		wasMouseDown = isMouseDown;
		if (isScrolling) {
			currentScroll = (i - i1 - scrollWidgetWidth / 2.0f) / ((float) (i2 - i1) - scrollWidgetWidth);
			currentScroll = MathHelper.clamp_float(currentScroll, 0.0f, 1.0f);
			currentFactionIndex = Math.round(currentScroll * (currentFactionList.size() - 1));
			scrollPaneAlliesEnemies.resetScroll();
		}
		//noinspection StreamToLoop
		if (Stream.of(Page.ALLIES, Page.ENEMIES, Page.RANKS).anyMatch(page -> currentPage == page)) {
			switch (currentPage) {
				case ALLIES:
					List<TESFaction> friends;
					currentAlliesEnemies = new ArrayList<>();
					List<TESFaction> allies = currentFaction.getOthersOfRelation(TESFactionRelations.Relation.ALLY);
					if (!allies.isEmpty()) {
						currentAlliesEnemies.add(TESFactionRelations.Relation.ALLY);
						currentAlliesEnemies.addAll(allies);
					}
					if (!(friends = currentFaction.getOthersOfRelation(TESFactionRelations.Relation.FRIEND)).isEmpty()) {
						if (!currentAlliesEnemies.isEmpty()) {
							currentAlliesEnemies.add(null);
						}
						currentAlliesEnemies.add(TESFactionRelations.Relation.FRIEND);
						currentAlliesEnemies.addAll(friends);
					}
					break;
				case ENEMIES:
					List<TESFaction> enemies;
					currentAlliesEnemies = new ArrayList<>();
					List<TESFaction> mortals = currentFaction.getOthersOfRelation(TESFactionRelations.Relation.MORTAL_ENEMY);
					if (!mortals.isEmpty()) {
						currentAlliesEnemies.add(TESFactionRelations.Relation.MORTAL_ENEMY);
						currentAlliesEnemies.addAll(mortals);
					}
					if (!(enemies = currentFaction.getOthersOfRelation(TESFactionRelations.Relation.ENEMY)).isEmpty()) {
						if (!currentAlliesEnemies.isEmpty()) {
							currentAlliesEnemies.add(null);
						}
						currentAlliesEnemies.add(TESFactionRelations.Relation.ENEMY);
						currentAlliesEnemies.addAll(enemies);
					}
					break;
				case RANKS:
					currentAlliesEnemies = new ArrayList<>();
					currentAlliesEnemies.add(StatCollector.translateToLocal("tes.gui.factions.rankHeader"));
					if (TESLevelData.getData(mc.thePlayer).getAlignment(currentFaction) <= 0.0f) {
						currentAlliesEnemies.add(TESFactionRank.RANK_ENEMY);
					}
					TESFactionRank rank = TESFactionRank.RANK_NEUTRAL;
					do {
						currentAlliesEnemies.add(rank);
						TESFactionRank nextRank = currentFaction.getRankAbove(rank);
						if (nextRank == null || nextRank.isDummyRank() || currentAlliesEnemies.contains(nextRank)) {
							break;
						}
						rank = nextRank;
					} while (true);
					break;
			}
			scrollPaneAlliesEnemies.setHasScrollBar(false);
			numDisplayedAlliesEnemies = currentAlliesEnemies.size();
			if (numDisplayedAlliesEnemies > 10) {
				numDisplayedAlliesEnemies = 10;
				scrollPaneAlliesEnemies.setHasScrollBar(true);
			}
			scrollPaneAlliesEnemies.setPaneX0(guiLeft);
			scrollPaneAlliesEnemies.setScrollBarX0(guiLeft + scrollAlliesEnemiesX);
			if (currentPage == Page.RANKS) {
				scrollPaneAlliesEnemies.setScrollBarX0(scrollPaneAlliesEnemies.getScrollBarX0() + 50);
			}
			scrollPaneAlliesEnemies.setPaneY0(guiTop + PAGE_Y + PAGE_BORDER_TOP);
			scrollPaneAlliesEnemies.setPaneY1(scrollPaneAlliesEnemies.getPaneY0() + fontRendererObj.FONT_HEIGHT * numDisplayedAlliesEnemies);
		} else {
			scrollPaneAlliesEnemies.setHasScrollBar(false);
		}
		scrollPaneAlliesEnemies.mouseDragScroll(i, j);
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int i, int j) {
		super.setWorldAndResolution(mc, i, j);
		mapDrawGui.setWorldAndResolution(mc, i, j);
	}

	private void updateCurrentDimensionAndFaction() {
		TESPlayerData pd = TESLevelData.getData(mc.thePlayer);
		Map<TESDimension.DimensionRegion, TESFaction> lastViewedRegions = new EnumMap<>(TESDimension.DimensionRegion.class);
		if (currentFactionIndex != prevFactionIndex) {
			currentFaction = currentFactionList.get(currentFactionIndex);
		}
		prevFactionIndex = currentFactionIndex;
		currentDimension = TESDimension.GAME_OF_THRONES;
		if (currentDimension != prevDimension) {
			currentRegion = currentDimension.getDimensionRegions().get(0);
		}
		if (currentRegion != prevRegion) {
			pd.setRegionLastViewedFaction(prevRegion, currentFaction);
			lastViewedRegions.put(prevRegion, currentFaction);
			currentFactionList = currentRegion.getFactionList();
			currentFaction = pd.getRegionLastViewedFaction(currentRegion);
			prevFactionIndex = currentFactionIndex = currentFactionList.indexOf(currentFaction);
		}
		prevDimension = currentDimension;
		prevRegion = currentRegion;
		TESFaction prevFaction = pd.getViewingFaction();
		boolean changes = currentFaction != prevFaction;
		if (changes) {
			pd.setViewingFaction(currentFaction);
			TESClientProxy.sendClientInfoPacket(currentFaction, lastViewedRegions);
			isPledging = false;
			isUnpledging = false;
		}
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		updateCurrentDimensionAndFaction();
		TESPlayerData playerData = TESLevelData.getData(mc.thePlayer);
		if (isPledging && !playerData.hasPledgeAlignment(currentFaction)) {
			isPledging = false;
		}
		if (isUnpledging && !playerData.isPledgedTo(currentFaction)) {
			isUnpledging = false;
		}
	}

	private boolean useFullPageTexture() {
		return isPledging || isUnpledging || currentPage == Page.RANKS;
	}

	private enum Page {
		FRONT, RANKS, ALLIES, ENEMIES;

		private Page next() {
			int i = ordinal();
			if (i == values().length - 1) {
				return null;
			}
			i++;
			return values()[i];
		}

		private Page prev() {
			int i = ordinal();
			if (i == 0) {
				return null;
			}
			i--;
			return values()[i];
		}
	}
}