package tes.client;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import tes.common.util.TESReflection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

import java.lang.reflect.Method;

public class TESReflectionClient {
	private static int[] colorCodes;

	private TESReflectionClient() {
	}

	private static float getCameraRoll(EntityRenderer renderer) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, renderer, "camRoll", "field_78495_O");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0.0f;
		}
	}

	private static int[] getColorCodes(FontRenderer fontRenderer) {
		if (colorCodes == null) {
			try {
				colorCodes = ObfuscationReflectionHelper.getPrivateValue(FontRenderer.class, fontRenderer, "colorCode", "field_78285_g");
			} catch (Exception e) {
				TESReflection.logFailure(e);
			}
		}
		return colorCodes;
	}

	public static int getCreativeTabIndex(GuiContainerCreative gui) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(GuiContainerCreative.class, gui, "selectedTabIndex", "field_147058_w");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0;
		}
	}

	public static int getFormattingColor(EnumChatFormatting ecf) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		int colorIndex = ecf.ordinal();
		int[] arr = getColorCodes(fr);
		if (arr != null) {
			return arr[colorIndex];
		}
		return 0xffffff;
	}

	public static float getFOVModifier(EntityRenderer renderer, float tick, boolean flag) {
		try {
			Method method = TESReflection.getPrivateMethod(EntityRenderer.class, renderer, new Class[]{Float.TYPE, Boolean.TYPE}, "getFOVModifier", "func_78481_a");
			return (Float) method.invoke(renderer, tick, flag);
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0.0f;
		}
	}

	public static int getGuiTop(GuiContainer gui) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gui, "guiTop", "field_147009_r");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0;
		}
	}

	public static int getGuiXSize(GuiContainer gui) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(GuiContainer.class, gui, "xSize", "field_146999_f");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0;
		}
	}

	public static float getHandFOV(EntityRenderer renderer) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, renderer, "fovModifierHand", "field_78507_R");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0.0f;
		}
	}

	public static ItemStack getHighlightedItemStack(GuiIngame gui) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, gui, "highlightingItemStack", "field_92016_l");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return null;
		}
	}

	public static int getHighlightedItemTicks(GuiIngame gui) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(GuiIngame.class, gui, "remainingHighlightTicks", "field_92017_k");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return 0;
		}
	}

	public static boolean hasGuiPotionEffects(InventoryEffectRenderer gui) {
		try {
			return ObfuscationReflectionHelper.getPrivateValue(InventoryEffectRenderer.class, gui, "field_147045_u");
		} catch (Exception e) {
			TESReflection.logFailure(e);
			return false;
		}
	}

	private static void setCameraRoll(EntityRenderer renderer, float roll) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, renderer, roll, "camRoll", "field_78495_O");
		} catch (Exception e) {
			TESReflection.logFailure(e);
		}
	}

	public static void setHandFOV(EntityRenderer renderer, float fov) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, renderer, fov, "fovModifierHand", "field_78507_R");
		} catch (Exception e) {
			TESReflection.logFailure(e);
		}
	}

	public static void setHighlightedItemTicks(GuiIngame gui, int ticks) {
		try {
			ObfuscationReflectionHelper.setPrivateValue(GuiIngame.class, gui, ticks, "remainingHighlightTicks", "field_92017_k");
		} catch (Exception e) {
			TESReflection.logFailure(e);
		}
	}

	public static void testAll(Minecraft mc) {
		setCameraRoll(mc.entityRenderer, getCameraRoll(mc.entityRenderer));
		setHandFOV(mc.entityRenderer, getHandFOV(mc.entityRenderer));
		getColorCodes(mc.fontRenderer);
		setHighlightedItemTicks(mc.ingameGUI, getHighlightedItemTicks(mc.ingameGUI));
		getHighlightedItemStack(mc.ingameGUI);
	}
}