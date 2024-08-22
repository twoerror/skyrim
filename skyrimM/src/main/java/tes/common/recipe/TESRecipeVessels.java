package tes.common.recipe;

import cpw.mods.fml.common.registry.GameRegistry;
import tes.common.item.other.TESItemMug;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class TESRecipeVessels {
	private TESRecipeVessels() {
	}

	public static void addRecipes(ItemStack result, Item drinkBase, Object[] ingredients) {
		List<IRecipe> recipes = generateRecipes(result, drinkBase, ingredients);
		for (IRecipe r : recipes) {
			GameRegistry.addRecipe(r);
		}
	}

	public static void addRecipes(ItemStack result, Object[] ingredients) {
		addRecipes(result, null, ingredients);
	}

	private static List<IRecipe> generateRecipes(ItemStack result, Item drinkBase, Object[] ingredients) {
		List<IRecipe> recipes = new ArrayList<>();
		for (TESItemMug.Vessel v : TESItemMug.Vessel.values()) {
			Collection<Object> vIngredients = new ArrayList<>();
			ItemStack vBase = v.getEmptyVessel();
			if (drinkBase != null) {
				vBase = new ItemStack(drinkBase);
				TESItemMug.setVessel(vBase, v, true);
			}
			vIngredients.add(vBase);
			vIngredients.addAll(Arrays.asList(ingredients));
			ItemStack vResult = result.copy();
			TESItemMug.setVessel(vResult, v, true);
			IRecipe recipe = new ShapelessOreRecipe(vResult, vIngredients.toArray());
			recipes.add(recipe);
		}
		return recipes;
	}
}