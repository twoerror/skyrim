package tes.client.render.other;

import tes.client.TESClientProxy;
import tes.common.item.weapon.TESItemBow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.util.EnumMap;
import java.util.Map;

public class TESRenderBow implements IItemRenderer {
	private static boolean renderingWeaponRack;

	private final TESRenderLargeItem largeItemRenderer;

	private Map<TESItemBow.BowState, TESExtraLargeIconToken> tokensPullStates;

	public TESRenderBow(TESRenderLargeItem large) {
		largeItemRenderer = large;
		if (largeItemRenderer != null) {
			tokensPullStates = new EnumMap<>(TESItemBow.BowState.class);
			for (TESItemBow.BowState state : TESItemBow.BowState.values()) {
				if (state != TESItemBow.BowState.HELD) {
					TESExtraLargeIconToken token = largeItemRenderer.extraIcon(state.getIconName());
					tokensPullStates.put(state, token);
				}
			}
		}
	}

	public static boolean isRenderingWeaponRack() {
		return renderingWeaponRack;
	}

	public static void setRenderingWeaponRack(boolean renderingWeaponRack) {
		TESRenderBow.renderingWeaponRack = renderingWeaponRack;
	}

	@Override
	public boolean handleRenderType(ItemStack itemstack, IItemRenderer.ItemRenderType type) {
		return type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON;
	}

	public boolean isLargeBow() {
		return largeItemRenderer != null;
	}

	@Override
	public void renderItem(IItemRenderer.ItemRenderType type, ItemStack itemstack, Object... data) {
		GL11.glPushMatrix();
		EntityLivingBase entity = (EntityLivingBase) data[1];
		if (!renderingWeaponRack && (Minecraft.getMinecraft().gameSettings.thirdPersonView != 0 || entity != Minecraft.getMinecraft().thePlayer)) {
			GL11.glTranslatef(0.9375f, 0.0625f, 0.0f);
			GL11.glRotatef(-335.0f, 0.0f, 0.0f, 1.0f);
			GL11.glRotatef(-50.0f, 0.0f, 1.0f, 0.0f);
			GL11.glScalef(0.6666667f, 0.6666667f, 0.6666667f);
			GL11.glTranslatef(0.0f, 0.3f, 0.0f);
			GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
			GL11.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(-60.0f, 0.0f, 0.0f, 1.0f);
			GL11.glScalef(2.6666667f, 2.6666667f, 2.6666667f);
			GL11.glTranslatef(-0.25f, -0.1875f, 0.1875f);
			GL11.glTranslatef(0.0f, 0.125f, 0.3125f);
			GL11.glRotatef(-20.0f, 0.0f, 1.0f, 0.0f);
			GL11.glScalef(0.625f, -0.625f, 0.625f);
			GL11.glRotatef(-100.0f, 1.0f, 0.0f, 0.0f);
			GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
			GL11.glTranslatef(0.0f, -0.3f, 0.0f);
			GL11.glScalef(1.5f, 1.5f, 1.5f);
			GL11.glRotatef(50.0f, 0.0f, 1.0f, 0.0f);
			GL11.glRotatef(335.0f, 0.0f, 0.0f, 1.0f);
			GL11.glTranslatef(-0.9375f, -0.0625f, 0.0f);
		}
		if (largeItemRenderer != null) {
			Item item = itemstack.getItem();
			if (!(item instanceof TESItemBow)) {
				throw new RuntimeException("Attempting to render a large bow which is not a bow");
			}
			TESItemBow bow = (TESItemBow) item;
			TESItemBow.BowState bowState;
			if (entity instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entity;
				ItemStack usingItem = entityplayer.getItemInUse();
				int useCount = entityplayer.getItemInUseCount();
				bowState = bow.getBowState(entityplayer, usingItem, useCount);
			} else {
				bowState = bow.getBowState(entity, itemstack, 0);
			}
			if (bowState == TESItemBow.BowState.HELD) {
				largeItemRenderer.renderLargeItem();
			} else {
				largeItemRenderer.renderLargeItem(tokensPullStates.get(bowState));
			}
		} else {
			IIcon icon = ((EntityLivingBase) data[1]).getItemIcon(itemstack, 0);
			icon = RenderBlocks.getInstance().getIconSafe(icon);
			float minU = icon.getMinU();
			float maxU = icon.getMaxU();
			float minV = icon.getMinV();
			float maxV = icon.getMaxV();
			int width = icon.getIconWidth();
			int height = icon.getIconWidth();
			Tessellator tessellator = Tessellator.instance;
			ItemRenderer.renderItemIn2D(tessellator, maxU, minV, minU, maxV, width, height, 0.0625f);
		}
		if (itemstack.hasEffect(0)) {
			TESClientProxy.renderEnchantmentEffect();
		}
		GL11.glDisable(32826);
		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack itemstack, IItemRenderer.ItemRendererHelper helper) {
		return false;
	}
}