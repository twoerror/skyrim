package tes.common.database;

import com.google.common.base.CaseFormat;
import cpw.mods.fml.common.registry.GameRegistry;
import tes.common.block.other.TESBlockReplacement;
import tes.common.enchant.TESEnchantment;
import tes.common.item.other.*;
import tes.common.item.tool.TESItemAxe;
import tes.common.item.tool.TESItemHoe;
import tes.common.item.tool.TESItemPickaxe;
import tes.common.item.tool.TESItemShovel;
import tes.common.item.weapon.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Collection;

@SuppressWarnings({"WeakerAccess", "PublicField"})
public class TESItems {
	public static final Collection<Item> CONTENT = new ArrayList<>();

	public static Item beaverRaw;
	public static Item beaverTail;
	public static Item beaverCooked;
	public static Item aegonHelmet;
	public static Item aleHorn;
	public static Item aleHornGold;
	public static Item alloySteelAxe;
	public static Item alloySteelDagger;
	public static Item alloySteelDaggerPoisoned;
	public static Item alloySteelHalberd;
	public static Item alloySteelHoe;
	public static Item alloySteelIngot;
	public static Item alloySteelNugget;
	public static Item alloySteelPickaxe;
	public static Item alloySteelShovel;
	public static Item alloySteelSword;
	public static Item almond;
	public static Item amber;
	public static Item amethyst;
	public static Item anonymousMask;
	public static Item appleCrumble;
	public static Item appleGreen;
	public static Item archmagStaff;
	public static Item areoHotahAxe;
	public static Item armorStand;
	public static Item arrowFire;
	public static Item arrowPoisoned;
	public static Item baelishBrooch;
	public static Item baelishDagger;
	public static Item banana;
	public static Item bananaBread;
	public static Item bananaCake;
	public static Item bane;
	public static Item banner;
	public static Item bannerTab;
	public static Item bearRug;
	public static Item bericSword;
	public static Item berryPie;
	public static Item bittersteelHelmet;
	public static Item blackArakh;
	public static Item blackberry;
	public static Item bloodOfTrueKings;
	public static Item blueberry;
	public static Item boltonDagger;
	public static Item boneBoots;
	public static Item boneChestplate;
	public static Item gemsbokHide;
	public static Item gemsbokHorn;
	public static Item boneHelmet;
	public static Item boneLeggings;
	public static Item bottlePoison;
	public static Item bountyTrophy;
	public static Item brandingIron;
	public static Item brightroar;
	public static Item bronzeAxe;
	public static Item bronzeBattleaxe;
	public static Item bronzeBoots;
	public static Item bronzeChestplate;
	public static Item bronzeCrossbow;
	public static Item bronzeDagger;
	public static Item bronzeDaggerPoisoned;
	public static Item bronzeHelmet;
	public static Item mugCarrotWine;
	public static Item bronzeHoe;
	public static Item bronzeIngot;
	public static Item bronzeLeggings;
	public static Item bronzeNugget;
	public static Item bronzePickaxe;
	public static Item bronzeRing;
	public static Item bronzeShovel;
	public static Item bronzeSpear;
	public static Item bronzeSword;
	public static Item bronzeThrowingAxe;
	public static Item camelCooked;
	public static Item camelRaw;
	public static Item cargocart;
	public static Item celtigarAxe;
	public static Item ceramicMug;
	public static Item ceramicPlate;
	public static Item cherry;
	public static Item cherryPie;
	public static Item chestnut;
	public static Item chestnutRoast;
	public static Item chisel;
	public static Item clayMug;
	public static Item clayPlate;
	public static Item club;
	public static Item cobaltBlue;
	public static Item coin;
	public static Item commandHorn;
	public static Item commandSword;
	public static Item conquestHorn;
	public static Item copperIngot;
	public static Item copperNugget;
	public static Item copperRing;
	public static Item coral;
	public static Item corn;
	public static Item cornBread;
	public static Item cornCooked;
	public static Item cranberry;
	public static Item crossbowBolt;
	public static Item crossbowBoltPoisoned;
	public static Item crowbar;
	public static Item crownlandsBoots;
	public static Item crownlandsChestplate;
	public static Item crownlandsHelmet;
	public static Item crownlandsLeggings;
	public static Item cutwave;
	public static Item daarioArakh;
	public static Item darkSister;
	public static Item darkstar;
	public static Item dart;
	public static Item dartPoisoned;
	public static Item date;
	public static Item dawn;
	public static Item deerCooked;
	public static Item deerRaw;
	public static Item diamond;
	public static Item diamondHorseArmor;
	public static Item dye;
	public static Item eddardSword;
	public static Item elderberry;
	public static Item elephantCooked;
	public static Item elephantRaw;
	public static Item emerald;
	public static Item euronDagger;
	public static Item featherDyed;
	public static Item fin;
	public static Item firePot;
	public static Item flax;
	public static Item flaxSeeds;
	public static Item flintDagger;
	public static Item flintSpear;
	public static Item fur;
	public static Item furBed;
	public static Item fuse;
	public static Item gammon;
	public static Item gateGear;
	public static Item gingerbread;
	public static Item giraffeRug;
	public static Item gobletCopper;
	public static Item gobletGold;
	public static Item gobletSilver;
	public static Item gobletWood;
	public static Item goldBoots;
	public static Item goldChestplate;
	public static Item goldHelmet;
	public static Item goldHorseArmor;
	public static Item goldLeggings;
	public static Item goldRing;
	public static Item grapeRed;
	public static Item grapeWhite;
	public static Item gregorCleganeSword;
	public static Item handGold;
	public static Item handSilver;
	public static Item harpy;
	public static Item hearteater;
	public static Item heartsbane;
	public static Item honor;
	public static Item horn;
	public static Item ice;
	public static Item iceHeavySword;
	public static Item iceShard;
	public static Item iceSpear;
	public static Item iceSword;
	public static Item indomitable;
	public static Item ironBattleaxe;
	public static Item ironCrossbow;
	public static Item ironDagger;
	public static Item ironDaggerPoisoned;
	public static Item ironHorseArmor;
	public static Item ironNugget;
	public static Item ironPike;
	public static Item ironSpear;
	public static Item ironThrowingAxe;
	public static Item justMaid;
	public static Item kaftanChestplate;
	public static Item kaftanLeggings;
	public static Item katana;
	public static Item kebab;
	public static Item ladyForlorn;
	public static Item lamentation;
	public static Item lannisterBrooch;
	public static Item leatherHat;
	public static Item leek;
	public static Item leekSoup;
	public static Item lemon;
	public static Item lemonCake;
	public static Item lettuce;

	public static Item lightbringer;
	public static Item lime;
	public static Item lingeringPotion;
	public static Item lionBed;
	public static Item lionCooked;
	public static Item lionFur;
	public static Item lionRaw;
	public static Item lionRug;
	public static Item longclaw;
	public static Item mango;
	public static Item mapleSyrup;
	public static Item marzipan;
	public static Item marzipanChocolate;
	public static Item melonSoup;
	public static Item mug;
	public static Item mugAle;
	public static Item mugAppleJuice;
	public static Item mugAraq;
	public static Item mugBananaBeer;
	public static Item mugBlackberryJuice;
	public static Item mugBlueberryJuice;
	public static Item mugBrandy;
	public static Item mugCactusLiqueur;
	public static Item CarrotWine;
	public static Item mugCherryLiqueur;
	public static Item mugChocolate;
	public static Item mugCider;
	public static Item mugCocoa;
	public static Item mugCornLiquor;
	public static Item mugCranberryJuice;
	public static Item mugElderberryJuice;
	public static Item mugEthanol;
	public static Item mugGin;
	public static Item mugLemonLiqueur;
	public static Item mugLemonade;
	public static Item mugLimeLiqueur;
	public static Item mugMangoJuice;
	public static Item mugMapleBeer;
	public static Item mugMead;
	public static Item mugMelonLiqueur;
	public static Item mugMilk;
	public static Item mugOrangeJuice;
	public static Item mugPerry;
	public static Item mugPlantainBrew;
	public static Item mugPlumKvass;
	public static Item mugPomegranateJuice;
	public static Item mugPomegranateWine;
	public static Item mugPoppyMilk;
	public static Item mugRaspberryJuice;
	public static Item mugRedGrapeJuice;
	public static Item mugRedWine;
	public static Item mugRum;
	public static Item mugSambuca;
	public static Item mugShadeEvening;
	public static Item mugSourMilk;
	public static Item mugTermiteTequila;
	public static Item mugUnsulliedTonic;
	public static Item mugVodka;
	public static Item mugWater;
	public static Item mugWhisky;
	public static Item mugWhiteGrapeJuice;
	public static Item mugWhiteWine;
	public static Item mugWildFire;
	public static Item mushroomPie;
	public static Item muttonCooked;
	public static Item muttonRaw;
	public static Item mysteryWeb;
	public static Item needle;
	public static Item nightKingSword;
	public static Item nightfall;
	public static Item npcRespawner;
	public static Item nymeriaWhip;
	public static Item oathkeeper;
	public static Item obaraSpear;
	public static Item obsidianShard;
	public static Item olive;
	public static Item oliveBread;
	public static Item opal;
	public static Item orange;
	public static Item orphanMaker;
	public static Item pancake;
	public static Item pastry;
	public static Item pear;
	public static Item pearl;
	public static Item pebble;
	public static Item pipe;
	public static Item pipeweed;
	public static Item pipeweedLeaf;
	public static Item pipeweedSeeds;
	public static Item plate;
	public static Item plowcart;
	public static Item plum;
	public static Item pomegranate;
	public static Item pouch;
	public static Item pruner;
	public static Item questBook;
	public static Item rabbitCooked;
	public static Item rabbitRaw;
	public static Item rabbitStew;
	public static Item raisins;
	public static Item raspberry;
	public static Item redClayBall;
	public static Item redRain;
	public static Item reminder;
	public static Item rhinoArmor;
	public static Item rhinoCooked;
	public static Item rhinoHorn;
	public static Item rhinoRaw;
	public static Item rollingPin;
	public static Item ruby;
	public static Item salt;
	public static Item saltedFlesh;
	public static Item saltpeter;
	public static Item sapphire;
	public static Item sarbacane;
	public static Item seedsGrapeRed;
	public static Item seedsGrapeWhite;
	public static Item shishKebab;
	public static Item silverIngot;
	public static Item silverNugget;
	public static Item silverRing;
	public static Item skullCup;
	public static Item skullStaff;
	public static Item sling;
	public static Item smithScroll;
	public static Item spawnEgg;
	public static Item stoneSpear;
	public static Item strawBed;
	public static Item structureSpawner;
	public static Item sulfur;
	public static Item sulfurMatch;
	public static Item sunspear;
	public static Item swanFeather;
	public static Item termite;
	public static Item tidewings;
	public static Item tinIngot;
	public static Item topaz;
	public static Item tormundSword;
	public static Item trident;
	public static Item truth;
	public static Item trystaneSword;
	public static Item tugarKhanSword;
	public static Item turnip;
	public static Item turnipCooked;
	public static Item tyeneDagger;
	public static Item walkingStick;
	public static Item walrusLardCooked;
	public static Item walrusLardRaw;
	public static Item waterskin;
	public static Item wheel;
	public static Item widowWail;
	public static Item wildberry;
	public static Item wineGlass;
	public static Item yam;
	public static Item yamRoast;
	public static Item zebraCooked;
	public static Item zebraRaw;
	public static Item whiteBisonHorn;
	
	public static Item bronzeChainmailBoots;
	public static Item bronzeChainmailChestplate;
	public static Item bronzeChainmailHelmet;
	public static Item bronzeChainmailLeggings;
	
	public static Item ironSkyRimBoots;
	public static Item ironSkyRimChestplate;
	public static Item ironSkyRimHelmet;
	public static Item ironSkyRimLeggings;
	
	public static Item OrcishBoots;
	public static Item OrcishChestplate;
	public static Item OrcishHelmet;
	public static Item OrcishLeggings;

	private TESItems() {
	}

	private static void assignContent() {
		aegonHelmet = new TESItemArmor(TESMaterial.HELMET, 0, "aegon").setCreativeTab(TESCreativeTabs.TAB_STORY);
		aleHorn = new TESItemVessel();
		aleHornGold = new TESItemVessel();
		alloySteelAxe = new TESItemAxe(TESMaterial.COBALT_TOOL);
		alloySteelDagger = new TESItemDagger(TESMaterial.COBALT_TOOL);
		alloySteelDaggerPoisoned = new TESItemDagger(TESMaterial.COBALT_TOOL, TESItemSword.HitEffect.POISON);
		alloySteelHalberd = new TESItemPolearmLong(TESMaterial.COBALT_TOOL);
		alloySteelHoe = new TESItemHoe(TESMaterial.COBALT_TOOL);
		alloySteelIngot = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		alloySteelNugget = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		alloySteelPickaxe = new TESItemPickaxe(TESMaterial.COBALT_TOOL);
		alloySteelShovel = new TESItemShovel(TESMaterial.COBALT_TOOL);
		alloySteelSword = new TESItemSword(TESMaterial.COBALT_TOOL);
		almond = new TESItemFood(2, 0.2f, false);
		amber = new TESItemGemWithAnvilNameColor(EnumChatFormatting.YELLOW);
		amethyst = new TESItemGemWithAnvilNameColor(EnumChatFormatting.LIGHT_PURPLE);
		anonymousMask = new TESItemArmor(TESMaterial.ANONYMOUS, 0).setCreativeTab(null);
		appleGreen = new TESItemFood(4, 0.3f, false);
		archmagStaff = new TESItemAsshaiArchmagStaff();
		areoHotahAxe = new TESItemLegendaryBattleaxe(Item.ToolMaterial.IRON);
		armorStand = new TESItemArmorStand();
		arrowFire = new TESItemArrowFire();
		arrowPoisoned = new TESItemArrowPoisoned();
		baelishBrooch = new TESItemArmor(TESMaterial.HAND, 1, "baelish").setCreativeTab(TESCreativeTabs.TAB_STORY);
		baelishDagger = new TESItemLegendaryDagger(TESMaterial.VALYRIAN_TOOL);
		banana = new TESItemHangingFruit(2, 0.5f, false, TESBlocks.banana);
		bananaBread = new TESItemFood(5, 0.6f, false);
		bane = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		banner = new TESItemBanner();
		bannerTab = new TESItemBannerTab();
		bearRug = new TESItemBearRug();
		bericSword = new TESItemLegendarySword(Item.ToolMaterial.IRON, TESItemSword.HitEffect.FIRE);
		bittersteelHelmet = new TESItemArmor(TESMaterial.HELMET, 0, "bittersteel").setCreativeTab(TESCreativeTabs.TAB_STORY);
		blackArakh = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		blackberry = new TESItemBerry();
		bloodOfTrueKings = new Item().setCreativeTab(TESCreativeTabs.TAB_MISC);
		blueberry = new TESItemBerry();
		boltonDagger = new TESItemLegendaryDagger(TESMaterial.VALYRIAN_TOOL);
		boneBoots = new TESItemArmor(TESMaterial.BONE, 3);
		boneChestplate = new TESItemArmor(TESMaterial.BONE, 1);
		boneHelmet = new TESItemArmor(TESMaterial.BONE, 0);
		boneLeggings = new TESItemArmor(TESMaterial.BONE, 2);
		bottlePoison = new TESItemBottlePoison();
		bountyTrophy = new TESItemEnchantment(TESEnchantment.HEADHUNTING);
		brandingIron = new TESItemBrandingIron();
		brightroar = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		bronzeAxe = new TESItemAxe(TESMaterial.BRONZE_TOOL);
		bronzeBattleaxe = new TESItemBattleaxe(TESMaterial.BRONZE_TOOL);
		bronzeBoots = new TESItemArmor(TESMaterial.BRONZE, 3);
		bronzeChestplate = new TESItemArmor(TESMaterial.BRONZE, 1);
		bronzeCrossbow = new TESItemCrossbow(TESMaterial.BRONZE_TOOL);
		bronzeDagger = new TESItemDagger(TESMaterial.BRONZE_TOOL);
		bronzeDaggerPoisoned = new TESItemDagger(TESMaterial.BRONZE_TOOL, TESItemSword.HitEffect.POISON);
		bronzeHelmet = new TESItemArmor(TESMaterial.BRONZE, 0);
		bronzeHoe = new TESItemHoe(TESMaterial.BRONZE_TOOL);
		bronzeIngot = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		bronzeLeggings = new TESItemArmor(TESMaterial.BRONZE, 2);
		bronzeNugget = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		bronzePickaxe = new TESItemPickaxe(TESMaterial.BRONZE_TOOL);
		bronzeRing = new TESItemRing();
		bronzeShovel = new TESItemShovel(TESMaterial.BRONZE_TOOL);
		bronzeSpear = new TESItemSpear(TESMaterial.BRONZE_TOOL);
		bronzeSword = new TESItemSword(TESMaterial.BRONZE_TOOL);
		bronzeThrowingAxe = new TESItemThrowingAxe(TESMaterial.BRONZE_TOOL);
		camelCooked = new TESItemFood(6, 0.6f, true);
		camelRaw = new TESItemFood(2, 0.2f, true);
		cargocart = new TESItemCargocart();
		celtigarAxe = new TESItemLegendaryBattleaxe(TESMaterial.VALYRIAN_TOOL);
		ceramicMug = new TESItemVessel();
		cherry = new TESItemFood(2, 0.2f, false);
		chestnut = new TESItemConker();
		chestnutRoast = new TESItemFood(2, 0.2f, false);
		chisel = new TESItemChisel(TESBlocks.signCarved);
		clayMug = new Item().setCreativeTab(TESCreativeTabs.TAB_FOOD);
		mug = new TESItemVessel();
		mugAle = new TESItemMug(0.3f).setDrinkStats(3, 0.3f);
		mugAppleJuice = new TESItemMug(true, true).setDrinkStats(6, 0.6f);
		mugAraq = new TESItemMug(1.4f).setDrinkStats(4, 0.4f);
		mugBananaBeer = new TESItemMug(0.5f).setDrinkStats(4, 0.6f);
		mugBlackberryJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugBlueberryJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugBrandy = new TESItemMug(0.8f).setDrinkStats(2, 0.3f);
		mugCactusLiqueur = new TESItemMug(0.8f).setDrinkStats(2, 0.3f);
		mugCarrotWine = new TESItemMug(0.8f).setDrinkStats(3, 0.4f);
		mugCherryLiqueur = new TESItemMug(1.0f).setDrinkStats(3, 0.3f);
		mugChocolate = new TESItemMug(true, true).setDrinkStats(6, 0.6f);
		mugCider = new TESItemMug(0.3f).setDrinkStats(4, 0.4f);
		mugCocoa = new TESItemMug(0.0f).setDrinkStats(6, 0.6f).addPotionEffect(Potion.damageBoost.id, 40).addPotionEffect(Potion.moveSpeed.id, 40);
		mugCornLiquor = new TESItemMug(1.0f).setDrinkStats(3, 0.3f);
		mugCranberryJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugElderberryJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugEthanol = new TESItemMug(3.2f).setDrinkStats(5, 0.5f);
		mugGin = new TESItemMug(0.8f).setDrinkStats(2, 0.3f);
		mugLemonLiqueur = new TESItemMug(1.0f).setDrinkStats(3, 0.3f);
		mugLemonade = new TESItemMug(true, true).setDrinkStats(5, 0.3f);
		mugLimeLiqueur = new TESItemMug(1.0f).setDrinkStats(3, 0.3f);
		mugMangoJuice = new TESItemMug(true, true).setDrinkStats(6, 0.6f);
		mugMapleBeer = new TESItemMug(0.4f).setDrinkStats(4, 0.6f);
		mugMead = new TESItemMug(0.6f).setDrinkStats(4, 0.4f);
		mugMelonLiqueur = new TESItemMug(1.0f).setDrinkStats(3, 0.3f);
		mugMilk = new TESItemMug(true, false).setCuresEffects();
		mugOrangeJuice = new TESItemMug(true, true).setDrinkStats(6, 0.6f);
		mugPerry = new TESItemMug(0.3f).setDrinkStats(4, 0.4f);
		mugPlantainBrew = new TESItemMug(0.0f).setDrinkStats(6, 0.6f).addPotionEffect(Potion.damageBoost.id, 120).addPotionEffect(Potion.regeneration.id, 60);
		mugPlumKvass = new TESItemMug(0.2f).setDrinkStats(4, 0.4f);
		mugPomegranateJuice = new TESItemMug(true, true).setDrinkStats(6, 0.6f);
		mugPomegranateWine = new TESItemMug(0.9f).setDrinkStats(4, 0.4f);
		mugPoppyMilk = new TESItemMugPoppyMilk(0.0f).addPotionEffect(Potion.regeneration.id, 240);
		mugRaspberryJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugRedGrapeJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugRedWine = new TESItemMug(1.0f).setDrinkStats(4, 0.4f);
		mugRum = new TESItemMug(1.5f).setDrinkStats(3, 0.3f);
		mugSambuca = new TESItemMug(1.0f).setDrinkStats(3, 0.3f);
		mugShadeEvening = new TESItemMugWarlockDraught().setDrinkStats(4, 0.4f).setDamageAmount(3).addPotionEffect(Potion.nightVision.id, 300);
		mugSourMilk = new TESItemMug(0.2f).setDrinkStats(5, 0.5f);
		mugTermiteTequila = new TESItemMugTermite(0.0f).setDrinkStats(3, 0.3f);
		mugUnsulliedTonic = new TESItemMug(0.0f).setDrinkStats(6, 0.6f).addPotionEffect(Potion.damageBoost.id, 60).addPotionEffect(Potion.moveSpeed.id, 60).setDamageAmount(2);
		mugVodka = new TESItemMug(1.75f).setDrinkStats(3, 0.3f);
		mugWater = new TESItemMug(true, false);
		mugWhisky = new TESItemMug(1.0f).setDrinkStats(4, 0.4f);
		mugWhiteGrapeJuice = new TESItemMug(true, true).setDrinkStats(5, 0.5f);
		mugWhiteWine = new TESItemMug(0.9f).setDrinkStats(4, 0.4f);
		mugWildFire = new TESItemMugFire(0.0f).setDrinkStats(3, 0.3f);
		clayPlate = new Item().setCreativeTab(TESCreativeTabs.TAB_FOOD);
		club = new TESItemHammer(Item.ToolMaterial.WOOD);
		cobaltBlue = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		coin = new TESItemCoin().setCreativeTab(TESCreativeTabs.TAB_MISC);
		commandHorn = new TESItemCommandHorn();
		commandSword = new TESItemCommandSword();
		conquestHorn = new TESItemConquestHorn();
		copperIngot = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		copperNugget = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		copperRing = new TESItemRing();
		coral = new TESItemGem();
		corn = new TESItemFood(2, 0.3f, false);
		cornBread = new TESItemFood(5, 0.6f, false);
		cornCooked = new TESItemFood(4, 0.4f, false);
		cranberry = new TESItemBerry();
		crossbowBolt = new TESItemCrossbowBolt();
		crossbowBoltPoisoned = new TESItemCrossbowBolt().setPoisoned();
		crowbar = new TESItemLegendaryCrowbar(Item.ToolMaterial.IRON).setCreativeTab(null);
		crownlandsBoots = new TESItemArmor(TESMaterial.CROWNLANDS, 3);
		plate = new TESItemPlate(TESBlocks.plate);
		ceramicPlate = new TESItemPlate(TESBlocks.ceramicPlate);
		crownlandsChestplate = new TESItemArmor(TESMaterial.CROWNLANDS, 1);
		crownlandsHelmet = new TESItemArmor(TESMaterial.CROWNLANDS, 0);
		crownlandsLeggings = new TESItemArmor(TESMaterial.CROWNLANDS, 2);
		cutwave = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		daarioArakh = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		darkSister = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		darkstar = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		dart = new TESItemDart();
		dartPoisoned = new TESItemDart().setPoisoned();
		date = new TESItemHangingFruit(2, 0.3f, false, TESBlocks.date);
		dawn = new TESItemLegendarySword(Item.ToolMaterial.IRON).setIsGlowing();
		deerCooked = new TESItemFood(8, 0.8f, true);
		deerRaw = new TESItemFood(3, 0.3f, true);
		diamond = new TESItemGemWithAnvilNameColor(EnumChatFormatting.GRAY);
		diamondHorseArmor = new TESItemMountArmor(ItemArmor.ArmorMaterial.DIAMOND, TESItemMountArmor.Mount.HORSE, null).setTemplateItem(Items.diamond_horse_armor);
		dye = new TESItemDye();
		eddardSword = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		elderberry = new TESItemBerry();
		elephantCooked = new TESItemFood(7, 0.4f, true);
		elephantRaw = new TESItemFood(2, 0.1f, true);
		emerald = new TESItemGemWithAnvilNameColor(EnumChatFormatting.GREEN);
		euronDagger = new TESItemLegendaryDagger(TESMaterial.VALYRIAN_TOOL);
		featherDyed = new TESItemFeatherDyed();
		fin = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		firePot = new TESItemFirePot();
		flax = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		flaxSeeds = new TESItemSeeds(TESBlocks.flaxCrop, Blocks.farmland);
		flintDagger = new TESItemDagger(TESMaterial.FLINT_TOOL);
		flintSpear = new TESItemSpear(TESMaterial.FLINT_TOOL);
		fur = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		furBed = new TESItemBed(TESBlocks.furBed);
		fuse = new TESItemDoubleTorch(TESBlocks.fuse).setCreativeTab(TESCreativeTabs.TAB_DECO);
		gammon = new TESItemFood(8, 0.8f, true);
		gateGear = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		gingerbread = new TESItemFood(8, 1.0f, false);
		giraffeRug = new TESItemGiraffeRug();
		gobletCopper = new TESItemVessel();
		gobletGold = new TESItemVessel();
		gobletSilver = new TESItemVessel();
		gobletWood = new TESItemVessel();
		goldBoots = new TESItemArmor(TESMaterial.GOLDENCOMPANY, 3);
		goldChestplate = new TESItemArmor(TESMaterial.GOLDENCOMPANY, 1);
		goldHelmet = new TESItemArmor(TESMaterial.GOLDENCOMPANY, 0);
		goldHorseArmor = new TESItemMountArmor(ItemArmor.ArmorMaterial.GOLD, TESItemMountArmor.Mount.HORSE, null).setTemplateItem(Items.golden_horse_armor);
		goldLeggings = new TESItemArmor(TESMaterial.GOLDENCOMPANY, 2);
		goldRing = new TESItemRing();
		grapeRed = new TESItemFood(2, 0.2f, false);
		grapeWhite = new TESItemFood(2, 0.2f, false);
		gregorCleganeSword = new TESItemLegendaryGreatsword(Item.ToolMaterial.IRON);
		handGold = new TESItemArmor(TESMaterial.HAND, 1, "gold").setCreativeTab(TESCreativeTabs.TAB_STORY);
		handSilver = new TESItemArmor(TESMaterial.HAND, 1, "silver").setCreativeTab(TESCreativeTabs.TAB_STORY);
		harpy = new TESItemArmor(TESMaterial.GHISCAR, 0, "harpy");
		hearteater = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		heartsbane = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		honor = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		horn = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		ice = new TESItemLegendaryGreatsword(TESMaterial.VALYRIAN_TOOL);
		iceHeavySword = new TESItemBattleaxe(TESMaterial.ICE_TOOL);
		iceShard = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		iceSpear = new TESItemSpear(TESMaterial.ICE_TOOL);
		iceSword = new TESItemSword(TESMaterial.ICE_TOOL);
		indomitable = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		ironBattleaxe = new TESItemBattleaxe(Item.ToolMaterial.IRON);
		ironCrossbow = new TESItemCrossbow(Item.ToolMaterial.IRON);
		ironDagger = new TESItemDagger(Item.ToolMaterial.IRON);
		ironDaggerPoisoned = new TESItemDagger(Item.ToolMaterial.IRON, TESItemSword.HitEffect.POISON);
		ironHorseArmor = new TESItemMountArmor(ItemArmor.ArmorMaterial.IRON, TESItemMountArmor.Mount.HORSE, null).setTemplateItem(Items.iron_horse_armor);
		ironNugget = new Item().setCreativeTab(CreativeTabs.tabMaterials);
		ironPike = new TESItemPike(Item.ToolMaterial.IRON);
		ironSpear = new TESItemSpear(Item.ToolMaterial.IRON);
		ironThrowingAxe = new TESItemThrowingAxe(Item.ToolMaterial.IRON);
		justMaid = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		kaftanChestplate = new TESItemKaftan(1);
		kaftanLeggings = new TESItemKaftan(2);
		katana = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		kebab = new TESItemKebab(8, 0.8f, true);
		ladyForlorn = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		lamentation = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		lannisterBrooch = new TESItemArmor(TESMaterial.HAND, 1, "lannister").setCreativeTab(TESCreativeTabs.TAB_STORY);
		leatherHat = new TESItemLeatherHat();
		leek = new ItemSeedFood(2, 0.3f, TESBlocks.leekCrop, Blocks.farmland).setCreativeTab(TESCreativeTabs.TAB_FOOD);
		leekSoup = new TESItemStew(8, 0.8f, false);
		lemon = new TESItemFood(2, 0.2f, false);
		lettuce = new ItemSeedFood(3, 0.4f, TESBlocks.lettuceCrop, Blocks.farmland).setCreativeTab(TESCreativeTabs.TAB_FOOD);
		lightbringer = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		lime = new TESItemFood(2, 0.2f, false);
		lingeringPotion = new TESItemLingeringPotion();
		lionBed = new TESItemBed(TESBlocks.lionBed);
		lionCooked = new TESItemFood(8, 0.8f, true);
		lionFur = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		lionRaw = new TESItemFood(3, 0.3f, true);
		lionRug = new TESItemLionRug();
		longclaw = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		mango = new TESItemFood(4, 0.3f, false);
		mapleSyrup = new TESItemStew(2, 0.1f, false);
		marzipan = new TESItemFood(6, 0.6f, false);
		marzipanChocolate = new TESItemFood(8, 0.8f, false);
		melonSoup = new TESItemStew(5, 0.5f, false);
		mushroomPie = new TESItemFood(8, 0.3f, false);
		muttonCooked = new TESItemFood(8, 0.8f, true);
		muttonRaw = new TESItemFood(3, 0.3f, true);
		mysteryWeb = new TESItemMysteryWeb();
		needle = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		nightKingSword = new TESItemLegendarySword(TESMaterial.ICE_TOOL);
		nightfall = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		npcRespawner = new TESItemNPCRespawner();
		nymeriaWhip = new TESItemLegendaryWhip();
		oathkeeper = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		obaraSpear = new TESItemLegendarySword(Item.ToolMaterial.IRON, TESItemSword.HitEffect.POISON);
		obsidianShard = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		olive = new TESItemFood(1, 0.1f, false);
		oliveBread = new TESItemFood(5, 0.6f, false);
		opal = new TESItemGemWithAnvilNameColor(EnumChatFormatting.AQUA);
		orange = new TESItemFood(4, 0.3f, false);
		orphanMaker = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		pancake = new TESItemFood(4, 0.6f, false);
		pear = new TESItemFood(4, 0.3f, false);
		pearl = new TESItemGem();
		pebble = new TESItemPebble();
		pipe = new TESItemPipe();
		pipeweed = new Item().setCreativeTab(TESCreativeTabs.TAB_MISC);
		pipeweedLeaf = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		pipeweedSeeds = new TESItemSeeds(TESBlocks.pipeweedCrop, Blocks.grass);
		plowcart = new TESItemPlowcart();
		plum = new TESItemFood(4, 0.3f, false);
		pomegranate = new TESItemFood(4, 0.3f, false);
		pouch = new TESItemPouch();
		pruner = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		questBook = new TESItemQuestBook();
		rabbitCooked = new TESItemFood(6, 0.6f, true);
		rabbitRaw = new TESItemFood(2, 0.3f, true);
		rabbitStew = new TESItemStew(10, 0.8f, true);
		raisins = new TESItemFood(1, 0.1f, false);
		raspberry = new TESItemBerry();
		redClayBall = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		redRain = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		reminder = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		rhinoArmor = new TESItemMountArmor(ItemArmor.ArmorMaterial.CLOTH, TESItemMountArmor.Mount.RHINO, "rhino");
		rhinoCooked = new TESItemFood(7, 0.4f, true);
		rhinoHorn = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		rhinoRaw = new TESItemFood(2, 0.1f, true);
		rollingPin = new TESItemSword(Item.ToolMaterial.WOOD);
		ruby = new TESItemGemWithAnvilNameColor(EnumChatFormatting.RED);
		salt = new TESItemSalt();
		saltedFlesh = new TESItemFood(6, 0.6f, true);
		saltpeter = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		sapphire = new TESItemGemWithAnvilNameColor(EnumChatFormatting.BLUE);
		sarbacane = new TESItemSarbacane(Item.ToolMaterial.WOOD);
		seedsGrapeRed = new TESItemGrapeSeeds(TESBlocks.grapevineRed);
		seedsGrapeWhite = new TESItemGrapeSeeds(TESBlocks.grapevineWhite);
		shishKebab = new TESItemKebab(8, 0.8f, false).setFull3D();
		silverIngot = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		silverNugget = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		silverRing = new TESItemRing();
		skullCup = new TESItemVessel();
		skullStaff = new TESItemSkullStaff();
		sling = new TESItemSling();
		smithScroll = new TESItemModifierTemplate();
		spawnEgg = new TESItemSpawnEgg();
		stoneSpear = new TESItemSpear(Item.ToolMaterial.STONE);
		strawBed = new TESItemBed(TESBlocks.strawBed);
		structureSpawner = new TESItemStructureSpawner();
		sulfur = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		beaverTail = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		sulfurMatch = new TESItemMatch();
		sunspear = new TESItemLegendarySword(Item.ToolMaterial.IRON, TESItemSword.HitEffect.POISON);
		swanFeather = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		termite = new TESItemTermite();
		tidewings = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		tinIngot = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		topaz = new TESItemGemWithAnvilNameColor(EnumChatFormatting.GOLD);
		tormundSword = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		trident = new TESItemTrident(Item.ToolMaterial.IRON);
		truth = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		trystaneSword = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		tugarKhanSword = new TESItemLegendarySword(Item.ToolMaterial.IRON);
		turnip = new ItemSeedFood(2, 0.3f, TESBlocks.turnipCrop, Blocks.farmland).setCreativeTab(TESCreativeTabs.TAB_FOOD);
		turnipCooked = new TESItemFood(6, 0.6f, false);
		tyeneDagger = new TESItemLegendaryDagger(Item.ToolMaterial.IRON, TESItemSword.HitEffect.POISON);
		walkingStick = new TESItemSword(Item.ToolMaterial.WOOD);
		walrusLardCooked = new TESItemFood(7, 0.4f, true);
		walrusLardRaw = new TESItemFood(2, 0.1f, true);
		beaverCooked = new TESItemFood(7, 0.4f, true);
		beaverRaw = new TESItemFood(2, 0.1f, true);
		waterskin = new TESItemVessel();
		wheel = new TESItemWheel();
		widowWail = new TESItemLegendarySword(TESMaterial.VALYRIAN_TOOL);
		wildberry = new TESItemBerry().setPoisonous();
		wineGlass = new TESItemVessel();
		yam = new ItemSeedFood(1, 0.4f, TESBlocks.yamCrop, Blocks.farmland).setPotionEffect(Potion.hunger.id, 15, 0, 0.4f);
		yamRoast = new TESItemFood(6, 0.6f, false);
		zebraCooked = new TESItemFood(6, 0.6f, true);
		zebraRaw = new TESItemFood(2, 0.1f, true);
		gemsbokHide = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		gemsbokHorn = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		whiteBisonHorn = new Item().setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		bronzeChainmailBoots = new TESItemArmor(TESMaterial.BRONZE_CHAINMAIL, 3);
		bronzeChainmailChestplate = new TESItemArmor(TESMaterial.BRONZE_CHAINMAIL, 1);
		bronzeChainmailHelmet = new TESItemArmor(TESMaterial.BRONZE_CHAINMAIL, 0);
		bronzeChainmailLeggings = new TESItemArmor(TESMaterial.BRONZE_CHAINMAIL, 2);
	
		ironSkyRimBoots = new TESItemArmor(TESMaterial.IRON, 3);
		ironSkyRimChestplate = new TESItemArmor(TESMaterial.IRON, 1);
		ironSkyRimHelmet = new TESItemArmor(TESMaterial.IRON, 0);
		ironSkyRimLeggings = new TESItemArmor(TESMaterial.IRON, 2);
		
		armorStand = new TESItemArmorStand();

	}

	public static void preInit() {
		TESBlockReplacement.replaceVanillaItem(Items.potionitem, new TESItemPotion().setTextureName("potion"));
		TESBlockReplacement.replaceVanillaItem(Items.glass_bottle, new TESItemGlassBottle().setTextureName("potion_bottle_empty"));
		assignContent();
		registerContent();
	}

	public static void register(Item item, String codename) {
		String lowerUnderscoreName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, codename);
		item.setUnlocalizedName("tes:" + lowerUnderscoreName);
		item.setTextureName("tes:" + lowerUnderscoreName);
		GameRegistry.registerItem(item, "item." + lowerUnderscoreName);
		CONTENT.add(item);
	}

	private static void registerContent() {
		/* banner */

		register(banner, "banner");

		/* brewing */

		register(lingeringPotion, "lingeringPotion");

		/* combat */

		
		register(ironSkyRimBoots, "ironSkyRimBoots");
		register(ironSkyRimChestplate, "ironSkyRimChestplate");
		register(ironSkyRimHelmet, "ironSkyRimHelmet");
		register(ironSkyRimLeggings, "ironSkyRimLeggings");
		
		register(bronzeChainmailHelmet, "bronzeChainmailHelmet");
		register(bronzeChainmailChestplate, "bronzeChainmailChestplate");
		register(bronzeChainmailLeggings, "bronzeChainmailLeggings");
		register(bronzeChainmailBoots, "bronzeChainmailBoots");

		register(bronzeBattleaxe, "bronzeBattleaxe");
		register(bronzeCrossbow, "bronzeCrossbow");
		register(bronzeDagger, "bronzeDagger");
		register(bronzeDaggerPoisoned, "bronzeDaggerPoisoned");
		register(bronzeSpear, "bronzeSpear");
		register(bronzeSword, "bronzeSword");
		register(bronzeThrowingAxe, "bronzeThrowingAxe");

		register(ironBattleaxe, "ironBattleaxe");
		register(ironCrossbow, "ironCrossbow");
		register(ironDagger, "ironDagger");
		register(ironDaggerPoisoned, "ironDaggerPoisoned");
		register(ironPike, "ironPike");
		register(ironSpear, "ironSpear");
		register(ironThrowingAxe, "ironThrowingAxe");

		register(alloySteelDagger, "alloySteelDagger");
		register(alloySteelDaggerPoisoned, "alloySteelDaggerPoisoned");
		register(alloySteelHalberd, "alloySteelHalberd");
		register(alloySteelSword, "alloySteelSword");

		register(sarbacane, "sarbacane");
		register(dart, "dart");
		register(dartPoisoned, "dartPoisoned");

		register(sling, "sling");
		register(pebble, "pebble");

		register(firePot, "firePot");

		register(arrowFire, "arrowFire");
		register(arrowPoisoned, "arrowPoisoned");
		register(crossbowBolt, "crossbowBolt");
		register(crossbowBoltPoisoned, "crossbowBoltPoisoned");

		register(stoneSpear, "stoneSpear");

		register(flintDagger, "flintDagger");
		register(flintSpear, "flintSpear");

		register(iceHeavySword, "iceHeavySword");
		register(iceSpear, "iceSpear");
		register(iceSword, "iceSword");

		register(ironHorseArmor, "horseArmorIron");
		register(goldHorseArmor, "horseArmorGold");
		register(diamondHorseArmor, "horseArmorDiamond");

		register(rhinoArmor, "rhinoArmor");

		register(club, "club");
		register(trident, "trident");
		register(rollingPin, "rollingPin");
		register(skullStaff, "skullStaff");
		register(walkingStick, "walkingStick");

		register(commandSword, "commandSword");
		register(commandHorn, "commandHorn");
		register(conquestHorn, "conquestHorn");
		register(harpy, "harpy");

		register(bronzeHelmet, "bronzeHelmet");
		register(bronzeChestplate, "bronzeChestplate");
		register(bronzeLeggings, "bronzeLeggings");
		register(bronzeBoots, "bronzeBoots");

		register(boneHelmet, "boneHelmet");
		register(boneChestplate, "boneChestplate");
		register(boneLeggings, "boneLeggings");
		register(boneBoots, "boneBoots");
		
		register(gemsbokHide, "gemsbokHide");
		register(gemsbokHorn, "gemsbokHorn");
		register(whiteBisonHorn, "whiteBisonHorn");

		register(crownlandsHelmet, "crownlandsHelmet");
		register(crownlandsChestplate, "crownlandsChestplate");
		register(crownlandsLeggings, "crownlandsLeggings");
		register(crownlandsBoots, "crownlandsBoots");

		register(goldHelmet, "goldHelmet");
		register(goldChestplate, "goldChestplate");
		register(goldLeggings, "goldLeggings");
		register(goldBoots, "goldBoots");

		/* decorations */
		
		register(armorStand, "armorStand");

		/* vanilla materials */

		register(ironNugget, "ironNugget");

		/* materials */

		register(amber, "amber");
		register(amethyst, "amethyst");
		register(diamond, "diamond");
		register(emerald, "emerald");
		register(opal, "opal");
		register(pearl, "pearl");
		register(ruby, "ruby");
		register(sapphire, "sapphire");
		register(topaz, "topaz");

		register(coral, "coral");

		register(alloySteelIngot, "alloySteelIngot");
		register(bronzeIngot, "bronzeIngot");
		register(copperIngot, "copperIngot");
		register(tinIngot, "tinIngot");
		register(silverIngot, "silverIngot");

		register(alloySteelNugget, "alloySteelNugget");
		register(bronzeNugget, "bronzeNugget");
		register(copperNugget, "copperNugget");
		register(silverNugget, "silverNugget");

		register(cobaltBlue, "cobaltBlue");
		register(saltpeter, "saltpeter");
		register(sulfur, "sulfur");

		register(iceShard, "iceShard");
		register(obsidianShard, "obsidianShard");

		register(redClayBall, "redClayBall");

		register(gateGear, "gateGear");
		register(bountyTrophy, "bountyTrophy");

		register(pipeweedLeaf, "pipeweedLeaf");
		register(flax, "flax");
		register(pipeweedSeeds, "pipeweedSeeds");
		register(flaxSeeds, "flaxSeeds");
		register(seedsGrapeRed, "seedsGrapeRed");
		register(seedsGrapeWhite, "seedsGrapeWhite");

		register(fur, "fur");
		register(lionFur, "lionFur");
		register(horn, "horn");
		register(rhinoHorn, "rhinoHorn");
		register(beaverTail, "beaverTail");
		register(swanFeather, "swanFeather");

		register(dye, "dye");

		/* misc */

		register(questBook, "questBook");

		register(pouch, "pouch");

		register(coin, "coin");

		register(copperRing, "copperRing");
		register(bronzeRing, "bronzeRing");
		register(silverRing, "silverRing");
		register(goldRing, "goldRing");

		register(pipe, "pipe");
		register(pipeweed, "pipeweed");
		
		register(leatherHat, "leatherHat");

		register(kaftanChestplate, "kaftanChestplate");
		register(kaftanLeggings, "kaftanLeggings");

		register(wheel, "wheel");
		register(cargocart, "cargocart");
		register(plowcart, "plowcart");

		register(bottlePoison, "bottlePoison");
		register(bloodOfTrueKings, "bloodOfTrueKings");
		register(chestnut, "chestnut");
		register(mysteryWeb, "mysteryWeb");
		register(termite, "termite");

		register(npcRespawner, "npcRespawner");

		/* null */

		register(anonymousMask, "anonymousMask");
		register(bannerTab, "bannerTab");
		register(crowbar, "crowbar");
		register(featherDyed, "featherDyed");

		/* spawning */

		register(spawnEgg, "spawnEgg");
		register(structureSpawner, "structureSpawner");

		/* story */

		register(aegonHelmet, "aegonHelmet");
		register(bittersteelHelmet, "bittersteelHelmet");

		register(handSilver, "handSilver");
		register(handGold, "handGold");

		register(baelishBrooch, "baelishBrooch");
		register(lannisterBrooch, "lannisterBrooch");

		register(archmagStaff, "archmagStaff");
		register(areoHotahAxe, "areoHotahAxe");
		register(baelishDagger, "baelishDagger");
		register(bane, "bane");
		register(bericSword, "bericSword");
		register(blackArakh, "blackArakh");
		register(boltonDagger, "boltonDagger");
		register(brightroar, "brightroar");
		register(celtigarAxe, "celtigarAxe");
		register(cutwave, "cutwave");
		register(daarioArakh, "daarioArakh");
		register(darkSister, "darkSister");
		register(darkstar, "darkstar");
		register(dawn, "dawn");
		register(eddardSword, "eddardSword");
		register(euronDagger, "euronDagger");
		register(fin, "fin");
		register(gregorCleganeSword, "gregorCleganeSword");
		register(hearteater, "hearteater");
		register(heartsbane, "heartsbane");
		register(honor, "honor");
		register(ice, "ice");
		register(indomitable, "indomitable");
		register(justMaid, "justMaid");
		register(katana, "katana");
		register(ladyForlorn, "ladyForlorn");
		register(lamentation, "lamentation");
		register(lightbringer, "lightbringer");
		register(longclaw, "longclaw");
		register(needle, "needle");
		register(nightKingSword, "nightKingSword");
		register(nightfall, "nightfall");
		register(oathkeeper, "oathkeeper");
		register(obaraSpear, "obaraSpear");
		register(orphanMaker, "orphanMaker");
		register(pruner, "pruner");
		register(redRain, "redRain");
		register(reminder, "reminder");
		register(sunspear, "sunspear");
		register(tidewings, "tidewings");
		register(tormundSword, "tormundSword");
		register(truth, "truth");
		register(trystaneSword, "trystaneSword");
		register(tugarKhanSword, "tugarKhanSword");
		register(tyeneDagger, "tyeneDagger");
		register(widowWail, "widowWail");

		register(nymeriaWhip, "nymeriaWhip");

		/* tools */

		register(bronzeAxe, "bronzeAxe");
		register(bronzeHoe, "bronzeHoe");
		register(bronzePickaxe, "bronzePickaxe");
		register(bronzeShovel, "bronzeShovel");

		register(alloySteelAxe, "alloySteelAxe");
		register(alloySteelHoe, "alloySteelHoe");
		register(alloySteelPickaxe, "alloySteelPickaxe");
		register(alloySteelShovel, "alloySteelShovel"); 

		register(chisel, "chisel");

		register(brandingIron, "brandingIron");

		register(sulfurMatch, "sulfurMatch");

		register(smithScroll, "smithScroll");

		/* util */

		register(strawBed, "strawBed");
		register(furBed, "furBed");
		register(lionBed, "lionBed");
		
		register(plate, "plate");
		register(clayPlate, "clayPlate");
		register(ceramicPlate, "ceramicPlate");

		register(mug, "mug");
		register(clayMug, "clayMug");
		register(ceramicMug, "ceramicMug");
		register(mugMilk, "mugMilk");
		register(mugWater, "mugWater");

		register(mugAle, "mugAle");
		register(mugAraq, "mugAraq");
		register(mugBananaBeer, "mugBananaBeer");
		register(mugBrandy, "mugBrandy");
		register(mugCactusLiqueur, "mugCactusLiqueur");
		register(mugCarrotWine, "mugCarrotWine");
		register(mugCherryLiqueur, "mugCherryLiqueur");
		register(mugCider, "mugCider");
		register(mugCornLiquor, "mugCornLiquor");
		register(mugGin, "mugGin");
		register(mugLemonLiqueur, "mugLemonLiqueur");
		register(mugLimeLiqueur, "mugLimeLiqueur");
		register(mugMapleBeer, "mugMapleBeer");
		register(mugMead, "mugMead");
		register(mugMelonLiqueur, "mugMelonLiqueur");
		register(mugPerry, "mugPerry");
		register(mugPlantainBrew, "mugPlantainBrew");
		register(mugPlumKvass, "mugPlumKvass");
		register(mugPomegranateWine, "mugPomegranateWine");
		register(mugRedWine, "mugRedWine");
		register(mugRum, "mugRum");
		register(mugSambuca, "mugSambuca");
		register(mugSourMilk, "mugSourMilk");
		register(mugVodka, "mugVodka");
		register(mugWhisky, "mugWhisky");
		register(mugWhiteWine, "mugWhiteWine");

		register(mugChocolate, "mugChocolate");
		register(mugAppleJuice, "mugAppleJuice");
		register(mugBlackberryJuice, "mugBlackberryJuice");
		register(mugBlueberryJuice, "mugBlueberryJuice");
		register(mugCranberryJuice, "mugCranberryJuice");
		register(mugElderberryJuice, "mugElderberryJuice");
		register(mugLemonade, "mugLemonade");
		register(mugMangoJuice, "mugMangoJuice");
		register(mugOrangeJuice, "mugOrangeJuice");
		register(mugPomegranateJuice, "mugPomegranateJuice");
		register(mugRaspberryJuice, "mugRaspberryJuice");
		register(mugRedGrapeJuice, "mugRedGrapeJuice");
		register(mugWhiteGrapeJuice, "mugWhiteGrapeJuice");

		register(mugCocoa, "mugCocoa");
		register(mugPoppyMilk, "mugPoppyMilk");
		register(mugShadeEvening, "mugShadeEvening");
		register(mugUnsulliedTonic, "mugUnsulliedTonic");

		register(mugEthanol, "mugEthanol");
		register(mugTermiteTequila, "mugTermiteTequila");
		register(mugWildFire, "mugWildFire");
	}
}