package tes.common.recipe;

import cpw.mods.fml.common.registry.GameRegistry;
import tes.common.TESConfig;
import tes.common.block.leaves.TESBlockLeavesBase;
import tes.common.block.other.TESBlockConcretePowder;
import tes.common.block.other.TESBlockFallenLeaves;
import tes.common.block.other.TESBlockStairs;
import tes.common.block.other.TESBlockTreasurePile;
import tes.common.block.planks.TESBlockPlanksBase;
import tes.common.block.sapling.TESBlockSaplingBase;
import tes.common.block.slab.TESBlockSlabBase;
import tes.common.block.wood.TESBlockWoodBase;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import tes.common.database.TESMaterial;
import tes.common.item.other.TESItemBanner;
import tes.common.item.other.TESItemBerry;
import tes.common.item.other.TESItemRobes;
import tes.common.util.TESEnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.material.Material;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipesArmorDyes;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import java.lang.reflect.Field;
import java.util.*;

public class TESRecipe {
	public static final List<IRecipe> Empire = new ArrayList<>();

	private static final String[] DYE_ORE_NAMES = {"dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyePurple", "dyeCyan", "dyeLightGray", "dyeGray", "dyePink", "dyeLime", "dyeYellow", "dyeLightBlue", "dyeMagenta", "dyeOrange", "dyeWhite"};
	private static final Collection<IRecipe> SLAB = new ArrayList<>();
	private static final Collection<IRecipe> TINY_BASALT = new ArrayList<>();
	private static final Collection<IRecipe> COMMON_WESTEROS = new ArrayList<>();
	private static final Collection<IRecipe> COMMON_ESSOS = new ArrayList<>();

	private TESRecipe() {
	}

	private static void addDyeableWoolRobeRecipes(Collection<IRecipe> recipeList, ItemStack result, Object... params) {
		for (int i = 0; i <= 15; ++i) {
			Object[] paramsDyed = Arrays.copyOf(params, params.length);
			ItemStack wool = new ItemStack(Blocks.wool, 1, i);
			for (int l = 0; l < paramsDyed.length; ++l) {
				Object param = paramsDyed[l];
				if (param instanceof Block && param == Block.getBlockFromItem(wool.getItem())) {
					paramsDyed[l] = wool.copy();
					continue;
				}
				if (!(param instanceof ItemStack) || ((ItemStack) param).getItem() != wool.getItem()) {
					continue;
				}
				paramsDyed[l] = wool.copy();
			}
			ItemStack resultDyed = result.copy();
			float[] colors = EntitySheep.fleeceColorTable[i];
			float r = colors[0];
			float g = colors[1];
			float b = colors[2];
			if (r != 1.0f || g != 1.0f || b != 1.0f) {
				int rI = (int) (r * 255.0f);
				int gI = (int) (g * 255.0f);
				int bI = (int) (b * 255.0f);
				int rgb = rI << 16 | gI << 8 | bI;
				TESItemRobes.setRobesColor(resultDyed, rgb);
			}
			recipeList.add(new ShapedOreRecipe(resultDyed, paramsDyed));
		}
	}

	private static void addSmeltingXPForItem(Item item, float xp) {
		try {
			Field field = FurnaceRecipes.class.getDeclaredFields()[2];
			field.setAccessible(true);
			Map<ItemStack, Float> map = (Map<ItemStack, Float>) field.get(FurnaceRecipes.smelting());
			map.put(new ItemStack(item, 1, 32767), xp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static boolean checkItemEquals(ItemStack target, ItemStack input) {
		return target.getItem().equals(input.getItem()) && (target.getItemDamage() == 32767 || target.getItemDamage() == input.getItemDamage());
	}
	
	private static void createEmpireRecipes() {
	
	}

	rivate static void createStandardRecipes() {
		int i;
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick6, 1, 3), "XXX", "XYX", "XXX", 'X', TESItems.westerosSword, 'Y', Items.lava_bucket));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick6, 1, 3), "XXX", "XYX", "XXX", 'X', Items.iron_sword, 'Y', Items.lava_bucket));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.flintSpear), "  X", " Y ", "Y  ", 'X', Items.flint, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.flintDagger), "X", "Y", 'X', Items.flint, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.club), "X", "X", "X", 'X', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.trident), " XX", " YX", "Y  ", 'X', "IngotIron", 'Y', "stickWood"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wildFireJar), "XYX", "YZY", "XYX", 'X', "IngotIron", 'Y', Items.gunpowder, 'Z', TESItems.fuse));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.firePot, 4), "Z", "Y", "X", 'X', "IngotIron", 'Y', Items.gunpowder, 'Z', TESItems.fuse));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.skullStaff), "X", "Y", "Y", 'X', Items.skull, 'Y', "stickWood"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.beacon), "XXX", "XXX", "YYY", 'X', "logWood", 'Y', Blocks.cobblestone));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.kebabStandSand), " X ", " Y ", "ZZZ", 'X', "plankWood", 'Y', "stickWood", 'Z', Blocks.sandstone));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.kebabStand), " X ", " Y ", "ZZZ", 'X', "plankWood", 'Y', "stickWood", 'Z', Blocks.cobblestone));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.shishKebab, 2), "  X", " X ", "Y  ", 'X', TESItems.kebab, 'Y', "stickWood"));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick1, 4, 15), "XX", "XX", 'X', new ItemStack(Blocks.sandstone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle4, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsSandstoneBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone1, 6, 15), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.pillar1, 3, 5), "X", "X", "X", 'X', new ItemStack(Blocks.sandstone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle4, 6, 7), "XXX", 'X', new ItemStack(TESBlocks.pillar1, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick3, 1, 8), "XX", "XX", 'X', new ItemStack(TESBlocks.brick1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle7, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsSandstoneBrickCracked, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone3, 6, 3), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick3, 4, 13), "XX", "XX", 'X', new ItemStack(TESBlocks.redSandstone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle7, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsSandstoneBrickRed, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone3, 6, 4), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick3, 1, 15), "XX", "XX", 'X', new ItemStack(TESBlocks.brick3, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle7, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsSandstoneBrickRedCracked, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone3, 6, 5), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick3, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.pillar1, 3, 15), "X", "X", "X", 'X', new ItemStack(TESBlocks.redSandstone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle7, 6, 4), "XXX", 'X', new ItemStack(TESBlocks.pillar1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick4, 1, 7), " X ", "XYX", " X ", 'X', "gemLapis", 'Y', new ItemStack(TESBlocks.brick1, 1, 15)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.brick1, 1, 2), new ItemStack(TESBlocks.brick1, 1, 1), "vine"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick1, 1, 5), "XX", "XX", 'X', new ItemStack(TESBlocks.brick1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick1, 4, 1), "XX", "XX", 'X', new ItemStack(TESBlocks.rock, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.pillar1, 3, 6), "X", "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle1, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.smoothStone, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle1, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle1, 6, 4), "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle1, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle11, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle5, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.pillar1, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.smoothStone, 2, 1), "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsAndesite, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsAndesiteBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsAndesiteBrickCracked, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsAndesiteBrickMossy, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone1, 6, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone1, 6, 3), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone1, 6, 4), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone1, 6, 5), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 3)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.chainmail_helmet), "XXX", "Y Y", 'X', "IngotIron", 'Y', TESItems.ironNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.chainmail_chestplate), "X X", "YYY", "XXX", 'X', "IngotIron", 'Y', TESItems.ironNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.chainmail_leggings), "XXX", "Y Y", "X X", 'X', "IngotIron", 'Y', TESItems.ironNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.chainmail_boots), "Y Y", "X X", 'X', "IngotIron", 'Y', TESItems.ironNugget));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeChainmailHelmet), "XXX", "Y Y", 'X', TESItems.bronzeIngot, 'Y', TESItems.bronzeNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeChainmailChestplate), "X X", "YYY", "XXX", 'X', TESItems.bronzeIngot, 'Y', TESItems.bronzeNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeChainmailLeggings), "XXX", "Y Y", "X X", 'X', TESItems.bronzeIngot, 'Y', TESItems.bronzeNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeChainmailBoots), "Y Y", "X X", 'X', TESItems.bronzeIngot, 'Y', TESItems.bronzeNugget));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelAxe), "XX", "XY", " Y", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelDagger), "X", "Y", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelHalberd), " XX", " YX", "Y  ", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelHoe), "XX", " Y", " Y", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelPickaxe), "XXX", " Y ", " Y ", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelShovel), "X", "Y", "Y", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.alloySteelSword), "X", "X", "Y", 'X', TESItems.alloySteelIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick6, 1, 4), "XX", "XX", 'X', new ItemStack(TESBlocks.rock, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brick6, 1, 5), "XX", "XX", 'X', new ItemStack(TESBlocks.brick6, 1, 4)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.brick6, 1, 7), new ItemStack(TESBlocks.brick6, 1, 4), "vine"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.iceHeavySword), " XX", " X ", "Y  ", 'X', TESItems.iceShard, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.iceSword), "X", "X", "Y", 'X', TESItems.iceShard, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.iceSpear), "  X", " Y ", "Y  ", 'X', TESItems.iceShard, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.smoothStone, 2, 6), "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsLabradorite, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsLabradoriteBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsLabradoriteBrickMossy, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsLabradoriteBrickCracked, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.pillar2, 3, 0), "X", "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone4, 6, 5), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone4, 6, 6), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone4, 6, 7), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallStone4, 6, 8), "XXX", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle2, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.smoothStone, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle2, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.pillar2, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle2, 6, 4), "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle2, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle2, 6, 6), "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingle2, 6, 7), "XXX", 'X', new ItemStack(TESBlocks.brick6, 1, 7)));
		GameRegistry.addRecipe(new ItemStack(Blocks.dirt, 4, 1), "XY", "YX", 'X', new ItemStack(Blocks.dirt, 1, 0), 'Y', Blocks.gravel);
		GameRegistry.addRecipe(new ItemStack(Blocks.obsidian, 1), "XXX", "XXX", "XXX", 'X', TESItems.obsidianShard);
		GameRegistry.addRecipe(new ItemStack(Blocks.packed_ice), "XX", "XX", 'X', Blocks.ice);
		GameRegistry.addRecipe(new ItemStack(Blocks.stone_brick_stairs, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(Blocks.stone_slab, 6, 5), "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(Blocks.stonebrick, 1, 3), "XX", "XX", 'X', new ItemStack(Blocks.stonebrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.alloyForge), "XXX", "X X", "XXX", 'X', TESBlocks.scorchedStone);
		GameRegistry.addRecipe(new ItemStack(TESItems.alloySteelIngot), "XXX", "XXX", "XXX", 'X', TESItems.alloySteelNugget);
		GameRegistry.addRecipe(new ItemStack(TESItems.bananaBread), "XYX", 'X', Items.wheat, 'Y', TESItems.banana);
		GameRegistry.addRecipe(new ItemStack(TESItems.bananaCake), "AAA", "BCB", "DDD", 'A', Items.milk_bucket, 'B', TESItems.banana, 'C', Items.egg, 'D', Items.wheat);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.bank), "XXX", "XYX", "XXX", 'X', Blocks.cobblestone, 'Y', TESItems.coin);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 0), "XXX", "XXX", "XXX", 'X', TESItems.topaz);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 1), "XXX", "XXX", "XXX", 'X', TESItems.amethyst);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 2), "XXX", "XXX", "XXX", 'X', TESItems.sapphire);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 3), "XXX", "XXX", "XXX", 'X', TESItems.ruby);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 4), "XXX", "XXX", "XXX", 'X', TESItems.amber);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 5), "XXX", "XXX", "XXX", 'X', TESItems.diamond);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 6), "XXX", "XXX", "XXX", 'X', TESItems.pearl);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 7), "XXX", "XXX", "XXX", 'X', TESItems.opal);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 8), "XX", "XX", 'X', TESItems.coral);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockGem, 1, 9), "XXX", "XXX", "XXX", 'X', TESItems.emerald);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal1, 1, 0), "XXX", "XXX", "XXX", 'X', TESItems.copperIngot);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal1, 1, 1), "XXX", "XXX", "XXX", 'X', TESItems.tinIngot);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal1, 1, 13), "XXX", "XXX", "XXX", 'X', TESItems.sulfur);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal1, 1, 14), "XXX", "XXX", "XXX", 'X', TESItems.saltpeter);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal1, 1, 2), "XXX", "XXX", "XXX", 'X', TESItems.bronzeIngot);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal1, 1, 3), "XXX", "XXX", "XXX", 'X', TESItems.silverIngot);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal2, 1, 3), "XXX", "XXX", "XXX", 'X', TESItems.salt);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.blockMetal2, 1, 4), "XXX", "XXX", "XXX", 'X', TESItems.alloySteelIngot);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.brick1, 4, 14), "XX", "XX", 'X', new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.brick2, 4, 2), "XX", "XX", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.brick3, 1, 0), "XX", "XX", 'X', new ItemStack(TESBlocks.brick1, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.brick3, 1, 1), "XX", "XX", 'X', new ItemStack(TESBlocks.brick2, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.brick4, 4, 15), "XX", "XX", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.brick5, 4, 0), "XX", "XX", 'X', new ItemStack(TESBlocks.mud, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESItems.bronzeBoots), "X X", "X X", 'X', TESItems.bronzeIngot);
		GameRegistry.addRecipe(new ItemStack(TESItems.bronzeChestplate), "X X", "XXX", "XXX", 'X', TESItems.bronzeIngot);
		GameRegistry.addRecipe(new ItemStack(TESItems.bronzeHelmet), "XXX", "X X", 'X', TESItems.bronzeIngot);
		GameRegistry.addRecipe(new ItemStack(TESItems.bronzeLeggings), "XXX", "X X", "X X", 'X', TESItems.bronzeIngot);
		GameRegistry.addRecipe(new ItemStack(TESItems.cherryPie), "AAA", "BCB", "DDD", 'A', Items.milk_bucket, 'B', TESItems.cherry, 'C', Items.sugar, 'D', Items.wheat);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.chestBasket), "XXX", "XYX", "XXX", 'X', TESBlocks.driedReeds, 'Y', Blocks.chest);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.chestStone), "XXX", "XYX", "XXX", 'X', Blocks.cobblestone, 'Y', Blocks.chest);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.chestSandstone), "XXX", "XYX", "XXX", 'X', Blocks.sandstone, 'Y', Blocks.chest);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.clayTile, 4, 0), "XX", "XX", 'X', new ItemStack(Blocks.hardened_clay, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESItems.copperIngot), "XXX", "XXX", "XXX", 'X', TESItems.copperNugget);
		GameRegistry.addRecipe(new ItemStack(TESItems.bronzeIngot), "XXX", "XXX", "XXX", 'X', TESItems.bronzeNugget);
		GameRegistry.addRecipe(new ItemStack(Items.iron_ingot), "XXX", "XXX", "XXX", 'X', TESItems.ironNugget);
		GameRegistry.addRecipe(new ItemStack(TESItems.cornBread), "XXX", 'X', TESItems.corn);
		GameRegistry.addRecipe(new ItemStack(TESItems.diamondHorseArmor), "X  ", "XYX", "XXX", 'X', Items.diamond, 'Y', Items.leather);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.dirtPath, 2, 0), "XX", 'X', Blocks.dirt);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.dirtPath, 2, 1), "XX", 'X', TESBlocks.mud);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.dirtPath, 3, 2), "XYZ", 'X', Blocks.dirt, 'Y', Blocks.gravel, 'Z', Blocks.cobblestone);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.glass, 4), "XX", "XX", 'X', Blocks.glass);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.glassPane, 16), "XXX", "XXX", 'X', TESBlocks.glass);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.hearth, 3), "XXX", "YYY", 'X', new ItemStack(Items.coal, 1, 32767), 'Y', Items.brick);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.kebabBlock, 1, 0), "XXX", "XXX", "XXX", 'X', TESItems.kebab);
		GameRegistry.addRecipe(new ItemStack(TESItems.leatherHat), " X ", "XXX", 'X', Items.leather);
		GameRegistry.addRecipe(new ItemStack(TESItems.lemonCake), "AAA", "BCB", "DDD", 'A', Items.milk_bucket, 'B', TESItems.lemon, 'C', Items.sugar, 'D', Items.wheat);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.marzipanBlock), "XXX", 'X', TESItems.marzipan);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.mud, 4, 1), "XY", "YX", 'X', new ItemStack(TESBlocks.mud, 1, 0), 'Y', Blocks.gravel);
		GameRegistry.addRecipe(new ItemStack(TESItems.oliveBread), "XYX", 'X', Items.wheat, 'Y', TESItems.olive);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.oven), "XXX", "X X", "XXX", 'X', Blocks.brick_block);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pillar1, 3, 3), "X", "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pillar1, 3, 4), "X", "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pillar2, 3, 1), "X", "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pillar2, 3, 2), "X", "X", "X", 'X', new ItemStack(Blocks.stone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pillar2, 3, 3), "X", "X", "X", 'X', Blocks.brick_block);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateAndesite), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateBasalt), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateChalk), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateDiorite), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateGranite), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateRhyolite), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.pressurePlateLabradorite), "XX", 'X', new ItemStack(TESBlocks.rock, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.redSandstone, 1, 0), "XX", "XX", 'X', new ItemStack(Blocks.sand, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.rope, 3), "X", "X", "X", 'X', Items.string);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.scorchedSlabSingle, 6, 0), "XXX", 'X', TESBlocks.scorchedStone);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.scorchedWall, 6), "XXX", "XXX", 'X', TESBlocks.scorchedStone);
		GameRegistry.addRecipe(new ItemStack(TESItems.silverIngot), "XXX", "XXX", "XXX", 'X', TESItems.silverNugget);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabClayTileSingle, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.clayTile, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle10, 6, 6), "XXX", 'X', new ItemStack(TESBlocks.whiteSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle11, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle11, 6, 6), "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle11, 6, 7), "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle3, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.smoothStone, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle3, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle3, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.pillar1, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle3, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.smoothStone, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle3, 6, 6), "XXX", 'X', new ItemStack(TESBlocks.brick2, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle3, 6, 7), "XXX", 'X', new ItemStack(TESBlocks.pillar1, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle7, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.redSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle8, 6, 7), "XXX", 'X', new ItemStack(TESBlocks.smoothStone, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle9, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.brick4, 1, 15));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle9, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.pillar2, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle9, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.pillar2, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle9, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.pillar2, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingle9, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.brick5, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleDirt, 6, 0), "XXX", 'X', new ItemStack(Blocks.dirt, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleDirt, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.dirtPath, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleDirt, 6, 4), "XXX", 'X', new ItemStack(TESBlocks.dirtPath, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleDirt, 6, 5), "XXX", 'X', new ItemStack(TESBlocks.dirtPath, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleDirt, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.mud, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleDirt, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.asshaiDirt, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleGravel, 6, 0), "XXX", 'X', new ItemStack(Blocks.gravel, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleGravel, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.basaltGravel, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleGravel, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.obsidianGravel, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleSand, 6, 0), "XXX", 'X', new ItemStack(Blocks.sand, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleSand, 6, 1), "XXX", 'X', new ItemStack(Blocks.sand, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleSand, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.whiteSand, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleThatch, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.thatch, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleThatch, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.thatch, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleV, 6, 0), "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleV, 6, 1), "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleV, 6, 2), "XXX", 'X', new ItemStack(TESBlocks.redBrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleV, 6, 3), "XXX", 'X', new ItemStack(TESBlocks.redBrick, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.slabSingleV, 6, 4), "XXX", 'X', Blocks.mossy_cobblestone);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.smoothStone, 2, 3), "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.smoothStone, 2, 4), "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.smoothStone, 2, 5), "X", "X", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsAlmond, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 15));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsApple, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsAramant, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 8));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsAspen, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 12));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsBanana, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 11));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsBaobab, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsBeech, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 9));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsBrickCracked, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.redBrick, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsBrickMossy, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.redBrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsCedar, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsChalk, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsChalkBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick4, 1, 15));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsCharred, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsCherry, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsChestnut, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTile, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTile, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedBlack, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 15));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedBlue, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 11));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedBrown, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 12));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedCyan, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 9));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedGray, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 7));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedGreen, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 13));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedLightBlue, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedLightGray, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 8));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedLime, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedMagenta, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedOrange, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedPink, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedPurple, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 10));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedRed, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedWhite, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsClayTileDyedYellow, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsCobblestoneMossy, 4), "X  ", "XX ", "XXX", 'X', Blocks.mossy_cobblestone);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsCypress, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 10));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsDatePalm, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsDiorite, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsDioriteBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsDragon, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsFir, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsFotinia, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsGranite, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsGraniteBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick2, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsGreenOak, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 13));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsHolly, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 10));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsKanuka, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsLarch, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 13));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsLemon, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsLime, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 7));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsMahogany, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 8));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsMango, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 7));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsMangrove, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 15));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsMaple, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 12));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsMudBrick, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.brick5, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsOlive, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 11));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsOrange, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsPalm, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 3));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsPear, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsPine, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsPlum, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsPomegranate, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsRedSandstone, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.redSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsRedwood, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsReed, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.thatch, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsIbbinia, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsRotten, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planksRotten, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsScorchedStone, 4), "X  ", "XX ", "XXX", 'X', TESBlocks.scorchedStone);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsStone, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(Blocks.stone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsStoneBrickCracked, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsStoneBrickMossy, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsThatch, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.thatch, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsUlthos, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsWeirwood, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 6));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsWhiteSandstone, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.whiteSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.stairsWillow, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 9));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.thatch, 4, 1), "XX", "XX", 'X', TESBlocks.driedReeds);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.thatch, 6, 0), "XYX", "YXY", "XYX", 'X', Items.wheat, 'Y', Blocks.dirt);
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone1, 6, 13), "XXX", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 3));		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone1, 6, 14), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick1, 1, 14));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone2, 6, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone2, 6, 3), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick2, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone3, 6, 14), "XXX", "XXX", 'X', new ItemStack(TESBlocks.whiteSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone3, 6, 6), "XXX", "XXX", 'X', new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone3, 6, 7), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick4, 1, 15));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStone3, 6, 8), "XXX", "XXX", 'X', new ItemStack(TESBlocks.brick5, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallClayTile, 6, 0), "XXX", "XXX", 'X', new ItemStack(TESBlocks.clayTile, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 0), "XXX", "XXX", 'X', new ItemStack(Blocks.stone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 1), "XXX", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 3), "XXX", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 2));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 4), "XXX", "XXX", 'X', new ItemStack(Blocks.sandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 5), "XXX", "XXX", 'X', new ItemStack(TESBlocks.redSandstone, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 6), "XXX", "XXX", 'X', new ItemStack(Blocks.brick_block, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 7), "XXX", "XXX", 'X', new ItemStack(TESBlocks.redBrick, 1, 0));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.wallStoneV, 6, 8), "XXX", "XXX", 'X', new ItemStack(TESBlocks.redBrick, 1, 1));
		GameRegistry.addRecipe(new ItemStack(TESBlocks.whiteSandstone, 1, 0), "XX", "XX", 'X', new ItemStack(TESBlocks.whiteSand, 1, 0));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence_gate, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.stone_slab, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.smoothStoneV, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.aleHorn), "X", "Y", 'X', "horn", 'Y', "IngotTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.aleHornGold), "X", "Y", 'X', "horn", 'Y', "IngotGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.appleCrumble), "AAA", "BCB", "DDD", 'A', Items.milk_bucket, 'B', "apple", 'C', Items.sugar, 'D', Items.wheat));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.armorStand), " X ", " X ", "YYY", 'X', "stickWood", 'Y', Blocks.stone));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.barrel), "XXX", "YZY", "XXX", 'X', "plankWood", 'Y', "IngotIron", 'Z', Items.bucket));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.berryPie), "AAA", "BBB", "CCC", 'A', Items.milk_bucket, 'B', "berry", 'C', Items.wheat));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.birdCage, 1, 0), "YYY", "Y Y", "XXX", 'X', "IngotBronze", 'Y', TESBlocks.bronzeBars));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.birdCage, 1, 1), "YYY", "Y Y", "XXX", 'X', "IngotIron", 'Y', Blocks.iron_bars));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.birdCage, 1, 2), "YYY", "Y Y", "XXX", 'X', "IngotSilver", 'Y', TESBlocks.silverBars));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.birdCage, 1, 3), "YYY", "Y Y", "XXX", 'X', "IngotGold", 'Y', TESBlocks.goldBars));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.birdCageWood, 1, 0), "YYY", "Y Y", "XXX", 'X', "plankWood", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.bomb, 4), "XYX", "YXY", "XYX", 'X', Items.gunpowder, 'Y', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.boneBlock, 1, 0), "XX", "XX", 'X', "bone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.boneBoots), "X X", "X X", 'X', "bone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.boneChestplate), "X X", "XXX", "XXX", 'X', "bone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.boneHelmet), "XXX", "X X", 'X', "bone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.boneLeggings), "XXX", "X X", "X X", 'X', "bone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.brandingIron), "  X", " Y ", "X  ", 'X', "IngotIron", 'Y', TESItems.gemsbokHide));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.brandingIron), "  X", " Y ", "X  ", 'X', "IngotIron", 'Y', Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeAxe), "XX", "XY", " Y", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.bronzeBars, 16), "XXX", "XXX", 'X', "IngotBronze"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeBattleaxe), "XXX", "XYX", " Y ", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeCrossbow), "XXY", "ZYX", "YZX", 'X', TESItems.bronzeIngot, 'Y', "stickWood", 'Z', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeDagger), "X", "Y", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeHoe), "XX", " Y", " Y", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzePickaxe), "XXX", " Y ", " Y ", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeShovel), "X", "Y", "Y", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeSpear), "  X", " Y ", "Y  ", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeSword), "X", "X", "Y", 'X', TESItems.bronzeIngot, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeThrowingAxe), " X ", " YX", "Y  ", 'X', "IngotBronze", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.butterflyJar), "X", "Y", 'X', "plankWood", 'Y', Blocks.glass));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.cargocart), "X X", "X X", "YZY", 'X', Blocks.fence, 'Y', TESItems.wheel, 'Z', Blocks.planks));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.chain, 8), "X", "X", "X", 'X', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.anonymousMask), "XXX", "X X", 'X', Items.paper));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.chandelier, 2, 0), " X ", "YZY", 'X', "stickWood", 'Y', Blocks.torch, 'Z', "IngotBronze"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.chandelier, 2, 1), " X ", "YZY", 'X', "stickWood", 'Y', Blocks.torch, 'Z', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.chandelier, 2, 2), " X ", "YZY", 'X', "stickWood", 'Y', Blocks.torch, 'Z', "IngotSilver"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.chandelier, 2, 3), " X ", "YZY", 'X', "stickWood", 'Y', Blocks.torch, 'Z', "IngotGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.chisel), "XY", 'X', "IngotIron", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.clayMug, 2), "X", "Y", "X", 'X', "IngotTin", 'Y', "clayBall"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.clayPlate, 2), "XX", 'X', "clayBall"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.cobblebrick, 4, 0), "XX", "XX", 'X', Blocks.cobblestone));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.commandHorn), "XYX", 'X', "IngotBronze", 'Y', "horn"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.commandSword), "X", "Y", "Z", 'X', "IngotIron", 'Y', "IngotBronze", 'Z', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.commandTable), "XXX", "YYY", "ZZZ", 'X', Items.paper, 'Y', "plankWood", 'Z', "IngotBronze"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.crossbowBolt, 4), "X", "Y", "Z", 'X', "IngotBronze", 'Y', "stickWood", 'Z', "feather"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.crossbowBolt, 4), "X", "Y", "Z", 'X', "IngotIron", 'Y', "stickWood", 'Z', "feather"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.daub, 4), "XYX", "YXY", "XYX", 'X', "stickWood", 'Y', Blocks.dirt));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.daub, 4), "XYX", "YXY", "XYX", 'X', TESBlocks.driedReeds, 'Y', Blocks.dirt));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorAcacia), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorAlmond), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorApple), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorAramant), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 8)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorAspen), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 12)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorBanana), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorBaobab), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorBeech), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 9)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorBirch), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorCedar), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorCharred), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorCherry), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorChestnut), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorCypress), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 10)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorDarkOak), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorDatePalm), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorDragon), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorFir), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorFotinia), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorGreenOak), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorHolly), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 10)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorJungle), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorKanuka), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorLarch), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorLemon), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorLime), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorMahogany), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 8)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorMango), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorMangrove), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorMaple), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 12)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorUlthos), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorOlive), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorOrange), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorPalm), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorPear), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorPine), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorPlum), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorPomegranate), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorRedwood), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorIbbinia), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorRotten), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planksRotten, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorSpruce), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorWeirwood), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks3, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorWillow), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks2, 1, 9)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateAcacia, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateAlmond, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateApple, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateAramant, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 8)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateAspen, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 12)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateBanana, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateBaobab, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateBeech, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 9)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateBirch, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateCedar, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateCharred, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateCherry, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateChestnut, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateCypress, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 10)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateDarkOak, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateDatePalm, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateDragon, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateFir, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateFotinia, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateGreenOak, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateHolly, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 10)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateJungle, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateKanuka, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateLarch, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateLemon, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateLime, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateMahogany, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 8)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateMango, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateMangrove, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateMaple, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 12)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateUlthos, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateOlive, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateOrange, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGatePalm, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGatePear, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGatePine, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGatePlum, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGatePomegranate, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateRedwood, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateIbbinia, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateRotten, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planksRotten, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateSpruce, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateWeirwood, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks3, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateWillow, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks2, 1, 9)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.flintDagger), "X", "Y", 'X', Items.flint, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.flintSpear), "  X", " Y ", "Y  ", 'X', Items.flint, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.furBed), "XXX", "YYY", 'X', TESItems.fur, 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.fuse, 2), "X", "Y", "Y", 'X', new ItemStack(Items.coal, 1, 32767), 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.gateBronzeBars, 4), "YYY", "YXY", "YYY", 'X', TESItems.gateGear, 'Y', "IngotBronze"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.gateGear, 4), " X ", "XYX", " X ", 'X', "IngotIron", 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.gateGold, 4), "YYY", "YXY", "YYY", 'X', TESItems.gateGear, 'Y', "IngotGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.gateIronBars, 4), "YYY", "YXY", "YYY", 'X', TESItems.gateGear, 'Y', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.gateSilver, 4), "YYY", "YXY", "YYY", 'X', TESItems.gateGear, 'Y', "IngotSilver"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.gateWooden, 4), "ZYZ", "YXY", "ZYZ", 'X', TESItems.gateGear, 'Y', "plankWood", 'Z', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.gateWoodenCross, 4), "YYY", "YXY", "YYY", 'X', TESItems.gateGear, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.gobletCopper, 2), "X X", " X ", " X ", 'X', "IngotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.gobletGold, 2), "X X", " X ", " X ", 'X', "IngotGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.gobletSilver, 2), "X X", " X ", " X ", 'X', "IngotSilver"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.gobletWood, 2), "X X", " X ", " X ", 'X', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.goldBars, 16), "XXX", "XXX", 'X', "IngotGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.goldHorseArmor), "X  ", "XYX", "XXX", 'X', "IngotGold", 'Y', Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.goldRing), "XXX", "X X", "XXX", 'X', "nuggetGold"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.grapevine), "X", "X", "X", 'X', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironBattleaxe), "XXX", "XYX", " Y ", 'X', "IngotIron", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironCrossbow), "XXY", "ZYX", "YZX", 'X', "IngotIron", 'Y', "stickWood", 'Z', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironDagger), "X", "Y", 'X', "IngotIron", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironHorseArmor), "X  ", "XYX", "XXX", 'X', "IngotIron", 'Y', Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironPike), "  X", " YX", "Y  ", 'X', "IngotIron", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironSpear), "  X", " Y ", "Y  ", 'X', "IngotIron", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.ironThrowingAxe), " X ", " YX", "Y  ", 'X', "IngotIron", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.lionBed), "XXX", "YYY", 'X', TESItems.lionFur, 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.millstone), "XYX", "XZX", "XXX", 'X', Blocks.cobblestone, 'Y', "IngotBronze", 'Z', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.millstone), "XYX", "XZX", "XXX", 'X', Blocks.cobblestone, 'Y', "IngotIron", 'Z', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.mug, 2), "X", "Y", "X", 'X', "IngotTin", 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.oreGlowstone), "XXX", "XYX", "XXX", 'X', Items.glowstone_dust, 'Y', new ItemStack(Blocks.stone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.pipe), "X  ", " XX", 'X', Items.clay_ball));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.plowcart), "fff", "php", "wpw", 'f', Blocks.fence, 'h', Items.iron_hoe, 'p', Blocks.planks, 'w', TESItems.wheel));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.pouch, 1, 0), "X X", "X X", "XXX", 'X', Items.leather));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.redClay), "XX", "XX", 'X', TESItems.redClayBall));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.reedBars, 16), "XXX", "XXX", 'X', new ItemStack(TESBlocks.thatch, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.rhinoArmor), "X  ", "XYX", "XXX", 'X', Items.leather, 'Y', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.rollingPin), "XYX", 'X', "stickWood", 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.silverBars, 16), "XXX", "XXX", 'X', "IngotSilver"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.silverRing), "XXX", "X X", "XXX", 'X', TESItems.silverNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.bronzeRing), "XXX", "X X", "XXX", 'X', TESItems.bronzeNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.copperRing), "XXX", "X X", "XXX", 'X', TESItems.copperNugget));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.skullCup), "X", "Y", 'X', new ItemStack(Items.skull, 1, 0), 'Y', "IngotTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.skullStaff), "X", "Y", 'X', Items.skull, 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabBoneSingle, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.boneBlock, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.slabSingleV, 6, 5), "XXX", 'X', new ItemStack(Blocks.stone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.sling), "XYX", "XZX", " X ", 'X', "stickWood", 'Y', Items.leather, 'Z', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.smoothStoneV, 2, 0), "X", "X", 'X', new ItemStack(Blocks.stone, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsBone, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.boneBlock, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.stoneSpear), "  X", " Y ", "Y  ", 'X', "cobblestone", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.strawBed), "XXX", "YYY", 'X', Items.wheat, 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.sulfurMatch, 4), "X", "Y", 'X', "sulfur", 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.termiteMound, 1, 1), " X ", "XYX", " X ", 'X', TESItems.termite, 'Y', Blocks.stone));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.thatchFloor, 3), "XX", 'X', new ItemStack(TESBlocks.thatch, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorAlmond, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorApple, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorAramant, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 8)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorAspen, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 12)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorBanana, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorBaobab, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorBeech, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 9)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorBirch, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.planks, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorCedar, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorCharred, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorCherry, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorChestnut, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorCypress, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 10)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorDarkOak, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.planks, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorDatePalm, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorDragon, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorFir, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorFotinia, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 14)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorGreenOak, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorHolly, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 10)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorJungle, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.planks, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorKanuka, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorLarch, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 13)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorLemon, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorLime, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorMahogany, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 8)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorMango, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 7)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorMangrove, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 15)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorMaple, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 12)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorUlthos, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorOlive, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 11)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorOrange, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorPalm, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 3)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorPear, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 5)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorPine, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 4)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorPlum, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorPomegranate, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 2)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorRedwood, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorIbbinia, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorRotten, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planksRotten, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorSpruce, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.planks, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorWeirwood, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks3, 1, 6)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorWillow, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks2, 1, 9)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.unsmeltery), "X X", "YXY", "ZZZ", 'X', "IngotIron", 'Y', "stickWood", 'Z', Blocks.cobblestone));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wallBone, 6, 0), "XXX", "XXX", 'X', new ItemStack(TESBlocks.boneBlock, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.wasteBlock, 4), "XY", "YZ", 'X', Items.rotten_flesh, 'Y', Blocks.dirt, 'Z', "bone"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.waterskin, 2), " Y ", "X X", " X ", 'X', TESItems.fur, 'Y', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.waterskin, 2), " Y ", "X X", " X ", 'X', TESItems.gemsbokHide, 'Y', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.waterskin, 2), " Y ", "X X", " X ", 'X', TESItems.lionFur, 'Y', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.waterskin, 2), " Y ", "X X", " X ", 'X', Items.leather, 'Y', Items.string));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.weaponRack), "X X", "YYY", 'X', "stickWood", 'Y', "plankWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.wheel), "sss", "sps", "sss", 's', "stickWood", 'p', Blocks.planks));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.wineGlass, 2), "X X", " X ", " X ", 'X', Blocks.glass));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.arrow, 4), "X", "Y", "Z", 'X', "arrowTip", 'Y', "stickWood", 'Z', "feather"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.arrowFire, 4), " X ", "XYX", " X ", 'X', Items.arrow, 'Y', TESItems.sulfur));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.arrowPoisoned, 4), " X ", "XYX", " X ", 'X', Items.arrow, 'Y', "poison"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESItems.crossbowBoltPoisoned, 4), " X ", "XYX", " X ", 'X', TESItems.crossbowBolt, 'Y', "poison"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.brewing_stand, 1), " X ", "YYY", 'X', "stickWood", 'Y', Blocks.cobblestone));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.clock), " X ", "XYX", " X ", 'X', "IngotGold", 'Y', "IngotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.compass), " X ", "XYX", " X ", 'X', "IngotIron", 'Y', "IngotCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.item_frame), "XXX", "XYX", "XXX", 'X', "stickWood", 'Y', TESItems.fur));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.item_frame), "XXX", "XYX", "XXX", 'X', "stickWood", 'Y', TESItems.gemsbokHide));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.paper, 3), "XXX", 'X', TESBlocks.cornStalk));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.paper, 3), "XXX", 'X', TESBlocks.reeds));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.saddle), "XXX", "Y Y", 'X', TESItems.fur, 'Y', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.saddle), "XXX", "Y Y", 'X', TESItems.gemsbokHide, 'Y', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.saddle), "XXX", "Y Y", 'X', Items.leather, 'Y', "IngotIron"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.wooden_door), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stairsCatalpa, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodSlabSingle1, 6, 1), "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam1, 3, 1), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fence, 3, 1), "XYX", "XYX", 'X', new ItemStack(TESBlocks.planks1, 1, 1), 'Y', "stickWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceGateCatalpa, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(TESBlocks.planks1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.doorCatalpa), "XX", "XX", "XX", 'X', new ItemStack(TESBlocks.planks1, 1, 1)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorCatalpa, 2), "XXX", "XXX", 'X', new ItemStack(TESBlocks.planks1, 1, 1)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.brickIce, 1), "XX", "XX", 'X', new ItemStack(Blocks.packed_ice, 1)));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.dirt, 1, 0), new ItemStack(Blocks.dirt, 1, 1), Items.wheat_seeds));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.mossy_cobblestone, 1, 0), new ItemStack(Blocks.cobblestone, 1, 0), "vine"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Blocks.stonebrick, 1, 1), new ItemStack(Blocks.stonebrick, 1, 0), "vine"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.planks1, 4, 1), new ItemStack(TESBlocks.wood1, 1, 1)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESItems.brandingIron), new ItemStack(TESItems.brandingIron, 1, 32767), "IngotIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESItems.dye, 2, 5), "dyeOrange", "dyeBlack"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESItems.dye, 3, 5), "dyeRed", "dyeYellow", "dyeBlack"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESItems.gammon), Items.cooked_porkchop, "salt"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.mud, 1, 0), new ItemStack(TESBlocks.mud, 1, 1), Items.wheat_seeds));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESItems.questBook), Items.book, new ItemStack(Items.dye, 1, 1), "nuggetGold"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.redBrick, 1, 0), new ItemStack(Blocks.brick_block, 1, 0), "vine"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESItems.saltedFlesh), Items.rotten_flesh, "salt"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(Items.gunpowder, 2), TESItems.termite));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.bomb, 1, 1), new ItemStack(TESBlocks.bomb, 1, 0), Items.gunpowder, "IngotIron"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.bomb, 1, 2), new ItemStack(TESBlocks.bomb, 1, 1), Items.gunpowder, "IngotIron"));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.alloySteelIngot, 9), new ItemStack(TESBlocks.blockMetal2, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.alloySteelNugget, 9), TESItems.alloySteelIngot);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.amber, 9), new ItemStack(TESBlocks.blockGem, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.amethyst, 9), new ItemStack(TESBlocks.blockGem, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.bottlePoison), Items.glass_bottle, TESItems.wildberry);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.bronzeIngot, 1), TESItems.copperIngot, TESItems.tinIngot);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.bronzeIngot, 9), new ItemStack(TESBlocks.blockMetal1, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonAndesite), new ItemStack(TESBlocks.rock, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonBasalt), new ItemStack(TESBlocks.rock, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonChalk), new ItemStack(TESBlocks.rock, 1, 5));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonDiorite), new ItemStack(TESBlocks.rock, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonGranite), new ItemStack(TESBlocks.rock, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonRhyolite), new ItemStack(TESBlocks.rock, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.buttonLabradorite), new ItemStack(TESBlocks.rock, 1, 6));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.copperIngot, 9), new ItemStack(TESBlocks.blockMetal1, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.copperNugget, 9), TESItems.copperIngot);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.coral, 4), new ItemStack(TESBlocks.blockGem, 1, 8));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.diamond, 9), new ItemStack(TESBlocks.blockGem, 1, 5));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 0), new ItemStack(TESBlocks.essosFlower, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 0), new ItemStack(TESBlocks.yitiFlower, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 1), new ItemStack(TESBlocks.yitiFlower, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 2), TESBlocks.bluebell);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 2), new ItemStack(TESBlocks.yitiFlower, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 3), new ItemStack(TESBlocks.clover, 1, 32767));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 1, 4), new ItemStack(Items.coal, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.dye, 2, 0), new ItemStack(TESBlocks.doubleFlower, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.emerald, 9), new ItemStack(TESBlocks.blockGem, 1, 9));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.flaxSeeds, 2), TESBlocks.flaxPlant);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.kebab, 9), new ItemStack(TESBlocks.kebabBlock, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.leekSoup), Items.bowl, TESItems.leek, TESItems.leek, Items.potato);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.mapleSyrup), new ItemStack(TESBlocks.wood3, 1, 0), Items.bowl);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.marzipan), TESItems.almond, TESItems.almond, Items.sugar);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.marzipanChocolate), TESItems.marzipan, new ItemStack(Items.dye, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.melonSoup), Items.bowl, Items.melon, Items.melon, Items.sugar);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.mushroomPie), Items.egg, Blocks.red_mushroom, Blocks.brown_mushroom);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.obsidianShard, 9), Blocks.obsidian);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.opal, 9), new ItemStack(TESBlocks.blockGem, 1, 7));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.pearl, 9), new ItemStack(TESBlocks.blockGem, 1, 6));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.pebble, 4), Blocks.gravel);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.pipeweedSeeds), TESItems.pipeweedLeaf);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.pipeweedSeeds, 2), TESBlocks.pipeweedPlant);
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 0), new ItemStack(TESBlocks.wood1, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 10), new ItemStack(TESBlocks.wood2, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 11), new ItemStack(TESBlocks.wood2, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 12), new ItemStack(TESBlocks.wood3, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 13), new ItemStack(TESBlocks.wood3, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 14), new ItemStack(TESBlocks.wood3, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 15), new ItemStack(TESBlocks.wood3, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 2), new ItemStack(TESBlocks.wood1, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 3), new ItemStack(TESBlocks.wood1, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 4), new ItemStack(TESBlocks.fruitWood, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 5), new ItemStack(TESBlocks.fruitWood, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 6), new ItemStack(TESBlocks.fruitWood, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 7), new ItemStack(TESBlocks.fruitWood, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 8), new ItemStack(TESBlocks.wood2, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks1, 4, 9), new ItemStack(TESBlocks.wood2, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 0), new ItemStack(TESBlocks.wood4, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 1), new ItemStack(TESBlocks.wood4, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 10), new ItemStack(TESBlocks.wood6, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 11), new ItemStack(TESBlocks.wood6, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 12), new ItemStack(TESBlocks.wood7, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 13), new ItemStack(TESBlocks.wood7, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 14), new ItemStack(TESBlocks.wood7, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 15), new ItemStack(TESBlocks.wood7, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 2), new ItemStack(TESBlocks.wood4, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 3), new ItemStack(TESBlocks.wood4, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 4), new ItemStack(TESBlocks.wood5, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 5), new ItemStack(TESBlocks.wood5, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 6), new ItemStack(TESBlocks.wood5, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 7), new ItemStack(TESBlocks.wood5, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 8), new ItemStack(TESBlocks.wood6, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks2, 4, 9), new ItemStack(TESBlocks.wood6, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 0), new ItemStack(TESBlocks.wood8, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 1), new ItemStack(TESBlocks.wood8, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 2), new ItemStack(TESBlocks.wood8, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 3), new ItemStack(TESBlocks.wood8, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 4), new ItemStack(TESBlocks.wood9, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 5), new ItemStack(TESBlocks.wood9, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planks3, 4, 6), new ItemStack(TESBlocks.wood9, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESBlocks.planksRotten, 4, 0), new ItemStack(TESBlocks.rottenLog, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.rabbitStew), Items.bowl, TESItems.rabbitCooked, Items.potato, Items.potato);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.ruby, 9), new ItemStack(TESBlocks.blockGem, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.salt, 9), new ItemStack(TESBlocks.blockMetal2, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.saltpeter, 9), new ItemStack(TESBlocks.blockMetal1, 1, 14));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.sapphire, 9), new ItemStack(TESBlocks.blockGem, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.seedsGrapeRed), TESItems.grapeRed);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.seedsGrapeWhite), TESItems.grapeWhite);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.silverIngot, 9), new ItemStack(TESBlocks.blockMetal1, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.silverNugget, 9), TESItems.silverIngot);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.sulfur, 9), new ItemStack(TESBlocks.blockMetal1, 1, 13));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.tinIngot, 9), new ItemStack(TESBlocks.blockMetal1, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.topaz, 9), new ItemStack(TESBlocks.blockGem, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.bronzeNugget, 9), TESItems.bronzeIngot);
		GameRegistry.addShapelessRecipe(new ItemStack(TESItems.ironNugget, 9), Items.iron_ingot);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, 1), new ItemStack(TESBlocks.essosFlower, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, 13), new ItemStack(TESBlocks.essosFlower, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, 14), TESBlocks.marigold);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, 14), new ItemStack(TESBlocks.yitiFlower, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, 5), new ItemStack(TESBlocks.essosFlower, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 1, 9), new ItemStack(TESBlocks.yitiFlower, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 1), new ItemStack(TESBlocks.doubleFlower, 1, 3));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 13), new ItemStack(TESBlocks.doubleFlower, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 15), TESItems.saltpeter, Blocks.dirt);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 2, 5), new ItemStack(TESBlocks.doubleFlower, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 4, 1), new ItemStack(TESBlocks.wood9, 1, 0), new ItemStack(TESBlocks.wood9, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.dye, 8, 15), TESBlocks.boneBlock);
		GameRegistry.addShapelessRecipe(new ItemStack(Items.gunpowder, 2), TESItems.sulfur, TESItems.saltpeter, new ItemStack(Items.coal, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(Items.string), TESItems.flax);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.trapdoorAcacia, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.planks, 1, 4)));
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugAppleJuice), new Object[]{"apple", "apple"});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugBlackberryJuice), new Object[]{TESItems.blackberry, TESItems.blackberry, TESItems.blackberry});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugBlueberryJuice), new Object[]{TESItems.blueberry, TESItems.blueberry, TESItems.blueberry});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugChocolate), TESItems.mugMilk, new Object[]{Items.sugar, new ItemStack(Items.dye, 1, 3)});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugCranberryJuice), new Object[]{TESItems.cranberry, TESItems.cranberry, TESItems.cranberry});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugElderberryJuice), new Object[]{TESItems.elderberry, TESItems.elderberry, TESItems.elderberry});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugLemonade), TESItems.mugWater, new Object[]{TESItems.lemon, Items.sugar});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugMangoJuice), new Object[]{TESItems.mango, TESItems.mango});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugOrangeJuice), new Object[]{TESItems.orange, TESItems.orange});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugPomegranateJuice), new Object[]{TESItems.pomegranate, TESItems.pomegranate});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugRaspberryJuice), new Object[]{TESItems.raspberry, TESItems.raspberry, TESItems.raspberry});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugRedGrapeJuice), new Object[]{TESItems.grapeRed, TESItems.grapeRed, TESItems.grapeRed});
		TESRecipeVessels.addRecipes(new ItemStack(TESItems.mugWhiteGrapeJuice), new Object[]{TESItems.grapeWhite, TESItems.grapeWhite, TESItems.grapeWhite});
		TESBlockTreasurePile.generateTreasureRecipes(TESBlocks.treasureCopper, TESItems.copperIngot);
		TESBlockTreasurePile.generateTreasureRecipes(TESBlocks.treasureGold, Items.gold_ingot);
		TESBlockTreasurePile.generateTreasureRecipes(TESBlocks.treasureSilver, TESItems.silverIngot);
		GameRegistry.addRecipe(new ShapelessOreRecipe(TESItems.bronzeDaggerPoisoned, TESItems.bronzeDagger, TESItems.bottlePoison));
		GameRegistry.addRecipe(new ShapelessOreRecipe(TESItems.bronzeDaggerPoisoned, TESItems.bronzeDagger, TESItems.bottlePoison));
		GameRegistry.addRecipe(new ShapelessOreRecipe(TESItems.ironDaggerPoisoned, TESItems.ironDagger, TESItems.bottlePoison));
		GameRegistry.addRecipe(new ShapelessOreRecipe(TESItems.alloySteelDaggerPoisoned, TESItems.alloySteelDagger, TESItems.bottlePoison));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeamRotten, 3, 0), "X", "X", "X", 'X', new ItemStack(TESBlocks.rottenLog, 1, 0)));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fenceRotten, 3, 0), "XYX", "XYX", 'X', new ItemStack(TESBlocks.planksRotten, 1, 0), 'Y', "stickWood"));
		GameRegistry.addRecipe(new TESRecipeBanners());
		GameRegistry.addRecipe(new TESRecipeFeatherDye());
		GameRegistry.addRecipe(new TESRecipeLeatherHatDye());
		GameRegistry.addRecipe(new TESRecipeLeatherHatFeather());
		GameRegistry.addRecipe(new TESRecipePipe());
		GameRegistry.addRecipe(new TESRecipePoisonDrinks());
		GameRegistry.addRecipe(new TESRecipePouch());
		for (TESEnumDyeColor dye : TESEnumDyeColor.values()) {
			GameRegistry.addShapelessRecipe(new ItemStack(getPowderFromDye(dye), 8), Blocks.sand, Blocks.sand, Blocks.sand, Blocks.sand, Blocks.gravel, Blocks.gravel, Blocks.gravel, Blocks.gravel, new ItemStack(Items.dye, 1, dye.getDyeDamage()));
		}
		for (i = 0; i <= 2; ++i) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam9, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood9, 1, i)));
		}
		for (i = 0; i <= 1; ++i) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeamV2, 3, i), "X", "X", "X", 'X', new ItemStack(Blocks.log2, 1, i)));
		}
		for (i = 0; i <= 2; ++i) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(TESBlocks.bomb, 1, i + 8), new ItemStack(TESBlocks.bomb, 1, i), Items.lava_bucket));
		}
		for (i = 0; i <= 3; ++i) {
			if (i != 1) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam1, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood1, 1, i)));
			}
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam8, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood8, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam7, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood7, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam2, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood2, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeamFruit, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.fruitWood, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam3, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood3, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam4, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood4, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam5, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood5, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeam6, 3, i), "X", "X", "X", 'X', new ItemStack(TESBlocks.wood6, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.woodBeamV1, 3, i), "X", "X", "X", 'X', new ItemStack(Blocks.log, 1, i)));
		}
		for (i = 0; i <= 5; ++i) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence, 3, i), "XYX", "XYX", 'X', new ItemStack(Blocks.planks, 1, i), 'Y', "stickWood"));
		}
		for (i = 0; i <= 6; ++i) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fence3, 3, i), "XYX", "XYX", 'X', new ItemStack(TESBlocks.planks3, 1, i), 'Y', "stickWood"));
		}
		for (i = 0; i <= 7; ++i) {
			GameRegistry.addRecipe(new ItemStack(TESBlocks.slabClayTileDyedSingle1, 6, i), "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, i));
			GameRegistry.addRecipe(new ItemStack(TESBlocks.slabClayTileDyedSingle2, 6, i), "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, i + 8));
		}
		for (i = 0; i <= 15; ++i) {
			if (i != 1) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fence, 3, i), "XYX", "XYX", 'X', new ItemStack(TESBlocks.planks1, 1, i), 'Y', "stickWood"));
			}
			GameRegistry.addRecipe(new ItemStack(TESBlocks.wallClayTileDyed, 6, i), "XXX", "XXX", 'X', new ItemStack(TESBlocks.clayTileDyed, 1, i));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.clayTileDyed, 8, i), "XXX", "XYX", "XXX", 'X', new ItemStack(TESBlocks.clayTile, 1, 0), 'Y', DYE_ORE_NAMES[BlockColored.func_150032_b(i)]));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.clayTileDyed, 4, i), "XX", "XX", 'X', new ItemStack(Blocks.stained_hardened_clay, 1, i)));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.fence2, 3, i), "XYX", "XYX", 'X', new ItemStack(TESBlocks.planks2, 1, i), 'Y', "stickWood"));
			GameRegistry.addRecipe(new ItemStack(TESBlocks.stainedGlass, 4, i), "XX", "XX", 'X', new ItemStack(Blocks.stained_glass, 1, i));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stainedGlass, 8, i), "XXX", "XYX", "XXX", 'X', TESBlocks.glass, 'Y', DYE_ORE_NAMES[BlockColored.func_150031_c(i)]));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(TESBlocks.stainedGlassPane, 16, i), "XXX", "XXX", 'X', new ItemStack(TESBlocks.stainedGlass, 1, i)));
		}
		for (TESBlockFallenLeaves fallenLeafBlock : TESBlockFallenLeaves.ALL_FALLEN_LEAVES) {
			for (Block leafBlock : fallenLeafBlock.getLeafBlocks()) {
				if (!(leafBlock instanceof TESBlockLeavesBase)) {
					continue;
				}
				String[] leafNames = ((TESBlockLeavesBase) leafBlock).getAllLeafNames();
				for (int leafMeta = 0; leafMeta < leafNames.length; ++leafMeta) {
					Object[] fallenBlockMeta = TESBlockFallenLeaves.fallenBlockMetaFromLeafBlockMeta(leafBlock, leafMeta);
					if (fallenBlockMeta == null) {
						continue;
					}
					Block fallenBlock = (Block) fallenBlockMeta[0];
					int fallenMeta = (Integer) fallenBlockMeta[1];
					if (fallenBlock == null) {
						continue;
					}
					GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(fallenBlock, 6, fallenMeta), "XXX", 'X', new ItemStack(leafBlock, 1, leafMeta)));
				}
			}
		}
	}
	
	public static ItemStack findMatchingRecipe(Iterable<IRecipe> recipeList, InventoryCrafting inv, World world) {
		for (IRecipe recipe : recipeList) {
			if (!recipe.matches(inv, world)) {
				continue;
			}
			return recipe.getCraftingResult(inv);
		}
		return null;
	}

	private static TESBlockConcretePowder getPowderFromDye(TESEnumDyeColor dye) {
		return TESBlocks.CONCRETE_POWDER.get(dye);
	}

	private static void modifyStandardRecipes() {
		List<IRecipe> recipeList = CraftingManager.getInstance().getRecipeList();
		removeRecipesItem(recipeList, Item.getItemFromBlock(Blocks.fence));
		removeRecipesItemStack(recipeList, new ItemStack(Blocks.sandstone, 1, 2));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.sandstone, 2, 2), "X", "X", 'X', new ItemStack(Blocks.sandstone, 1, 0)));

		for (int i = 0; i <= 5; ++i) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence, 3, i), "XYX", "XYX", 'X', new ItemStack(Blocks.planks, 1, i), 'Y', "stickWood"));
		}
		removeRecipesItem(recipeList, Item.getItemFromBlock(Blocks.fence_gate));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.fence_gate, 1), "XYX", "XYX", 'X', "stickWood", 'Y', new ItemStack(Blocks.planks, 1, 0)));
		removeRecipesItem(recipeList, Items.wooden_door);
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.wooden_door), "XX", "XX", "XX", 'X', new ItemStack(Blocks.planks, 1, 0)));
		removeRecipesItem(recipeList, Item.getItemFromBlock(Blocks.trapdoor));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.trapdoor, 2), "XXX", "XXX", 'X', new ItemStack(Blocks.planks, 1, 0)));
		if (TESConfig.removeGoldenAppleRecipes) {
			removeRecipesItem(recipeList, Items.golden_apple);
		}
		if (TESConfig.removeDiamondArmorRecipes) {
			removeRecipesItem(recipeList, Items.diamond_helmet);
			removeRecipesItem(recipeList, Items.diamond_chestplate);
			removeRecipesItem(recipeList, Items.diamond_leggings);
			removeRecipesItem(recipeList, Items.diamond_boots);
		}
		removeRecipesItemStack(recipeList, new ItemStack(Blocks.stone_slab, 1, 0));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.stone_slab, 6, 0), "XXX", 'X', new ItemStack(TESBlocks.smoothStoneV, 1, 0)));
		removeRecipesItemStack(recipeList, new ItemStack(Blocks.stone_slab, 1, 5));
		GameRegistry.addRecipe(new ItemStack(Blocks.stone_slab, 6, 5), "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 0));
		removeRecipesItem(recipeList, Item.getItemFromBlock(Blocks.stone_brick_stairs));
		GameRegistry.addRecipe(new ItemStack(Blocks.stone_brick_stairs, 4), "X  ", "XX ", "XXX", 'X', new ItemStack(Blocks.stonebrick, 1, 0));
		removeRecipesItem(recipeList, Item.getItemFromBlock(Blocks.anvil));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.anvil), "XXX", " Y ", "XXX", 'X', "IngotIron", 'Y', "blockIron"));
		removeRecipesClass(recipeList, RecipesArmorDyes.class);
		GameRegistry.addRecipe(new TESRecipeArmorDyes());
	}

	public static void onInit() {
		registerOres();
		RecipeSorter.register("tes:armorDyes", TESRecipeArmorDyes.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:pipe", TESRecipePipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:pouch", TESRecipePouch.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:leatherHatDye", TESRecipeLeatherHatDye.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:featherDye", TESRecipeFeatherDye.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:leatherHatFeather", TESRecipeLeatherHatFeather.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:robesDye", TESRecipeRobesDye.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:banners", TESRecipeBanners.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:poisonDrink", TESRecipePoisonDrinks.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("tes:treasurePile", TESRecipeTreasurePile.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		modifyStandardRecipes();
		createStandardRecipes();
		createEmpireRecipes();
		
		CraftingManager.getInstance().getRecipeList().addAll(0, SLAB);
		
	}

	private static void registerOres() {
		for (Object obj : Block.blockRegistry) {
			Block block = (Block) obj;
			if (block instanceof TESBlockPlanksBase) {
				OreDictionary.registerOre("plankWood", new ItemStack(block, 1, 32767));
			}
			if (block instanceof TESBlockSlabBase && block.getMaterial() == Material.wood) {
				OreDictionary.registerOre("slabWood", new ItemStack(block, 1, 32767));
			}
			if (block instanceof TESBlockStairs && block.getMaterial() == Material.wood) {
				OreDictionary.registerOre("stairWood", new ItemStack(block, 1, 32767));
			}
			if (block instanceof TESBlockWoodBase) {
				OreDictionary.registerOre("logWood", new ItemStack(block, 1, 32767));
			}
			if (!(block instanceof TESBlockSaplingBase)) {
				continue;
			}
			OreDictionary.registerOre("treeSapling", new ItemStack(block, 1, 32767));
		}
		for (Object obj : Item.itemRegistry) {
			Item item = (Item) obj;
			if (item == Items.bone) {
				OreDictionary.registerOre("bone", item);
			}
			if (!(item instanceof TESItemBerry)) {
				continue;
			}
			OreDictionary.registerOre("berry", item);
		}
		OreDictionary.registerOre("oreCopper", TESBlocks.oreCopper);
		OreDictionary.registerOre("oreTin", TESBlocks.oreTin);
		OreDictionary.registerOre("oreSilver", TESBlocks.oreSilver);
		OreDictionary.registerOre("oreCobalt", TESItems.cobaltBlue);
		OreDictionary.registerOre("oreSulfur", TESBlocks.oreSulfur);
		OreDictionary.registerOre("oreSaltpeter", TESBlocks.oreSaltpeter);
		OreDictionary.registerOre("oreSalt", TESBlocks.oreSalt);
		OreDictionary.registerOre("IngotCopper", TESItems.copperIngot);
		OreDictionary.registerOre("IngotTin", TESItems.tinIngot);
		OreDictionary.registerOre("IngotBronze", TESItems.bronzeIngot);
		OreDictionary.registerOre("IngotSilver", TESItems.silverIngot);
		OreDictionary.registerOre("nuggetSilver", TESItems.silverNugget);
		OreDictionary.registerOre("nuggetCopper", TESItems.copperNugget);
		OreDictionary.registerOre("nuggetAlloySteel", TESItems.alloySteelNugget);
		OreDictionary.registerOre("sulfur", TESItems.sulfur);
		OreDictionary.registerOre("saltpeter", TESItems.saltpeter);
		OreDictionary.registerOre("salt", TESItems.salt);
		OreDictionary.registerOre("clayBall", Items.clay_ball);
		OreDictionary.registerOre("clayBall", TESItems.redClayBall);
		OreDictionary.registerOre("dyeYellow", new ItemStack(TESItems.dye, 1, 0));
		OreDictionary.registerOre("dyeWhite", new ItemStack(TESItems.dye, 1, 1));
		OreDictionary.registerOre("dyeBlue", new ItemStack(TESItems.dye, 1, 2));
		OreDictionary.registerOre("dyeGreen", new ItemStack(TESItems.dye, 1, 3));
		OreDictionary.registerOre("dyeBlack", new ItemStack(TESItems.dye, 1, 4));
		OreDictionary.registerOre("dyeBrown", new ItemStack(TESItems.dye, 1, 5));
		OreDictionary.registerOre("sand", new ItemStack(TESBlocks.whiteSand, 1, 32767));
		OreDictionary.registerOre("sandstone", new ItemStack(TESBlocks.redSandstone, 1, 32767));
		OreDictionary.registerOre("sandstone", new ItemStack(TESBlocks.whiteSandstone, 1, 32767));
		OreDictionary.registerOre("apple", Items.apple);
		OreDictionary.registerOre("apple", TESItems.appleGreen);
		OreDictionary.registerOre("feather", Items.feather);
		OreDictionary.registerOre("feather", TESItems.swanFeather);
		OreDictionary.registerOre("horn", TESItems.rhinoHorn);
		OreDictionary.registerOre("horn", TESItems.horn);
		OreDictionary.registerOre("arrowTip", Items.flint);
		OreDictionary.registerOre("arrowTip", TESItems.rhinoHorn);
		OreDictionary.registerOre("arrowTip", TESItems.horn);
		OreDictionary.registerOre("poison", TESItems.bottlePoison);
		OreDictionary.registerOre("vine", Blocks.vine);
		OreDictionary.registerOre("vine", TESBlocks.willowVines);
		OreDictionary.registerOre("vine", TESBlocks.mirkVines);
	}

	private static void removeRecipesClass(Collection<IRecipe> recipeList, Class<? extends IRecipe> recipeClass) {
		Collection<IRecipe> recipesToRemove = new ArrayList<>();
		for (IRecipe recipe : recipeList) {
			if (!recipeClass.isAssignableFrom(recipe.getClass())) {
				continue;
			}
			recipesToRemove.add(recipe);
		}
		recipeList.removeAll(recipesToRemove);
	}

	private static void removeRecipesItem(Collection<IRecipe> recipeList, Item outputItem) {
		Collection<IRecipe> recipesToRemove = new ArrayList<>();
		for (IRecipe recipe : recipeList) {
			ItemStack output = recipe.getRecipeOutput();
			if (output == null || output.getItem() != outputItem) {
				continue;
			}
			recipesToRemove.add(recipe);
		}
		recipeList.removeAll(recipesToRemove);
	}

	private static void removeRecipesItemStack(Collection<IRecipe> recipeList, ItemStack outputItemStack) {
		Collection<IRecipe> recipesToRemove = new ArrayList<>();
		for (IRecipe recipe : recipeList) {
			ItemStack output = recipe.getRecipeOutput();
			if (output == null || !output.isItemEqual(outputItemStack)) {
				continue;
			}
			recipesToRemove.add(recipe);
		}
		recipeList.removeAll(recipesToRemove);
	}
}