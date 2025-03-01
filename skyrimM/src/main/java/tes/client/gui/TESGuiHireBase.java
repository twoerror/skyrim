package tes.client.gui;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.client.TESClientProxy;
import tes.common.TESLevelData;
import tes.common.TESSquadrons;
import tes.common.database.TESItems;
import tes.common.database.TESUnitTradeEntries;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import tes.common.inventory.TESContainerUnitTrade;
import tes.common.inventory.TESSlotAlignmentReward;
import tes.common.network.TESPacketBuyUnit;
import tes.common.network.TESPacketHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

import java.util.List;

public abstract class TESGuiHireBase extends GuiContainer {
	private static ResourceLocation guiTexture;

	private final TESHireableBase theUnitTrader;
	private final TESFaction traderFaction;

	private TESGuiUnitTradeButton buttonHire;
	private TESGuiUnitTradeButton buttonLeftUnit;
	private TESGuiUnitTradeButton buttonRightUnit;
	private TESUnitTradeEntries trades;
	private GuiTextField squadronNameField;

	private TESEntityNPC currentDisplayedMob;
	private EntityLiving currentDisplayedMount;

	private float screenXSize;
	private float screenYSize;

	private int currentTradeEntryIndex;


	protected TESGuiHireBase(EntityPlayer entityplayer, TESHireableBase trader, World world) {
		super(new TESContainerUnitTrade(entityplayer, trader, world));
		xSize = 220;
		ySize = 256;
		theUnitTrader = trader;
		traderFaction = trader.getFaction();
	}

	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			if (button == buttonLeftUnit) {
				if (currentTradeEntryIndex > 0) {
					--currentTradeEntryIndex;
				}
			} else if (button == buttonHire) {
				String squadron = squadronNameField.getText();
				IMessage packet = new TESPacketBuyUnit(currentTradeEntryIndex, squadron);
				TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
			} else if (button == buttonRightUnit && currentTradeEntryIndex < trades.getTradeEntries().length - 1) {
				++currentTradeEntryIndex;
			}
		}
	}

	private TESUnitTradeEntry currentTrade() {
		return trades.getTradeEntries()[currentTradeEntryIndex];
	}

	private void drawCenteredString(String s, int i, int j, int k) {
		fontRendererObj.drawString(s, i - fontRendererObj.getStringWidth(s) / 2, j, k);
	}

	@Override
	public void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		guiTexture = new ResourceLocation("tes:textures/gui/npc/unit_trade.png");
		mc.getTextureManager().bindTexture(guiTexture);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		if (((TESContainerUnitTrade) inventorySlots).getAlignmentRewardSlots() > 0) {
			Slot slot = inventorySlots.getSlot(0);
			drawTexturedModalRect(guiLeft + slot.xDisplayPosition - 3, guiTop + slot.yDisplayPosition - 3, xSize, 16, 22, 22);
			if (!slot.getHasStack() && TESLevelData.getData(mc.thePlayer).getAlignment(traderFaction) < 1500.0f) {
				drawTexturedModalRect(guiLeft + slot.xDisplayPosition, guiTop + slot.yDisplayPosition, xSize, 0, 16, 16);
			}
		}
		drawMobOnGui(guiLeft + 32, guiTop + 109, guiLeft + 32 - screenXSize, guiTop + 109 - 50 - screenYSize);
		boolean squadronPrompt = StringUtils.isNullOrEmpty(squadronNameField.getText()) && !squadronNameField.isFocused();
		if (squadronPrompt) {
			String squadronMessage = StatCollector.translateToLocal("tes.container.unitTrade.squadronBox");
			squadronNameField.setText(EnumChatFormatting.DARK_GRAY + squadronMessage);
		}
		squadronNameField.drawTextBox();
		if (squadronPrompt) {
			squadronNameField.setText("");
		}
	}

	@Override
	public void drawGuiContainerForegroundLayer(int i, int j) {
		TESUnitTradeEntry curTrade = currentTrade();
		drawCenteredString(theUnitTrader.getNPCName(), 110, 11, 4210752);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 30, 162, 4210752);
		drawCenteredString(curTrade.getUnitTradeName(), 138, 50, 4210752);
		int reqX = 64;
		int reqXText = reqX + 19;
		int reqY = 65;
		int reqYTextBelow = 4;
		int reqGap = 18;
		GL11.glEnable(2896);
		GL11.glEnable(2884);
		itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), new ItemStack(TESItems.coin), reqX, reqY);
		GL11.glDisable(2896);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		int cost = curTrade.getCost(mc.thePlayer, theUnitTrader);
		fontRendererObj.drawString(String.valueOf(cost), reqXText, reqY + reqYTextBelow, 4210752);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		mc.getTextureManager().bindTexture(TESClientProxy.ALIGNMENT_TEXTURE);
		drawTexturedModalRect(reqX, reqY += reqGap, 0, 36, 16, 16);
		float alignment = curTrade.getAlignmentRequired();
		String alignS = TESAlignmentValues.formatAlignForDisplay(alignment);
		fontRendererObj.drawString(alignS, reqXText, reqY + reqYTextBelow, 4210752);
		if (curTrade.getPledgeType() != TESUnitTradeEntry.PledgeType.NONE) {
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			mc.getTextureManager().bindTexture(TESClientProxy.ALIGNMENT_TEXTURE);
			drawTexturedModalRect(reqX, reqY += reqGap, 0, 212, 16, 16);
			String pledge = StatCollector.translateToLocal("tes.container.unitTrade.pledge");
			fontRendererObj.drawString(pledge, reqXText, reqY + reqYTextBelow, 4210752);
			int i2 = i - guiLeft - reqX;
			int j2 = j - guiTop - reqY;
			if (i2 >= 0 && i2 < 16 && j2 >= 0 && j2 < 16) {
				String pledgeDesc = curTrade.getPledgeType().getCommandReqText(traderFaction);
				drawCreativeTabHoveringText(pledgeDesc, i - guiLeft, j - guiTop);
				GL11.glDisable(2896);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		if (((TESContainerUnitTrade) inventorySlots).getAlignmentRewardSlots() > 0) {
			Slot slot = inventorySlots.getSlot(0);
			boolean hasRewardCost = slot.getHasStack();
			if (hasRewardCost) {
				GL11.glEnable(2896);
				GL11.glEnable(2884);
				itemRender.renderItemAndEffectIntoGUI(fontRendererObj, mc.getTextureManager(), new ItemStack(TESItems.coin), 160, 100);
				GL11.glDisable(2896);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				cost = TESSlotAlignmentReward.REWARD_COST;
				fontRendererObj.drawString(String.valueOf(cost), 179, 104, 4210752);
			} else if (!slot.getHasStack() && TESLevelData.getData(mc.thePlayer).getAlignment(traderFaction) < 1500.0f && func_146978_c(slot.xDisplayPosition, slot.yDisplayPosition, 16, 16, i, j)) {
				drawCreativeTabHoveringText(StatCollector.translateToLocalFormatted("tes.container.unitTrade.requiresAlignment", 1500.0f), i - guiLeft, j - guiTop);
				GL11.glDisable(2896);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			}
		}
		if (curTrade.hasExtraInfo()) {
			String extraInfo = curTrade.getFormattedExtraInfo();
			boolean mouseover = i >= guiLeft + 49 && i < guiLeft + 49 + 9 && j >= guiTop + 106 && j < guiTop + 106 + 7;
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			mc.getTextureManager().bindTexture(guiTexture);
			drawTexturedModalRect(49, 106, 220, 38 + (mouseover ? 1 : 0) * 7, 9, 7);
			if (mouseover) {
				float z = zLevel;
				int stringWidth = 200;
				List<String> desc = fontRendererObj.listFormattedStringToWidth(extraInfo, stringWidth);
				func_146283_a(desc, i - guiLeft, j - guiTop);
				GL11.glDisable(2896);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				zLevel = z;
			}
		}
	}

	private void drawMobOnGui(int i, int j, float f, float f1) {
		Class<? extends Entity> entityClass = currentTrade().getEntityClass();
		Class<? extends Entity> mountClass = currentTrade().getMountClass();
		if (currentDisplayedMob == null || currentDisplayedMob.getClass() != entityClass || mountClass == null && currentDisplayedMount != null || mountClass != null && (currentDisplayedMount == null || currentDisplayedMount.getClass() != mountClass)) {
			currentDisplayedMob = currentTrade().getOrCreateHiredNPC(mc.theWorld);
			if (mountClass != null) {
				currentDisplayedMount = currentTrade().createHiredMount(mc.theWorld);
				currentDisplayedMob.mountEntity(currentDisplayedMount);
			} else {
				currentDisplayedMount = null;
			}
		}
		float size = currentDisplayedMob.width * currentDisplayedMob.height * currentDisplayedMob.width;
		if (currentDisplayedMount != null) {
			size += currentDisplayedMount.width * currentDisplayedMount.height * currentDisplayedMount.width * 0.5f;
		}
		float scale = MathHelper.sqrt_float(MathHelper.sqrt_float(1.0f / size)) * 30.0f;
		GL11.glEnable(2903);
		GL11.glPushMatrix();
		GL11.glTranslatef(i, j, 50.0f);
		GL11.glScalef(-scale, scale, scale);
		GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
		GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(-(float) Math.atan(f1 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
		currentDisplayedMob.renderYawOffset = (float) Math.atan(f / 40.0f) * 20.0f;
		currentDisplayedMob.rotationYaw = (float) Math.atan(f / 40.0f) * 40.0f;
		currentDisplayedMob.rotationPitch = -(float) Math.atan(f1 / 40.0f) * 20.0f;
		currentDisplayedMob.rotationYawHead = currentDisplayedMob.rotationYaw;
		GL11.glTranslatef(0.0f, currentDisplayedMob.yOffset, 0.0f);
		if (currentDisplayedMount != null) {
			GL11.glTranslatef(0.0f, (float) currentDisplayedMount.getMountedYOffset(), 0.0f);
		}
		RenderManager.instance.playerViewY = 180.0f;
		RenderManager.instance.renderEntityWithPosYaw(currentDisplayedMob, 0.0, 0.0, 0.0, 0.0f, 1.0f);
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(32826);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(3553);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		if (currentDisplayedMount != null) {
			GL11.glEnable(2903);
			GL11.glPushMatrix();
			GL11.glTranslatef(i, j, 50.0f);
			GL11.glScalef(-scale, scale, scale);
			GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
			GL11.glRotatef(135.0f, 0.0f, 1.0f, 0.0f);
			RenderHelper.enableStandardItemLighting();
			GL11.glRotatef(-135.0f, 0.0f, 1.0f, 0.0f);
			GL11.glRotatef(-(float) Math.atan(f1 / 40.0f) * 20.0f, 1.0f, 0.0f, 0.0f);
			currentDisplayedMount.renderYawOffset = (float) Math.atan(f / 40.0f) * 20.0f;
			currentDisplayedMount.rotationYaw = (float) Math.atan(f / 40.0f) * 40.0f;
			currentDisplayedMount.rotationPitch = -(float) Math.atan(f1 / 40.0f) * 20.0f;
			currentDisplayedMount.rotationYawHead = currentDisplayedMount.rotationYaw;
			GL11.glTranslatef(0.0f, currentDisplayedMount.yOffset, 0.0f);
			RenderManager.instance.playerViewY = 180.0f;
			RenderManager.instance.renderEntityWithPosYaw(currentDisplayedMount, 0.0, 0.0, 0.0, 0.0f, 1.0f);
			GL11.glPopMatrix();
			RenderHelper.disableStandardItemLighting();
			GL11.glDisable(32826);
			OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glDisable(3553);
			OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
		}
	}

	@Override
	public void drawScreen(int i, int j, float f) {
		buttonLeftUnit.enabled = currentTradeEntryIndex > 0;
		buttonHire.enabled = currentTrade().hasRequiredCostAndAlignment(mc.thePlayer, theUnitTrader);
		buttonRightUnit.enabled = currentTradeEntryIndex < trades.getTradeEntries().length - 1;
		super.drawScreen(i, j, f);
		screenXSize = i;
		screenYSize = j;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonLeftUnit = new TESGuiUnitTradeButton(0, guiLeft + 90, guiTop + 144, 12, 19);
		buttonList.add(buttonLeftUnit);
		buttonLeftUnit.enabled = false;
		buttonHire = new TESGuiUnitTradeButton(1, guiLeft + 102, guiTop + 144, 16, 19);
		buttonList.add(buttonHire);
		buttonRightUnit = new TESGuiUnitTradeButton(2, guiLeft + 118, guiTop + 144, 12, 19);
		buttonList.add(buttonRightUnit);
		squadronNameField = new GuiTextField(fontRendererObj, guiLeft + xSize / 2 - 80, guiTop + 120, 160, 20);
		squadronNameField.setMaxStringLength(TESSquadrons.SQUADRON_LENGTH_MAX);
	}

	@Override
	public void keyTyped(char c, int i) {
		if (squadronNameField.getVisible() && squadronNameField.textboxKeyTyped(c, i)) {
			return;
		}
		super.keyTyped(c, i);
	}

	@Override
	public void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		squadronNameField.mouseClicked(i, j, k);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		squadronNameField.updateCursorCounter();
	}

	protected void setTrades(TESUnitTradeEntries t) {
		trades = t;
	}
}