package tes.common.database;

import tes.common.item.other.TESItemMug;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Random;

public class TESFoods {
	public static final TESFoods WESTEROS = new TESFoods(new ItemStack[]{new ItemStack(Items.cooked_porkchop), new ItemStack(Items.cooked_chicken), new ItemStack(Items.cooked_beef), new ItemStack(Items.cooked_fished), new ItemStack(TESItems.muttonCooked), new ItemStack(TESItems.deerCooked), new ItemStack(Items.baked_potato), new ItemStack(Items.apple), new ItemStack(TESItems.appleGreen), new ItemStack(TESItems.pear), new ItemStack(TESItems.olive), new ItemStack(TESItems.plum), new ItemStack(Items.bread), new ItemStack(TESItems.oliveBread), new ItemStack(TESItems.blueberry), new ItemStack(TESItems.blackberry), new ItemStack(TESItems.cranberry)});
	public static final TESFoods WESTEROS_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugMead), new ItemStack(TESItems.mugCider), new ItemStack(TESItems.mugPerry), new ItemStack(TESItems.mugAppleJuice)}).setDrinkVessels(TESItemMug.Vessel.MUG, TESItemMug.Vessel.MUG_CLAY, TESItemMug.Vessel.GOBLET_COPPER, TESItemMug.Vessel.GOBLET_WOOD, TESItemMug.Vessel.BOTTLE, TESItemMug.Vessel.HORN);
	public static final TESFoods WILD = new TESFoods(new ItemStack[]{new ItemStack(Items.cooked_porkchop), new ItemStack(Items.cooked_fished), new ItemStack(Items.cooked_chicken), new ItemStack(Items.cooked_beef), new ItemStack(TESItems.muttonCooked), new ItemStack(TESItems.gammon), new ItemStack(Items.baked_potato), new ItemStack(Items.apple), new ItemStack(TESItems.appleGreen), new ItemStack(TESItems.pear), new ItemStack(Items.bread), new ItemStack(TESItems.rabbitCooked), new ItemStack(TESItems.chestnutRoast)});
	public static final TESFoods WILD_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugMead), new ItemStack(TESItems.mugCider), new ItemStack(TESItems.mugRum)}).setDrinkVessels(TESItemMug.Vessel.MUG, TESItemMug.Vessel.GOBLET_WOOD, TESItemMug.Vessel.SKULL, TESItemMug.Vessel.SKIN, TESItemMug.Vessel.HORN);
	public static final TESFoods YITI = new TESFoods(new ItemStack[]{new ItemStack(Items.bread), new ItemStack(TESItems.oliveBread), new ItemStack(Items.carrot), new ItemStack(TESItems.lettuce), new ItemStack(TESItems.turnip), new ItemStack(TESItems.turnipCooked), new ItemStack(TESItems.rabbitCooked), new ItemStack(TESItems.deerCooked), new ItemStack(TESItems.olive), new ItemStack(TESItems.plum), new ItemStack(TESItems.almond), new ItemStack(Items.cooked_fished), new ItemStack(Items.cooked_chicken), new ItemStack(Items.cooked_beef), new ItemStack(Items.cooked_porkchop), new ItemStack(TESItems.grapeRed), new ItemStack(TESItems.grapeWhite), new ItemStack(TESItems.raisins), new ItemStack(TESItems.date), new ItemStack(TESItems.pomegranate)});
	public static final TESFoods YITI_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugSourMilk), new ItemStack(TESItems.mugSourMilk), new ItemStack(TESItems.mugSourMilk), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugRedWine), new ItemStack(TESItems.mugWhiteWine), new ItemStack(TESItems.mugRedGrapeJuice), new ItemStack(TESItems.mugWhiteGrapeJuice), new ItemStack(TESItems.mugPomegranateWine), new ItemStack(TESItems.mugPomegranateJuice)}).setDrinkVessels(TESItemMug.Vessel.MUG, TESItemMug.Vessel.MUG_CLAY, TESItemMug.Vessel.GOBLET_COPPER, TESItemMug.Vessel.GOBLET_WOOD, TESItemMug.Vessel.GLASS, TESItemMug.Vessel.BOTTLE, TESItemMug.Vessel.SKIN);
	public static final TESFoods ESSOS = new TESFoods(new ItemStack[]{new ItemStack(Items.bread), new ItemStack(TESItems.oliveBread), new ItemStack(Items.apple), new ItemStack(TESItems.appleGreen), new ItemStack(TESItems.pear), new ItemStack(TESItems.date), new ItemStack(TESItems.lemon), new ItemStack(TESItems.orange), new ItemStack(TESItems.lime), new ItemStack(TESItems.olive), new ItemStack(TESItems.almond), new ItemStack(TESItems.plum), new ItemStack(Items.carrot), new ItemStack(Items.baked_potato), new ItemStack(TESItems.lettuce), new ItemStack(Items.cooked_porkchop), new ItemStack(Items.cooked_fished), new ItemStack(Items.cooked_chicken), new ItemStack(Items.cooked_beef), new ItemStack(TESItems.muttonCooked), new ItemStack(TESItems.kebab), new ItemStack(TESItems.shishKebab), new ItemStack(TESItems.camelCooked)});
	public static final TESFoods ESSOS_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugWater), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugCactusLiqueur), new ItemStack(TESItems.mugOrangeJuice), new ItemStack(TESItems.mugLemonLiqueur), new ItemStack(TESItems.mugLemonade), new ItemStack(TESItems.mugLimeLiqueur), new ItemStack(TESItems.mugAle), new ItemStack(TESItems.mugCider)}).setDrinkVessels(TESItemMug.Vessel.MUG, TESItemMug.Vessel.MUG_CLAY, TESItemMug.Vessel.GOBLET_COPPER, TESItemMug.Vessel.GOBLET_WOOD, TESItemMug.Vessel.BOTTLE, TESItemMug.Vessel.SKIN);
	public static final TESFoods NOMAD = new TESFoods(new ItemStack[]{new ItemStack(Items.bread), new ItemStack(TESItems.oliveBread), new ItemStack(TESItems.date), new ItemStack(Items.cooked_beef), new ItemStack(TESItems.muttonCooked), new ItemStack(TESItems.kebab), new ItemStack(TESItems.shishKebab), new ItemStack(TESItems.camelCooked), new ItemStack(TESItems.camelCooked), new ItemStack(TESItems.camelCooked), new ItemStack(TESItems.camelCooked), new ItemStack(TESItems.saltedFlesh)});
	public static final TESFoods NOMAD_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugWater), new ItemStack(TESItems.mugWater), new ItemStack(TESItems.mugWater), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugAraq), new ItemStack(TESItems.mugCactusLiqueur), new ItemStack(TESItems.mugAle)}).setDrinkVessels(TESItemMug.Vessel.MUG, TESItemMug.Vessel.MUG_CLAY, TESItemMug.Vessel.GOBLET_COPPER, TESItemMug.Vessel.GOBLET_WOOD, TESItemMug.Vessel.SKIN);
	public static final TESFoods SOTHORYOS = new TESFoods(new ItemStack[]{new ItemStack(Items.bread), new ItemStack(TESItems.bananaBread), new ItemStack(TESItems.cornBread), new ItemStack(TESItems.corn), new ItemStack(TESItems.cornCooked), new ItemStack(Items.baked_potato), new ItemStack(TESItems.banana), new ItemStack(TESItems.mango), new ItemStack(Items.melon), new ItemStack(TESItems.melonSoup), new ItemStack(Items.cooked_fished)});
	public static final TESFoods SOTHORYOS_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugChocolate), new ItemStack(TESItems.mugMangoJuice), new ItemStack(TESItems.mugBananaBeer), new ItemStack(TESItems.mugMelonLiqueur), new ItemStack(TESItems.mugCornLiquor)}).setDrinkVessels(TESItemMug.Vessel.MUG, TESItemMug.Vessel.GOBLET_COPPER, TESItemMug.Vessel.GOBLET_WOOD);
	public static final TESFoods RICH_DRINK = new TESFoods(new ItemStack[]{new ItemStack(TESItems.mugRedWine)}).setDrinkVessels(TESItemMug.Vessel.GOBLET_COPPER, TESItemMug.Vessel.BOTTLE);

	private final ItemStack[] foodList;

	private TESItemMug.Vessel[] drinkVessels;
	private TESItemMug.Vessel[] drinkVesselsPlaceable;

	@SuppressWarnings("WeakerAccess")
	public TESFoods(ItemStack[] items) {
		foodList = items;
	}

	public TESItemMug.Vessel[] getDrinkVessels() {
		return drinkVessels;
	}

	@SuppressWarnings("WeakerAccess")
	public TESFoods setDrinkVessels(TESItemMug.Vessel... vessels) {
		drinkVessels = vessels;
		ArrayList<TESItemMug.Vessel> placeable = new ArrayList<>();
		for (TESItemMug.Vessel v : drinkVessels) {
			if (!v.isCanPlace()) {
				continue;
			}
			placeable.add(v);
		}
		if (placeable.isEmpty()) {
			drinkVesselsPlaceable = new TESItemMug.Vessel[]{TESItemMug.Vessel.MUG};
		} else {
			drinkVesselsPlaceable = placeable.toArray(new TESItemMug.Vessel[0]);
		}
		return this;
	}

	public TESItemMug.Vessel[] getPlaceableDrinkVessels() {
		return drinkVesselsPlaceable;
	}

	public ItemStack getRandomBrewableDrink(Random random) {
		ArrayList<ItemStack> alcohols = new ArrayList<>();
		for (ItemStack itemstack : foodList) {
			Item item = itemstack.getItem();
			if (!(item instanceof TESItemMug) || !((TESItemMug) item).isBrewable()) {
				continue;
			}
			alcohols.add(itemstack.copy());
		}
		ItemStack drink = alcohols.get(random.nextInt(alcohols.size()));
		setDrinkVessel(drink, random, false);
		return drink;
	}

	public ItemStack getRandomFood(Random random) {
		ItemStack food = foodList[random.nextInt(foodList.length)].copy();
		setDrinkVessel(food, random, false);
		return food;
	}

	public ItemStack getRandomFoodForPlate(Random random) {
		ArrayList<ItemStack> foodsNoContainer = new ArrayList<>();
		for (ItemStack itemstack : foodList) {
			Item item = itemstack.getItem();
			if (item.hasContainerItem(itemstack)) {
				continue;
			}
			foodsNoContainer.add(itemstack.copy());
		}
		return foodsNoContainer.get(random.nextInt(foodsNoContainer.size()));
	}

	public ItemStack getRandomPlaceableDrink(Random random) {
		ItemStack food = foodList[random.nextInt(foodList.length)].copy();
		setDrinkVessel(food, random, true);
		return food;
	}

	private TESItemMug.Vessel getRandomPlaceableVessel(Random random) {
		return drinkVesselsPlaceable[random.nextInt(drinkVesselsPlaceable.length)];
	}

	public TESItemMug.Vessel getRandomVessel(Random random) {
		return drinkVessels[random.nextInt(drinkVessels.length)];
	}

	private void setDrinkVessel(ItemStack itemstack, Random random, boolean requirePlaceable) {
		Item item = itemstack.getItem();
		if (item instanceof TESItemMug && ((TESItemMug) item).isFullMug()) {
			TESItemMug.Vessel v;
			if (requirePlaceable) {
				v = getRandomPlaceableVessel(random);
			} else {
				v = getRandomVessel(random);
			}
			TESItemMug.setVessel(itemstack, v, true);
		}
	}
}