package tes.common.enchant;

import tes.common.database.TESItems;
import tes.common.item.other.TESItemModifierTemplate;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;

public class TESEnchantmentCombining {
	private static final Collection<CombineRecipe> ALL_COMBINE_RECIPES = new ArrayList<>();

	private TESEnchantmentCombining() {
	}

	private static void combine(TESEnchantment in, TESEnchantment out, int cost) {
		if (!in.hasTemplateItem() || !out.hasTemplateItem()) {
			throw new IllegalArgumentException("Cannot create a modifier combining recipe for modifiers which lack scroll items!");
		}
		if (cost < 0) {
			throw new IllegalArgumentException("Cost must not be negative!");
		}
		ALL_COMBINE_RECIPES.add(new CombineRecipe(in, out, cost));
	}

	public static CombineRecipe getCombinationResult(ItemStack item1, ItemStack item2) {
		if (item1 != null && item2 != null && item1.getItem() instanceof TESItemModifierTemplate && item2.getItem() instanceof TESItemModifierTemplate) {
			TESEnchantment mod1 = TESItemModifierTemplate.getModifier(item1);
			TESEnchantment mod2 = TESItemModifierTemplate.getModifier(item2);
			if (mod1 == mod2) {
				for (CombineRecipe recipe : ALL_COMBINE_RECIPES) {
					if (recipe.getInputMod() == mod1) {
						return recipe;
					}
				}
			}
		}
		return null;
	}

	public static void onInit() {
		combine(TESEnchantment.STRONG_1, TESEnchantment.STRONG_2, 200);
		combine(TESEnchantment.STRONG_2, TESEnchantment.STRONG_3, 800);
		combine(TESEnchantment.STRONG_3, TESEnchantment.STRONG_4, 1600);

		combine(TESEnchantment.DURABLE_1, TESEnchantment.DURABLE_2, 300);
		combine(TESEnchantment.DURABLE_2, TESEnchantment.DURABLE_3, 1500);

		combine(TESEnchantment.KNOCKBACK_1, TESEnchantment.KNOCKBACK_2, 2500);

		combine(TESEnchantment.TOOL_SPEED_1, TESEnchantment.TOOL_SPEED_2, 200);
		combine(TESEnchantment.TOOL_SPEED_2, TESEnchantment.TOOL_SPEED_3, 800);
		combine(TESEnchantment.TOOL_SPEED_3, TESEnchantment.TOOL_SPEED_4, 1500);

		combine(TESEnchantment.LOOTING_1, TESEnchantment.LOOTING_2, 400);
		combine(TESEnchantment.LOOTING_2, TESEnchantment.LOOTING_3, 1500);

		combine(TESEnchantment.PROTECT_1, TESEnchantment.PROTECT_2, 2000);

		combine(TESEnchantment.PROTECT_FIRE_1, TESEnchantment.PROTECT_FIRE_2, 400);
		combine(TESEnchantment.PROTECT_FIRE_2, TESEnchantment.PROTECT_FIRE_3, 1500);

		combine(TESEnchantment.PROTECT_FALL_1, TESEnchantment.PROTECT_FALL_2, 400);
		combine(TESEnchantment.PROTECT_FALL_2, TESEnchantment.PROTECT_FALL_3, 1500);

		combine(TESEnchantment.PROTECT_RANGED_1, TESEnchantment.PROTECT_RANGED_2, 400);
		combine(TESEnchantment.PROTECT_RANGED_2, TESEnchantment.PROTECT_RANGED_3, 1500);

		combine(TESEnchantment.RANGED_STRONG_1, TESEnchantment.RANGED_STRONG_2, 400);
		combine(TESEnchantment.RANGED_STRONG_2, TESEnchantment.RANGED_STRONG_3, 1500);

		combine(TESEnchantment.RANGED_KNOCKBACK_1, TESEnchantment.RANGED_KNOCKBACK_2, 2500);
	}

	public static class CombineRecipe {
		private final TESEnchantment inputMod;
		private final TESEnchantment outputMod;
		private final int cost;

		protected CombineRecipe(TESEnchantment in, TESEnchantment out, int c) {
			inputMod = in;
			outputMod = out;
			cost = c;
		}

		public ItemStack createOutputItem() {
			ItemStack item = new ItemStack(TESItems.smithScroll);
			TESItemModifierTemplate.setModifier(item, outputMod);
			return item;
		}

		public int getCost() {
			return cost;
		}

		protected TESEnchantment getInputMod() {
			return inputMod;
		}
	}
}