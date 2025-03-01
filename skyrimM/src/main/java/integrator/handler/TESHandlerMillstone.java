package integrator.handler;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.TemplateRecipeHandler;
import tes.client.gui.TESGuiMillstone;
import tes.common.recipe.TESRecipeMillstone;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TESHandlerMillstone extends TemplateRecipeHandler {
	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 12, 166, 80);
	}

	@Override
	public void drawExtras(int recipe) {
		drawProgressBar(80, 35, 176, 0, 14, 14, 48, 7);
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() {
		return TESGuiMillstone.class;
	}

	@Override
	public String getGuiTexture() {
		return "tes:textures/gui/millstone.png";
	}

	@Override
	public String getRecipeName() {
		return StatCollector.translateToLocal("tes.container.millstone");
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (Map.Entry<ItemStack, TESRecipeMillstone.MillstoneResult> entry : TESRecipeMillstone.RECIPES.entrySet()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(entry.getValue().getResultItem(), result)) {
				arecipes.add(new CachedForgeRecipe(entry.getKey(), entry.getValue().getResultItem()));
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
		for (Map.Entry<ItemStack, TESRecipeMillstone.MillstoneResult> entry : TESRecipeMillstone.RECIPES.entrySet()) {
			if (NEIServerUtils.areStacksSameTypeCrafting(entry.getKey(), ingredient)) {
				arecipes.add(new CachedForgeRecipe(entry.getKey(), entry.getValue().getResultItem()));
			}
		}
	}

	@Override
	public void loadUsageRecipes(String inputId, Object... ingredients) {
		if ("item".equals(inputId)) {
			loadUsageRecipes((ItemStack) ingredients[0]);
		}
	}

	@Override
	public int recipiesPerPage() {
		return 1;
	}

	private class CachedForgeRecipe extends TemplateRecipeHandler.CachedRecipe {
		private final PositionedStack ingredient;
		private final PositionedStack resultItem;

		private CachedForgeRecipe(ItemStack ingredient, ItemStack resultItem) {
			this.ingredient = new PositionedStack(ingredient, 79, 13);
			this.resultItem = new PositionedStack(resultItem, 79, 59);
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return Collections.singletonList(ingredient);
		}

		@Override
		public PositionedStack getResult() {
			return resultItem;
		}
	}
}