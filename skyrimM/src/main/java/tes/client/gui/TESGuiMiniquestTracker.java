package tes.client.gui;

import tes.client.TESTickHandlerClient;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.quest.TESMiniQuest;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

public class TESGuiMiniquestTracker extends Gui {
	public static final TESGuiMiniquestTracker INSTANCE = new TESGuiMiniquestTracker();

	private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("tes:textures/gui/quest/tracker.png");
	private static final RenderItem ITEM_RENDERER = new RenderItem();

	private TESMiniQuest trackedQuest;

	private boolean holdingComplete;
	private int completeTime;

	private TESGuiMiniquestTracker() {
	}

	public void drawTracker(Minecraft mc, EntityPlayer entityplayer) {
		ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
		int width = resolution.getScaledWidth();
		FontRenderer fr = mc.fontRenderer;
		boolean flip = TESConfig.trackingQuestRight;
		if (entityplayer != null && trackedQuest != null) {
			float[] questRGB = trackedQuest.getQuestColorComponents();
			ItemStack itemstack = trackedQuest.getQuestIcon();
			String objective = trackedQuest.getQuestObjective();
			String progress = trackedQuest.getQuestProgressShorthand();
			float completion = trackedQuest.getCompletionFactor();
			boolean failed = trackedQuest.isFailed();
			boolean complete = trackedQuest.isCompleted();
			if (failed) {
				objective = trackedQuest.getQuestFailureShorthand();
			} else if (complete) {
				objective = StatCollector.translateToLocal("tes.gui.redBook.mq.diary.complete");
			}
			int barX = 16;
			int x = barX;
			int iconWidth = 20;
			if (flip) {
				x = width - barX - iconWidth;
			}
			GL11.glEnable(3008);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			mc.getTextureManager().bindTexture(GUI_TEXTURE);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			int iconHeight = 20;
			drawTexturedModalRect(x, 10, 0, 0, iconWidth, iconHeight);
			int iconX = x + (iconWidth - 16) / 2;
			int iconY = 10 + (iconHeight - 16) / 2;
			int gap = 4;
			int barWidth = 90;
			x = flip ? x - (barWidth + gap) : x + iconWidth + gap;
			int barEdge = 2;
			int meterWidth = barWidth - barEdge * 2;
			meterWidth = Math.round(meterWidth * completion);
			mc.getTextureManager().bindTexture(GUI_TEXTURE);
			GL11.glColor4f(questRGB[0], questRGB[1], questRGB[2], 1.0f);
			int barHeight = 15;
			drawTexturedModalRect(x + barEdge, 10, iconWidth + barEdge, barHeight, meterWidth, barHeight);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			drawTexturedModalRect(x, 10, iconWidth, 0, barWidth, barHeight);
			TESTickHandlerClient.drawAlignmentText(fr, x + barWidth / 2 - fr.getStringWidth(progress) / 2, 10 + barHeight - barHeight / 2 - fr.FONT_HEIGHT / 2, progress, 1.0f);
			fr.drawSplitString(objective, x, 10 + barHeight + gap, barWidth, 16777215);
			GL11.glDisable(3042);
			GL11.glDisable(3008);
			if (itemstack != null) {
				RenderHelper.enableGUIStandardItemLighting();
				GL11.glDisable(2896);
				GL11.glEnable(32826);
				GL11.glEnable(2896);
				GL11.glEnable(2884);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				ITEM_RENDERER.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), itemstack, iconX, iconY);
				GL11.glDisable(2896);
			}
		}
	}

	public void update(EntityPlayer entityplayer) {
		if (entityplayer == null) {
			trackedQuest = null;
		} else {
			if (trackedQuest != null && trackedQuest.isCompleted() && !holdingComplete) {
				completeTime = 200;
				holdingComplete = true;
			}
			TESMiniQuest currentTrackedQuest = TESLevelData.getData(entityplayer).getTrackingMiniQuest();
			if (completeTime > 0 && currentTrackedQuest == null) {
				--completeTime;
			} else {
				trackedQuest = currentTrackedQuest;
				holdingComplete = false;
			}
		}
	}

	public void setTrackedQuest(TESMiniQuest quest) {
		trackedQuest = quest;
	}
}