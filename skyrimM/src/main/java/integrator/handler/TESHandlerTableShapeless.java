package integrator.handler;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.recipe.ShapelessRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.util.StatCollector;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.awt.*;
import java.util.List;

public class TESHandlerTableShapeless extends ShapelessRecipeHandler {
	private final List<IRecipe> recipeList;
	private final Class<? extends GuiContainer> guiClass;
	private final String recipeName;

	public TESHandlerTableShapeless(List<IRecipe> recipes, Class<? extends GuiContainer> gui, String name) {
		recipeList = recipes;
		guiClass = gui;
		recipeName = name;
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return guiClass;
	}

	@Override
	public String getOverlayIdentifier() {
		return getRecipeName();
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tes.container.crafting." + recipeName);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (IRecipe irecipe : recipeList) {
			if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
				ShapelessRecipeHandler.CachedShapelessRecipe recipe = null;
				if (irecipe instanceof ShapelessRecipes) {
					recipe = new ShapelessRecipeHandler.CachedShapelessRecipe();
				} else if (irecipe instanceof ShapelessOreRecipe) {
					recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);
				}
				if (recipe != null) {
					arecipes.add(recipe);
				}
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals(getOverlayIdentifier())) {
			for (IRecipe irecipe : recipeList) {
				ShapelessRecipeHandler.CachedShapelessRecipe recipe = null;
				if (irecipe instanceof ShapelessRecipes) {
					recipe = new ShapelessRecipeHandler.CachedShapelessRecipe();
				} else if (irecipe instanceof ShapelessOreRecipe) {
					recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);
				}
				if (recipe != null) {
					arecipes.add(recipe);
				}
			}
		} else {
			super.loadCraftingRecipes(outputId, results);
		}
	}

	@Override
	public void loadTransferRects() {
		transferRects.add(new TemplateRecipeHandler.RecipeTransferRect(new Rectangle(84, 23, 24, 18), getOverlayIdentifier()));
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (IRecipe irecipe : recipeList) {
			ShapelessRecipeHandler.CachedShapelessRecipe recipe = null;
			if (irecipe instanceof ShapelessRecipes) {
				recipe = new ShapelessRecipeHandler.CachedShapelessRecipe();
			} else if (irecipe instanceof ShapelessOreRecipe) {
				recipe = forgeShapelessRecipe((ShapelessOreRecipe) irecipe);
			}
			if (recipe != null && recipe.contains(recipe.ingredients, ingredient)) {
				recipe.setIngredientPermutation(recipe.ingredients, ingredient);
				arecipes.add(recipe);
			}
		}
	}

	@Override
	public TemplateRecipeHandler newInstance() {
		return new TESHandlerTableShapeless(recipeList, guiClass, recipeName);
	}

	@Override
	public int recipiesPerPage() {
		return 2;
	}
}