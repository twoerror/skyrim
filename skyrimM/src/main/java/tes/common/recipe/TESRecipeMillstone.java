package tes.common.recipe;

import cpw.mods.fml.common.registry.GameRegistry;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TESRecipeMillstone {
	public static final Map<ItemStack, MillstoneResult> RECIPES = new HashMap<>();

	private TESRecipeMillstone() {
	}

	private static void addCrackedBricks(ItemStack itemstack, ItemStack result) {
		addRecipe(itemstack, result, 1.0f);
		GameRegistry.addSmelting(itemstack, result, 0.1f);
	}

	private static void addRecipe(Block block, ItemStack result) {
		addRecipe(block, result, 1.0f);
	}

	private static void addRecipe(Block block, ItemStack result, float chance) {
		addRecipe(Item.getItemFromBlock(block), result, chance);
	}

	private static void addRecipe(Item item, ItemStack result, float chance) {
		addRecipe(new ItemStack(item, 1, 32767), result, chance);
	}

	private static void addRecipe(ItemStack itemstack, ItemStack result) {
		addRecipe(itemstack, result, 1.0f);
	}

	private static void addRecipe(ItemStack itemstack, ItemStack result, float chance) {
		RECIPES.put(itemstack, new MillstoneResult(result, chance));
	}

	public static MillstoneResult getMillingResult(ItemStack itemstack) {
		for (Map.Entry<ItemStack, MillstoneResult> e : RECIPES.entrySet()) {
			ItemStack target = e.getKey();
			MillstoneResult result = e.getValue();
			if (!matches(itemstack, target)) {
				continue;
			}
			return result;
		}
		return null;
	}

	private static boolean matches(ItemStack itemstack, ItemStack target) {
		return target.getItem() == itemstack.getItem() && (target.getItemDamage() == 32767 || target.getItemDamage() == itemstack.getItemDamage());
	}

	public static void onInit() {
		addRecipe(Blocks.stone, new ItemStack(Blocks.cobblestone));
		addRecipe(Blocks.cobblestone, new ItemStack(Blocks.gravel), 0.75f);
		addRecipe(new ItemStack(TESBlocks.rock, 1, 0), new ItemStack(TESBlocks.basaltGravel), 0.75f);
		addRecipe(Blocks.gravel, new ItemStack(Items.flint), 0.25f);
		addRecipe(TESBlocks.basaltGravel, new ItemStack(Items.flint), 0.25f);
		addRecipe(TESBlocks.obsidianGravel, new ItemStack(TESItems.obsidianShard), 1.0f);
		addRecipe(TESBlocks.oreSalt, new ItemStack(TESItems.salt));
		addRecipe(new ItemStack(Blocks.sandstone, 1, 0), new ItemStack(Blocks.sand, 2, 0));
		addRecipe(new ItemStack(TESBlocks.redSandstone, 1, 0), new ItemStack(Blocks.sand, 2, 1));
		addRecipe(new ItemStack(TESBlocks.whiteSandstone, 1, 0), new ItemStack(TESBlocks.whiteSand, 2, 0));
		addCrackedBricks(new ItemStack(Blocks.brick_block, 1, 0), new ItemStack(TESBlocks.redBrick, 1, 1));
		addCrackedBricks(new ItemStack(Blocks.stonebrick, 1, 0), new ItemStack(Blocks.stonebrick, 1, 2));
		addCrackedBricks(new ItemStack(TESBlocks.brick1, 1, 0), new ItemStack(TESBlocks.brick1, 1, 7));
		addCrackedBricks(new ItemStack(TESBlocks.brick1, 1, 1), new ItemStack(TESBlocks.brick1, 1, 3));
		addCrackedBricks(new ItemStack(TESBlocks.brick1, 1, 15), new ItemStack(TESBlocks.brick3, 1, 11));
		addCrackedBricks(new ItemStack(TESBlocks.brick3, 1, 13), new ItemStack(TESBlocks.brick3, 1, 14));
		addCrackedBricks(new ItemStack(TESBlocks.brick4, 1, 0), new ItemStack(TESBlocks.brick4, 1, 2));
		addCrackedBricks(new ItemStack(TESBlocks.brick5, 1, 8), new ItemStack(TESBlocks.brick5, 1, 10));
		addCrackedBricks(new ItemStack(TESBlocks.brick5, 1, 11), new ItemStack(TESBlocks.brick5, 1, 14));
		addCrackedBricks(new ItemStack(TESBlocks.brick6, 1, 4), new ItemStack(TESBlocks.brick6, 1, 6));
	}

	public static class MillstoneResult {
		private final ItemStack resultItem;
		private final float chance;

		private MillstoneResult(ItemStack itemstack, float f) {
			resultItem = itemstack;
			chance = f;
		}

		public ItemStack getResultItem() {
			return resultItem;
		}

		public float getChance() {
			return chance;
		}
	}
}