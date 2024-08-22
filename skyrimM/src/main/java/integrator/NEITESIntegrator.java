package integrator;

import codechicken.nei.recipe.GuiCraftingRecipe;
import codechicken.nei.recipe.GuiUsageRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import tes.client.gui.TESGuiCraftingTable;
import tes.common.recipe.TESRecipe;
import integrator.handler.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.crafting.IRecipe;

import java.util.List;

public class NEITESIntegrator {
	private NEITESIntegrator() {
	}

	private static void registerHandler(String unlocalizedName, Class<? extends GuiContainer> guiClass, List<IRecipe> recipes) {
		registerHandler(new TESHandlerTableShaped(recipes, guiClass, unlocalizedName));
		registerHandler(new TESHandlerTableShapeless(recipes, guiClass, unlocalizedName));
	}

	private static void registerHandler(TemplateRecipeHandler handler) {
		GuiCraftingRecipe.craftinghandlers.add(handler);
		GuiUsageRecipe.usagehandlers.add(handler);
	}

	public static void registerRecipes() {
		registerHandler(new TESHandlerAlloyForge());
		registerHandler(new TESHandlerBarrel());
		registerHandler(new TESHandlerOven());
		registerHandler(new TESHandlerKebab());
		registerHandler(new TESHandlerUnsmeltery());
		registerHandler(new TESHandlerMillstone());
		registerHandler("empire", TESGuiCraftingTable.Empire.class, TESRecipe.Empire);
	}
}