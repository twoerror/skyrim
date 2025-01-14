package tes.client.render.other;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import tes.client.TESClientProxy;
import tes.client.TESTickHandlerClient;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESCapes;
import tes.common.database.TESShields;
import tes.common.faction.TESAlignmentValues;
import tes.common.fellowship.TESFellowshipClient;
import tes.common.world.TESWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.UUID;

public class TESRenderPlayer {
	public static final TESRenderPlayer INSTANCE = new TESRenderPlayer();

	private static final Minecraft MINECRAFT = Minecraft.getMinecraft();
	private static final RenderManager RENDER_MANAGER = RenderManager.instance;

	private static boolean shouldRenderAlignment(EntityPlayer entityplayer) {
		if (TESConfig.displayAlignmentAboveHead && shouldRenderPlayerHUD(entityplayer)) {
			if (TESLevelData.getData(entityplayer).getHideAlignment()) {
				UUID playerUuid = entityplayer.getUniqueID();
				List<TESFellowshipClient> fellowships = TESLevelData.getData(MINECRAFT.thePlayer).getClientFellowships();
				for (TESFellowshipClient fs : fellowships) {
					if (fs.containsPlayer(playerUuid)) {
						return true;
					}
				}
				return false;
			}
			return true;
		}
		return false;
	}

	private static boolean shouldRenderFellowPlayerHealth(EntityPlayer entityplayer) {
		if (TESConfig.fellowPlayerHealthBars && shouldRenderPlayerHUD(entityplayer)) {
			List<TESFellowshipClient> fellowships = TESLevelData.getData(MINECRAFT.thePlayer).getClientFellowships();
			for (TESFellowshipClient fs : fellowships) {
				if (fs.containsPlayer(entityplayer.getUniqueID())) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean shouldRenderPlayerHUD(EntityPlayer entityplayer) {
		return Minecraft.isGuiEnabled() && entityplayer != RENDER_MANAGER.livingPlayer && !entityplayer.isSneaking() && !entityplayer.isInvisibleToPlayer(MINECRAFT.thePlayer);
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void postRender(RenderPlayerEvent.Post event) {
		EntityPlayer entityplayer = event.entityPlayer;
		float tick = event.partialRenderTick;
		double d0 = RenderManager.renderPosX;
		double d1 = RenderManager.renderPosY;
		double d2 = RenderManager.renderPosZ;
		float f0 = (float) entityplayer.lastTickPosX + (float) (entityplayer.posX - entityplayer.lastTickPosX) * tick;
		float f1 = (float) entityplayer.lastTickPosY + (float) (entityplayer.posY - entityplayer.lastTickPosY) * tick;
		float f2 = (float) entityplayer.lastTickPosZ + (float) (entityplayer.posZ - entityplayer.lastTickPosZ) * tick;
		float fr0 = f0 - (float) d0;
		float fr1 = f1 - (float) d1;
		float fr2 = f2 - (float) d2;
		float yOffset = entityplayer.isPlayerSleeping() ? -1.5f : 0.0f;
		if (shouldRenderAlignment(entityplayer) && (MINECRAFT.theWorld.provider instanceof TESWorldProvider || TESConfig.alwaysShowAlignment)) {
			TESPlayerData clientPD = TESLevelData.getData(MINECRAFT.thePlayer);
			TESPlayerData otherPD = TESLevelData.getData(entityplayer);
			float alignment = otherPD.getAlignment(clientPD.getViewingFaction());
			double dist = entityplayer.getDistanceSqToEntity(RENDER_MANAGER.livingPlayer);
			float range = RendererLivingEntity.NAME_TAG_RANGE;
			if (dist < range * range) {
				FontRenderer fr = MINECRAFT.fontRenderer;
				GL11.glPushMatrix();
				GL11.glTranslatef(fr0, fr1, fr2);
				GL11.glTranslatef(0.0f, entityplayer.height + 0.6f + yOffset, 0.0f);
				GL11.glNormal3f(0.0f, 1.0f, 0.0f);
				GL11.glRotatef(-RENDER_MANAGER.playerViewY, 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(RENDER_MANAGER.playerViewX, 1.0f, 0.0f, 0.0f);
				GL11.glScalef(-1.0f, -1.0f, 1.0f);
				float scale = 0.025f;
				GL11.glScalef(scale, scale, scale);
				GL11.glDisable(2896);
				GL11.glDepthMask(false);
				GL11.glDisable(2929);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				String sAlign = TESAlignmentValues.formatAlignForDisplay(alignment);
				MINECRAFT.getTextureManager().bindTexture(TESClientProxy.ALIGNMENT_TEXTURE);
				TESTickHandlerClient.drawTexturedModalRect(-MathHelper.floor_double((fr.getStringWidth(sAlign) + 18) / 2.0), -19.0, 0, 36, 16, 16);
				TESTickHandlerClient.drawAlignmentText(fr, 18 - MathHelper.floor_double((fr.getStringWidth(sAlign) + 18) / 2.0), -12, sAlign, 1.0f);
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glDisable(3042);
				GL11.glEnable(2929);
				GL11.glDepthMask(true);
				GL11.glEnable(2896);
				GL11.glDisable(32826);
				GL11.glPopMatrix();
			}
		}
		if (shouldRenderFellowPlayerHealth(entityplayer)) {
			TESNPCRendering.renderHealthBar(entityplayer, fr0, fr1, fr2, new int[]{16375808, 12006707}, null);
		}
	}

	@SubscribeEvent
	@SuppressWarnings("MethodMayBeStatic")
	public void preRenderSpecials(RenderPlayerEvent.Specials.Pre event) {
		EntityPlayer entityplayer = event.entityPlayer;
		float tick = event.partialRenderTick;
		TESShields shield = TESLevelData.getData(entityplayer).getShield();
		TESCapes cape = TESLevelData.getData(entityplayer).getCape();
		double d = entityplayer.field_71091_bM + (entityplayer.field_71094_bP - entityplayer.field_71091_bM) * tick - (entityplayer.prevPosX + (entityplayer.posX - entityplayer.prevPosX) * tick);
		double d1 = entityplayer.field_71096_bN + (entityplayer.field_71095_bQ - entityplayer.field_71096_bN) * tick - (entityplayer.prevPosY + (entityplayer.posY - entityplayer.prevPosY) * tick);
		double d2 = entityplayer.field_71097_bO + (entityplayer.field_71085_bR - entityplayer.field_71097_bO) * tick - (entityplayer.prevPosZ + (entityplayer.posZ - entityplayer.prevPosZ) * tick);
		float f6 = entityplayer.prevRenderYawOffset + (entityplayer.renderYawOffset - entityplayer.prevRenderYawOffset) * tick;
		double d3 = MathHelper.sin(f6 * 3.1415927f / 180.0f);
		double d4 = -MathHelper.cos(f6 * 3.1415927f / 180.0f);
		float f7 = (float) d1 * 10.0f;
		if (f7 < -6.0f) {
			f7 = -6.0f;
		}
		if (f7 > 32.0f) {
			f7 = 32.0f;
		}
		float f8 = (float) (d * d3 + d2 * d4) * 100.0f;
		float f9 = (float) (d * d4 - d2 * d3) * 100.0f;
		if (f8 < 0.0f) {
			f8 = 0.0f;
		}
		float f10 = entityplayer.prevCameraYaw + (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * tick;
		f7 += MathHelper.sin((entityplayer.prevDistanceWalkedModified + (entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified) * tick) * 6.0f) * 32.0f * f10;
		if (entityplayer.isSneaking()) {
			f7 += 25.0f;
		}
		if (shield != null) {
			if (!entityplayer.isInvisible()) {
				TESRenderShield.renderShield(shield, entityplayer, event.renderer.modelBipedMain);
			} else if (!entityplayer.isInvisibleToPlayer(MINECRAFT.thePlayer)) {
				GL11.glPushMatrix();
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.15f);
				GL11.glDepthMask(false);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glAlphaFunc(516, 0.003921569f);
				TESRenderShield.renderShield(shield, entityplayer, event.renderer.modelBipedMain);
				GL11.glDisable(3042);
				GL11.glAlphaFunc(516, 0.1f);
				GL11.glPopMatrix();
				GL11.glDepthMask(true);
			}
		}
		if (cape != null) {
			if (!entityplayer.isInvisible()) {
				ResourceLocation capeTexture = cape.getCapeTexture();
				GL11.glPushMatrix();
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glTranslatef(0.0f, 0.0f, 0.125f);
				GL11.glRotatef(6.0f + f8 / 2.0f + f7, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(f9 / 2.0f, 0.0f, 0.0f, 1.0f);
				GL11.glRotatef(-f9 / 2.0f, 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				MINECRAFT.getTextureManager().bindTexture(capeTexture);
				event.renderer.modelBipedMain.renderCloak(0.0625f);
				GL11.glPopMatrix();
			} else if (!entityplayer.isInvisibleToPlayer(MINECRAFT.thePlayer)) {
				GL11.glPushMatrix();
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 0.15f);
				GL11.glDepthMask(false);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glAlphaFunc(516, 0.003921569f);
				ResourceLocation capeTexture = cape.getCapeTexture();
				GL11.glPushMatrix();
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				GL11.glTranslatef(0.0f, 0.0f, 0.125f);
				GL11.glRotatef(6.0f + f8 / 2.0f + f7, 1.0f, 0.0f, 0.0f);
				GL11.glRotatef(f9 / 2.0f, 0.0f, 0.0f, 1.0f);
				GL11.glRotatef(-f9 / 2.0f, 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				MINECRAFT.getTextureManager().bindTexture(capeTexture);
				event.renderer.modelBipedMain.renderCloak(0.0625f);
				GL11.glPopMatrix();
				GL11.glDisable(3042);
				GL11.glAlphaFunc(516, 0.1f);
				GL11.glPopMatrix();
				GL11.glDepthMask(true);
			}
		}
	}
}