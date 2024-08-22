package tes.common.database;

import cpw.mods.fml.common.FMLLog;
import tes.common.TESConfig;
import tes.common.TESLore;
import tes.common.enchant.TESEnchantmentHelper;
import tes.common.item.other.TESItemModifierTemplate;
import tes.common.item.other.TESItemMug;
import tes.common.item.other.TESItemPouch;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.ChestGenHooks;

import java.util.*;

public class TESChestContents {

	private final WeightedRandomChestContent[] items;
	private final int minItems;
	private final int maxItems;

	private boolean pouches;
	private TESItemMug.Vessel[] vesselTypes;
	private List<TESLore.LoreCategory> loreCategories = new ArrayList<>();
	private int loreChance = 10;

	@SuppressWarnings("WeakerAccess")
	public TESChestContents(int i, int j, List<WeightedRandomChestContent> list) {
		WeightedRandomChestContent[] w = new WeightedRandomChestContent[list.size()];
		w = list.toArray(w);
		minItems = i;
		maxItems = j;
		items = w;
	}

	public static void fillChest(IBlockAccess world, Random random, int i, int j, int k, TESChestContents itemPool) {
		fillChest(world, random, i, j, k, itemPool, -1);
	}

	public static void fillChest(IBlockAccess world, Random random, int i, int j, int k, TESChestContents itemPool, int amount) {
		TileEntity tileentity = world.getTileEntity(i, j, k);
		if (!(tileentity instanceof IInventory)) {
			if (j >= 0 && j < 256) {
				FMLLog.warning("Warning: TESChestContents attempted to fill a chest at " + i + ", " + j + ", " + k + " which does not exist");
			}
			return;
		}
		fillInventory((IInventory) tileentity, random, itemPool, amount);
	}

	public static void fillInventory(IInventory inventory, Random random, TESChestContents itemPool, int amount) {
		fillInventory(inventory, random, itemPool, amount, false);
	}

	public static void fillInventory(IInventory inventory, Random random, TESChestContents itemPool, int amount, boolean isNPCDrop) {
		int amount1 = amount;
		if (amount1 == -1) {
			amount1 = getRandomItemAmount(itemPool, random);
		} else if (amount1 <= 0) {
			throw new IllegalArgumentException("TESChestContents tried to fill a chest with " + amount1 + " items");
		}
		for (int i = 0; i < amount1; ++i) {
			WeightedRandomChestContent wrcc = (WeightedRandomChestContent) WeightedRandom.getRandomItem(random, itemPool.items);
			for (ItemStack itemstack : ChestGenHooks.generateStacks(random, wrcc.theItemId, wrcc.theMinimumChanceToGenerateItem, wrcc.theMaximumChanceToGenerateItem)) {
				Item item;
				if (!isNPCDrop && itemPool.pouches) {
					if (random.nextInt(50) == 0) {
						itemstack = new ItemStack(TESItems.pouch, 1, TESItemPouch.getRandomPouchSize(random));
					} else if (random.nextInt(50) == 0) {
						itemstack = TESItemModifierTemplate.getRandomCommonTemplate(random);
					}
				}
				if (!itemPool.loreCategories.isEmpty()) {
					TESLore lore;
					int loreChance = itemPool.loreChance;
					int minDropLoreChance = 8;
					if (isNPCDrop && loreChance > minDropLoreChance) {
						loreChance = (int) (loreChance * 0.75f);
						loreChance = Math.max(loreChance, minDropLoreChance);
					}
					if (random.nextInt(Math.max(loreChance, 1)) == 0 && (lore = TESLore.getMultiRandomLore(itemPool.loreCategories, random, false)) != null) {
						itemstack = lore.createLoreBook(random);
					}
				}
				if (itemstack.isItemStackDamageable() && !itemstack.getHasSubtypes()) {
					itemstack.setItemDamage(MathHelper.floor_double(itemstack.getMaxDamage() * MathHelper.randomFloatClamp(random, 0.0f, 0.75f)));
				}
				if (itemstack.stackSize > itemstack.getMaxStackSize()) {
					itemstack.stackSize = itemstack.getMaxStackSize();
				}
				if (TESConfig.enchantingTES) {
					boolean skilful = !isNPCDrop && random.nextInt(5) == 0;
					TESEnchantmentHelper.applyRandomEnchantments(itemstack, random, skilful, false);
				}
				if ((item = itemstack.getItem()) instanceof TESItemMug) {
					TESItemMug.Vessel[] vessels;
					if (((TESItemMug) item).isBrewable()) {
						TESItemMug.setStrengthMeta(itemstack, 1 + random.nextInt(3));
					}
					if (TESItemMug.isItemFullDrink(itemstack) && (vessels = itemPool.vesselTypes) != null) {
						TESItemMug.Vessel v = vessels[random.nextInt(vessels.length)];
						TESItemMug.setVessel(itemstack, v, true);
					}
				}
				inventory.setInventorySlotContents(random.nextInt(inventory.getSizeInventory()), itemstack);
			}
		}
	}

	private static int getRandomItemAmount(TESChestContents itemPool, Random random) {
		return MathHelper.getRandomIntegerInRange(random, itemPool.minItems, itemPool.maxItems);
	}

	public ItemStack getOneItem(Random random, boolean isNPCDrop) {
		IInventory drops = new InventoryBasic("oneItem", false, 1);
		fillInventory(drops, random, this, 1, isNPCDrop);
		ItemStack item = drops.getStackInSlot(0);
		item.stackSize = 1;
		return item;
	}

	@SuppressWarnings("WeakerAccess")
	public TESChestContents enablePouches() {
		pouches = true;
		return this;
	}

	@SuppressWarnings("WeakerAccess")
	public TESChestContents setDrinkVessels(TESFoods foods) {
		return setDrinkVessels(foods.getDrinkVessels());
	}

	@SuppressWarnings("WeakerAccess")
	public TESChestContents setDrinkVessels(TESItemMug.Vessel... v) {
		vesselTypes = v;
		return this;
	}

	@SuppressWarnings("WeakerAccess")
	public TESChestContents setLore(int chance, TESLore.LoreCategory... categories) {
		loreCategories = Arrays.asList(categories);
		loreChance = chance;
		return this;
	}
}