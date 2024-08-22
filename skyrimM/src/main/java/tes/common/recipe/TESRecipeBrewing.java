package tes.common.recipe;

import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.util.ArrayList;
import java.util.Collection;

public class TESRecipeBrewing {
	public static final Collection<ShapelessOreRecipe> RECIPES = new ArrayList<>();
	public static final int BARREL_CAPACITY = 16;

	private TESRecipeBrewing() {
	}

	private static void addBrewingRecipe(ItemStack result, Object... ingredients) {
		if (ingredients.length != 6) {
			throw new IllegalArgumentException("Brewing recipes must contain exactly 6 items");
		}
		RECIPES.add(new ShapelessOreRecipe(result, ingredients));
	}

	public static ItemStack findMatchingRecipe(IInventory barrel) {
		for (int i = 6; i < 9; ++i) {
			ItemStack itemstack = barrel.getStackInSlot(i);
			if (isWaterSource(itemstack)) {
				continue;
			}
			return null;
		}
		block1:
		for (ShapelessOreRecipe recipe : RECIPES) {
			Collection<Object> ingredients = new ArrayList<>(recipe.getInput());
			for (int i = 0; i < 6; ++i) {
				ItemStack itemstack = barrel.getStackInSlot(i);
				if (itemstack == null) {
					continue;
				}
				boolean inRecipe = false;
				for (Object next : ingredients) {
					boolean match = false;
					if (next instanceof ItemStack) {
						match = TESRecipe.checkItemEquals((ItemStack) next, itemstack);
					} else if (next instanceof ArrayList) {
						for (ItemStack item : (Iterable<ItemStack>) next) {
							match = match || TESRecipe.checkItemEquals(item, itemstack);
						}
					}
					if (!match) {
						continue;
					}
					inRecipe = true;
					ingredients.remove(next);
					break;
				}
				if (!inRecipe) {
					continue block1;
				}
			}
			if (!ingredients.isEmpty()) {
				continue;
			}
			return recipe.getRecipeOutput().copy();
		}
		return null;
	}

	public static boolean isWaterSource(ItemStack itemstack) {
		return itemstack != null && itemstack.getItem() == Items.water_bucket;
	}

	public static void onInit() {
		addBrewingRecipe(new ItemStack(TESItems.mugAle, BARREL_CAPACITY), Items.wheat, Items.wheat, Items.wheat, Items.wheat, Items.wheat, Items.wheat);
		addBrewingRecipe(new ItemStack(TESItems.mugUnsulliedTonic, BARREL_CAPACITY), "bone", "bone", "bone", "bone", "bone", "bone");
		addBrewingRecipe(new ItemStack(TESItems.mugMead, BARREL_CAPACITY), Items.sugar, Items.sugar, Items.sugar, Items.sugar, Items.sugar, Items.sugar);
		addBrewingRecipe(new ItemStack(TESItems.mugCider, BARREL_CAPACITY), "apple", "apple", "apple", "apple", "apple", "apple");
		addBrewingRecipe(new ItemStack(TESItems.mugPerry, BARREL_CAPACITY), TESItems.pear, TESItems.pear, TESItems.pear, TESItems.pear, TESItems.pear, TESItems.pear);
		addBrewingRecipe(new ItemStack(TESItems.mugCherryLiqueur, BARREL_CAPACITY), TESItems.cherry, TESItems.cherry, TESItems.cherry, TESItems.cherry, TESItems.cherry, TESItems.cherry);
		addBrewingRecipe(new ItemStack(TESItems.mugRum, BARREL_CAPACITY), Items.reeds, Items.reeds, Items.reeds, Items.reeds, Items.reeds, Items.reeds);
		addBrewingRecipe(new ItemStack(TESItems.mugPlantainBrew, BARREL_CAPACITY), TESBlocks.plantain, TESBlocks.plantain, TESBlocks.plantain, TESBlocks.plantain, TESBlocks.plantain, TESBlocks.plantain);
		addBrewingRecipe(new ItemStack(TESItems.mugVodka, BARREL_CAPACITY), Items.potato, Items.potato, Items.potato, Items.potato, Items.potato, Items.potato);
		addBrewingRecipe(new ItemStack(TESItems.mugMapleBeer, BARREL_CAPACITY), Items.wheat, Items.wheat, Items.wheat, Items.wheat, TESItems.mapleSyrup, TESItems.mapleSyrup);
		addBrewingRecipe(new ItemStack(TESItems.mugAraq, BARREL_CAPACITY), TESItems.date, TESItems.date, TESItems.date, TESItems.date, TESItems.date, TESItems.date);
		addBrewingRecipe(new ItemStack(TESItems.mugCarrotWine, BARREL_CAPACITY), Items.carrot, Items.carrot, Items.carrot, Items.carrot, Items.carrot, Items.carrot);
		addBrewingRecipe(new ItemStack(TESItems.mugBananaBeer, BARREL_CAPACITY), TESItems.banana, TESItems.banana, TESItems.banana, TESItems.banana, TESItems.banana, TESItems.banana);
		addBrewingRecipe(new ItemStack(TESItems.mugMelonLiqueur, BARREL_CAPACITY), Items.melon, Items.melon, Items.melon, Items.melon, Items.melon, Items.melon);
		addBrewingRecipe(new ItemStack(TESItems.mugCactusLiqueur, BARREL_CAPACITY), Blocks.cactus, Blocks.cactus, Blocks.cactus, Blocks.cactus, Blocks.cactus, Blocks.cactus);
		addBrewingRecipe(new ItemStack(TESItems.mugLemonLiqueur, BARREL_CAPACITY), TESItems.lemon, TESItems.lemon, TESItems.lemon, TESItems.lemon, TESItems.lemon, TESItems.lemon);
		addBrewingRecipe(new ItemStack(TESItems.mugLimeLiqueur, BARREL_CAPACITY), TESItems.lime, TESItems.lime, TESItems.lime, TESItems.lime, TESItems.lime, TESItems.lime);
		addBrewingRecipe(new ItemStack(TESItems.mugCornLiquor, BARREL_CAPACITY), TESItems.corn, TESItems.corn, TESItems.corn, TESItems.corn, TESItems.corn, TESItems.corn);
		addBrewingRecipe(new ItemStack(TESItems.mugRedWine, BARREL_CAPACITY), TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed);
		addBrewingRecipe(new ItemStack(TESItems.mugWhiteWine, BARREL_CAPACITY), TESItems.grapeWhite, TESItems.grapeWhite, TESItems.grapeWhite, TESItems.grapeWhite, TESItems.grapeWhite, TESItems.grapeWhite);
		addBrewingRecipe(new ItemStack(TESItems.mugShadeEvening, BARREL_CAPACITY), Items.dye, Items.dye, Items.dye, Items.dye, Items.dye, Items.dye);
		addBrewingRecipe(new ItemStack(TESItems.mugPlumKvass, BARREL_CAPACITY), Items.wheat, Items.wheat, Items.wheat, TESItems.plum, TESItems.plum, TESItems.plum);
		addBrewingRecipe(new ItemStack(TESItems.mugTermiteTequila, BARREL_CAPACITY), Blocks.cactus, Blocks.cactus, Blocks.cactus, Blocks.cactus, Blocks.cactus, TESItems.termite);
		addBrewingRecipe(new ItemStack(TESItems.mugSourMilk, BARREL_CAPACITY), Items.milk_bucket, Items.milk_bucket, Items.milk_bucket, Items.milk_bucket, Items.milk_bucket, Items.milk_bucket);
		addBrewingRecipe(new ItemStack(TESItems.mugPomegranateWine, BARREL_CAPACITY), TESItems.pomegranate, TESItems.pomegranate, TESItems.pomegranate, TESItems.pomegranate, TESItems.pomegranate, TESItems.pomegranate);
		addBrewingRecipe(new ItemStack(TESItems.mugEthanol, BARREL_CAPACITY), Items.potato, Items.potato, Items.potato, Items.wheat, Items.wheat, Items.wheat);
		addBrewingRecipe(new ItemStack(TESItems.mugWhisky, BARREL_CAPACITY), TESItems.corn, TESItems.corn, TESItems.corn, Items.wheat, Items.wheat, Items.wheat);
		addBrewingRecipe(new ItemStack(TESItems.mugSambuca, BARREL_CAPACITY), TESItems.elderberry, TESItems.elderberry, TESItems.elderberry, TESItems.elderberry, TESItems.elderberry, TESItems.elderberry);
		addBrewingRecipe(new ItemStack(TESItems.mugGin, BARREL_CAPACITY), Items.wheat, Items.wheat, Items.wheat, Items.wheat, TESItems.almond, TESItems.almond);
		addBrewingRecipe(new ItemStack(TESItems.mugBrandy, BARREL_CAPACITY), TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed, TESItems.plum, TESItems.plum);
		addBrewingRecipe(new ItemStack(TESItems.mugPoppyMilk, BARREL_CAPACITY), Blocks.red_flower, Blocks.red_flower, Blocks.red_flower, Blocks.red_flower, Blocks.red_flower, Blocks.red_flower);
		addBrewingRecipe(new ItemStack(TESItems.mugWildFire, BARREL_CAPACITY), TESBlocks.wildFireJar, TESBlocks.wildFireJar, TESBlocks.wildFireJar, TESBlocks.wildFireJar, TESBlocks.wildFireJar, TESBlocks.wildFireJar);
	}
}