package tes.client;

import com.google.common.collect.Lists;
import cpw.mods.fml.client.GuiModList;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tes.TES;
import tes.TESInfo;
import tes.client.gui.TESGuiAchievementHoverEvent;
import tes.client.gui.TESGuiButtonLock;
import tes.client.gui.TESGuiDownloadTerrain;
import tes.client.gui.TESMainMenu;
import tes.common.TESChatEvents;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.inventory.TESContainerCoinExchange;
import tes.common.item.other.TESItemCoin;
import tes.common.util.TESModChecker;
import tes.common.world.TESWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.event.HoverEvent;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class TESGuiEventHandler {
	public static final TESGuiEventHandler INSTANCE = new TESGuiEventHandler();

	private static final Collection<Class<? extends IInventory>> COIN_COUNT_EXCLUDED_INV_TYPES = new HashSet<Class<? extends IInventory>>();
	private static final Collection<String> COIN_COUNT_EXCLUDED_INV_TYPES_CLS_NAMES = new HashSet<String>();

	private static final RenderItem ITEM_RENDERER = new RenderItem();

	static {
		COIN_COUNT_EXCLUDED_INV_TYPES.add(TESContainerCoinExchange.InventoryCoinExchangeSlot.class);
		COIN_COUNT_EXCLUDED_INV_TYPES.add(InventoryCraftResult.class);
		COIN_COUNT_EXCLUDED_INV_TYPES_CLS_NAMES.add("thaumcraft.common.entities.InventoryMob");
	}

	private int descScrollIndex = -1;

	private TESGuiEventHandler() {
	}

	private static GuiButton getDifficultyButton(Iterable<GuiButton> buttons) {
		for (GuiButton obj : buttons) {
			GuiOptionButton button;
			if (obj instanceof GuiOptionButton && (button = (GuiOptionButton) obj).returnEnumOptions() == GameSettings.Options.DIFFICULTY) {
				return button;
			}
		}
		return null;
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void onGuiOpen(GuiOpenEvent event) {
		GuiScreen gui = event.gui;
		if (TESConfig.customMainMenu && gui != null && gui.getClass() == GuiMainMenu.class) {
			event.gui = gui = new TESMainMenu();
		}
		if (gui != null && gui.getClass() == GuiDownloadTerrain.class) {
			Minecraft mc = Minecraft.getMinecraft();
			WorldProvider provider = mc.theWorld.provider;
			if (provider instanceof TESWorldProvider) {
				event.gui = new TESGuiDownloadTerrain(mc.thePlayer.sendQueue);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void postActionPerformed(GuiScreenEvent.ActionPerformedEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = event.gui;
		List<GuiButton> buttons = event.buttonList;
		GuiButton button = event.button;
		if (gui instanceof GuiOptions && button instanceof TESGuiButtonLock && button.enabled && mc.isSingleplayer()) {
			TESLevelData.setSavedDifficulty(mc.gameSettings.difficulty);
			TESLevelData.setDifficultyLocked(true);
			button.enabled = false;
			GuiButton buttonDifficulty = getDifficultyButton(buttons);
			if (buttonDifficulty != null) {
				buttonDifficulty.enabled = false;
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings({"StringConcatenationMissingWhitespace", "MethodMayBeStatic"})
	public void postDrawScreen(GuiScreenEvent.DrawScreenEvent.Post event) {
		HoverEvent hoverevent;
		IChatComponent component;
		Minecraft mc = Minecraft.getMinecraft();
		EntityClientPlayerMP entityplayer = mc.thePlayer;
		GuiScreen gui = event.gui;
		int mouseX = event.mouseX;
		int mouseY = event.mouseY;
		if (gui instanceof GuiChat && (component = mc.ingameGUI.getChatGUI().func_146236_a(Mouse.getX(), Mouse.getY())) != null && component.getChatStyle().getChatHoverEvent() != null && (hoverevent = component.getChatStyle().getChatHoverEvent()).getAction() == TESChatEvents.showGotAchievement) {
			TESGuiAchievementHoverEvent proxyGui = new TESGuiAchievementHoverEvent();
			proxyGui.setWorldAndResolution(mc, gui.width, gui.height);
			try {
				String unformattedText = hoverevent.getValue().getUnformattedText();
				int splitIndex = unformattedText.indexOf('$');
				String categoryName = unformattedText.substring(0, splitIndex);
				TESAchievement.Category category = TESAchievement.categoryForName(categoryName);
				int achievementID = Integer.parseInt(unformattedText.substring(splitIndex + 1));
				TESAchievement achievement = TESAchievement.achievementForCategoryAndID(category, achievementID);
				IChatComponent name = new ChatComponentTranslation("tes.gui.achievements.hover.name", achievement.getAchievementChatComponent(entityplayer));
				IChatComponent subtitle = new ChatComponentTranslation("tes.gui.achievements.hover.subtitle", achievement.getDimension().getTranslatedDimensionName(), category.getDisplayName());
				subtitle.getChatStyle().setItalic(true);
				String desc = achievement.getDescription();
				List<String> list = Lists.newArrayList(name.getFormattedText(), subtitle.getFormattedText());
				list.addAll(mc.fontRenderer.listFormattedStringToWidth(desc, 150));
				proxyGui.func_146283_a(list, mouseX, mouseY);
			} catch (Exception e) {
				proxyGui.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid TESAchievement!", mouseX, mouseY);
			}
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void postInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
		GuiButton buttonDifficulty;
		GuiScreen gui = event.gui;
		List<GuiButton> buttons = event.buttonList;
		if (gui instanceof GuiOptions && (buttonDifficulty = getDifficultyButton(buttons)) != null) {
			TESGuiButtonLock lock = new TESGuiButtonLock(1000000, buttonDifficulty.xPosition + buttonDifficulty.width + 4, buttonDifficulty.yPosition);
			lock.enabled = !TESLevelData.isDifficultyLocked();
			buttons.add(lock);
			buttonDifficulty.enabled = !TESLevelData.isDifficultyLocked();
		}
	}

	@SubscribeEvent
	public void preDrawScreen(GuiScreenEvent.DrawScreenEvent.Pre event) {
		Minecraft mc = Minecraft.getMinecraft();
		GuiScreen gui = event.gui;
		if (gui instanceof GuiModList) {
			ModContainer mod = TES.getModContainer();
			ModMetadata meta = mod.getMetadata();
			if (descScrollIndex == -1) {
				meta.description = TESInfo.concatenateDescription(0);
				descScrollIndex = 0;
			}
			while (Mouse.next()) {
				int dwheel = Mouse.getEventDWheel();
				if (dwheel != 0) {
					int scroll = Integer.signum(dwheel);
					descScrollIndex -= scroll;
					descScrollIndex = MathHelper.clamp_int(descScrollIndex, 0, 0);
					meta.description = TESInfo.concatenateDescription(descScrollIndex);
				}
			}
		}
		if (gui instanceof GuiContainer && TESConfig.displayCoinCounts) {
			mc.theWorld.theProfiler.startSection("invCoinCount");
			GuiContainer guiContainer = (GuiContainer) gui;
			Container container = guiContainer.inventorySlots;
			boolean excludeGui = false;
			if (guiContainer instanceof GuiContainerCreative) {
				int creativeTabIndex = TESReflectionClient.getCreativeTabIndex((GuiContainerCreative) guiContainer);
				if (creativeTabIndex != CreativeTabs.tabInventory.getTabIndex()) {
					excludeGui = true;
				}
			}
			if (!excludeGui) {
				int guiLeft = -1;
				int guiTop = -1;
				int guiXSize = -1;
				Collection<IInventory> differentInvs = new ArrayList<>();
				Map<IInventory, Integer> invHighestY = new HashMap<>();
				for (int i = 0; i < container.inventorySlots.size(); i++) {
					Slot slot = container.getSlot(i);
					IInventory inv = slot.inventory;
					if (inv != null) {
						Class<? extends IInventory> invClass = inv.getClass();
						boolean excludeInv = COIN_COUNT_EXCLUDED_INV_TYPES.contains(invClass) || COIN_COUNT_EXCLUDED_INV_TYPES_CLS_NAMES.contains(invClass.getName());
						if (!excludeInv) {
							if (!differentInvs.contains(inv)) {
								differentInvs.add(inv);
							}
							int slotY = slot.yDisplayPosition;
							if (invHighestY.containsKey(inv)) {
								int highestY = invHighestY.get(inv);
								if (slotY < highestY) {
									invHighestY.put(inv, slotY);
								}
							} else {
								invHighestY.put(inv, slotY);
							}
						}
					}
				}
				for (IInventory inv : differentInvs) {
					int coins = TESItemCoin.getContainerValue(inv, true);
					if (coins > 0) {
						String sCoins = String.valueOf(coins);
						int sCoinsW = mc.fontRenderer.getStringWidth(sCoins);
						int border = 2;
						int rectWidth = 18 + sCoinsW + 1;
						if (guiLeft == -1) {
							guiTop = TESReflectionClient.getGuiTop(guiContainer);
							guiXSize = TESReflectionClient.getGuiXSize(guiContainer);
							guiLeft = gui.width / 2 - guiXSize / 2;
							if (guiContainer instanceof InventoryEffectRenderer && TESReflectionClient.hasGuiPotionEffects((InventoryEffectRenderer) gui) && !TESModChecker.hasNEI()) {
								guiLeft += 60;
							}
						}
						int guiGap = 8;
						int x = guiLeft + guiXSize + guiGap;
						int y = invHighestY.get(inv) + guiTop;
						int rectX0 = x - border;
						int rectX1 = x + rectWidth + border;
						int rectY0 = y - border;
						int rectY1 = y + 16 + border;
						float a0 = 1.0F;
						float a1 = 0.1F;
						GL11.glDisable(3553);
						GL11.glDisable(3008);
						GL11.glShadeModel(7425);
						GL11.glPushMatrix();
						GL11.glTranslatef(0.0F, 0.0F, -500.0F);
						Tessellator tessellator = Tessellator.instance;
						tessellator.startDrawingQuads();
						tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, a1);
						tessellator.addVertex(rectX1, rectY0, 0.0D);
						tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, a0);
						tessellator.addVertex(rectX0, rectY0, 0.0D);
						tessellator.addVertex(rectX0, rectY1, 0.0D);
						tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, a1);
						tessellator.addVertex(rectX1, rectY1, 0.0D);
						tessellator.draw();
						GL11.glPopMatrix();
						GL11.glShadeModel(7424);
						GL11.glEnable(3008);
						GL11.glEnable(3553);
						GL11.glPushMatrix();
						GL11.glTranslatef(0.0F, 0.0F, 500.0F);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						ITEM_RENDERER.renderItemIntoGUI(mc.fontRenderer, mc.getTextureManager(), new ItemStack(TESItems.coin), x, y);
						GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
						GL11.glDisable(2896);
						mc.fontRenderer.drawString(sCoins, x + 16 + 2, y + (16 - mc.fontRenderer.FONT_HEIGHT + 2) / 2, 16777215);
						GL11.glPopMatrix();
						GL11.glDisable(2896);
						GL11.glEnable(3008);
						GL11.glEnable(3042);
						GL11.glDisable(2884);
					}
				}
				mc.theWorld.theProfiler.endSection();
			}
		}
	}
}