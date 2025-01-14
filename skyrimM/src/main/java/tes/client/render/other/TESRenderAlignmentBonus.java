package tes.client.render.other;

import tes.client.TESClientProxy;
import tes.client.TESTickHandlerClient;
import tes.client.effect.TESEntityAlignmentBonus;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESAlignmentBonusMap;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class TESRenderAlignmentBonus extends Render {
	private static final Minecraft MINECRAFT = Minecraft.getMinecraft();

	public TESRenderAlignmentBonus() {
		shadowSize = 0.0f;
	}

	@Override
	public void doRender(Entity entity, double d, double d1, double d2, float f, float f1) {
		EntityClientPlayerMP entityplayer = MINECRAFT.thePlayer;
		TESPlayerData playerData = TESLevelData.getData(entityplayer);
		TESFaction viewingFaction = playerData.getViewingFaction();
		TESEntityAlignmentBonus alignmentBonus = (TESEntityAlignmentBonus) entity;
		TESFaction mainFaction = alignmentBonus.getMainFaction();
		TESAlignmentBonusMap factionBonusMap = alignmentBonus.getFactionBonusMap();
		TESFaction renderFaction = null;
		boolean showConquest = false;
		if (alignmentBonus.getConquestBonus() > 0.0f && playerData.isPledgedTo(viewingFaction) || alignmentBonus.getConquestBonus() < 0.0f && (viewingFaction == mainFaction || playerData.isPledgedTo(viewingFaction))) {
			renderFaction = viewingFaction;
			showConquest = true;
		} else if (!factionBonusMap.isEmpty()) {
			if (factionBonusMap.containsKey(viewingFaction)) {
				renderFaction = viewingFaction;
			} else if (factionBonusMap.size() == 1 && mainFaction.isPlayableAlignmentFaction() || mainFaction.isPlayableAlignmentFaction() && alignmentBonus.getPrevMainAlignment() >= 0.0f && factionBonusMap.get(mainFaction) < 0.0f) {
				renderFaction = mainFaction;
			} else {
				float alignment;
				for (Map.Entry<TESFaction, Float> entry : factionBonusMap.entrySet()) {
					TESFaction faction = entry.getKey();
					if (faction.isPlayableAlignmentFaction() && entry.getValue() > 0.0f) {
						alignment = playerData.getAlignment(faction);
						if (renderFaction == null || alignment > playerData.getAlignment(renderFaction)) {
							renderFaction = faction;
						}
					}
				}
				if (renderFaction == null) {
					if (mainFaction.isPlayableAlignmentFaction() && factionBonusMap.get(mainFaction) < 0.0f) {
						renderFaction = mainFaction;
					} else {
						for (Map.Entry<TESFaction, Float> entry : factionBonusMap.entrySet()) {
							TESFaction faction = entry.getKey();
							if (faction.isPlayableAlignmentFaction() && entry.getValue() < 0.0f) {
								alignment = playerData.getAlignment(faction);
								if (renderFaction == null || alignment > playerData.getAlignment(renderFaction)) {
									renderFaction = faction;
								}
							}
						}
					}
				}
			}
		}
		float renderBonus = factionBonusMap.getOrDefault(renderFaction, 0.0f);
		if (renderFaction != null && (renderBonus != 0.0f || showConquest)) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float) d, (float) d1, (float) d2);
			GL11.glNormal3f(0.0f, 1.0f, 0.0f);
			GL11.glRotatef(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
			GL11.glRotatef(renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
			GL11.glScalef(-0.025f, -0.025f, 0.025f);
			GL11.glDisable(2896);
			GL11.glDepthMask(false);
			GL11.glDisable(2929);
			GL11.glEnable(3042);
			GL11.glBlendFunc(770, 771);
			int age = alignmentBonus.getParticleAge();
			float alpha = age < 60 ? 1.0f : (80 - age) / 20.0f;
			renderBonusText(alignmentBonus, viewingFaction, renderFaction, !factionBonusMap.isEmpty(), renderBonus, showConquest, alpha);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glDisable(3042);
			GL11.glEnable(2929);
			GL11.glDepthMask(true);
			GL11.glEnable(2896);
			GL11.glPopMatrix();
		}
	}

	@Override
	public ResourceLocation getEntityTexture(Entity entity) {
		return TESClientProxy.ALIGNMENT_TEXTURE;
	}

	private void renderBonusText(TESEntityAlignmentBonus alignmentBonus, TESFaction viewingFaction, TESFaction renderFaction, boolean showAlign, float align, boolean showConquest, float alpha) {
		FontRenderer fr = MINECRAFT.fontRenderer;
		String strAlign = TESAlignmentValues.formatAlignForDisplay(align);
		String name = alignmentBonus.getName();
		float conq = alignmentBonus.getConquestBonus();
		GL11.glPushMatrix();
		boolean isViewingFaction = renderFaction == viewingFaction;
		if (!isViewingFaction) {
			float scale = 0.5f;
			GL11.glScalef(scale, scale, 1.0f);
			strAlign = strAlign + " (" + renderFaction.factionName() + "...)";
		}
		int x = -MathHelper.floor_double((fr.getStringWidth(strAlign) + 18) / 2.0);
		int y = -12;
		if (showAlign) {
			bindEntityTexture(alignmentBonus);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
			TESTickHandlerClient.drawTexturedModalRect(x, y - 5, 0, 36, 16, 16);
			TESTickHandlerClient.drawAlignmentText(fr, x + 18, y, strAlign, alpha);
			TESTickHandlerClient.drawAlignmentText(fr, -MathHelper.floor_double(fr.getStringWidth(name) / 2.0), y += 14, name, alpha);
		}
		if (showConquest && conq != 0.0f) {
			boolean negative = conq < 0.0f;
			String strConq = TESAlignmentValues.formatConqForDisplay(conq, true);
			x = -MathHelper.floor_double((fr.getStringWidth(strConq) + 18) / 2.0);
			if (showAlign) {
				y += 16;
			}
			bindEntityTexture(alignmentBonus);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
			TESTickHandlerClient.drawTexturedModalRect(x, y - 5, negative ? 16 : 0, 228, 16, 16);
			TESTickHandlerClient.drawConquestText(fr, x + 18, y, strConq, negative, alpha);
		}
		GL11.glPopMatrix();
	}
}