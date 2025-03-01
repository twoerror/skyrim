package integrator.handler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.FurnaceRecipeHandler;
import codechicken.nei.recipe.TemplateRecipeHandler;
import tes.client.gui.TESGuiOven;
import tes.common.tileentity.TESTileEntityOven;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TESHandlerOven extends TemplateRecipeHandler {
	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 120);
	}

	@Override
	public void drawExtras(int recipe) {
		drawProgressBar(75, 83, 176, 0, 14, 14, 48, 7);
		drawProgressBar(75, 29, 176, 14, 24, 25, 48, 1);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return TESGuiOven.class;
	}

	@Override
	public String getGuiTexture() {
		return "tes:textures/gui/oven.png";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tes.container.oven");
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		result.stackSize = 1;
		if (TESTileEntityOven.isCookResultAcceptable(result)) {
			Map<ItemStack, ItemStack> map = FurnaceRecipes.smelting().getSmeltingList();
			for (ItemStack itemStack : map.keySet()) {
				if (NEIServerUtils.areStacksSameTypeCrafting(FurnaceRecipes.smelting().getSmeltingResult(itemStack), result)) {
					arecipes.add(new CachedOvenRecipe(itemStack, result));
				}
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if ("item".equals(outputId)) {
			loadCraftingRecipes((ItemStack) results[0]);
		}
	}

	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		ingredient.stackSize = 1;
		ItemStack tmp = FurnaceRecipes.smelting().getSmeltingResult(ingredient);
		if (TESTileEntityOven.isCookResultAcceptable(tmp)) {
			arecipes.add(new CachedOvenRecipe(ingredient, tmp));
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if ("item".equals(inputId)) {
			loadUsageRecipes((ItemStack) ingredients[0]);
		}
	}

	@Override
	public TESHandlerOven newInstance() {
		return new TESHandlerOven();
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	private class CachedOvenRecipe extends TemplateRecipeHandler.CachedRecipe {
		private final List<PositionedStack> ingredients = new ArrayList<>();
		private final List<PositionedStack> results = new ArrayList<>();

		private CachedOvenRecipe(ItemStack ingredient, ItemStack result) {
			for (int i = 0; i < 9; i++) {
				ingredients.add(new PositionedStack(ingredient, 18 * i + 3, 10));
				results.add(new PositionedStack(result, 18 * i + 3, 56));
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return getCycledIngredients(cycleticks / 48, ingredients);
		}

		@Override
		public List<PositionedStack> getOtherStacks() {
			List<PositionedStack> tmp = new ArrayList<>();
			PositionedStack tmpStack = FurnaceRecipeHandler.afuels.get(cycleticks / 48 % FurnaceRecipeHandler.afuels.size()).stack;
			tmpStack.relx = 75;
			tmpStack.rely = 100;
			tmp.add(tmpStack);
			tmp.addAll(1, results);
			return tmp;
		}

		@Override
		public PositionedStack getResult() {
			return results.get(0);
		}
	}
}