package tes.common.database;

import tes.common.TESChatEvents;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import tes.common.util.TESEnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.HoverEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;

import java.awt.*;
import java.util.List;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "PublicField"})
public class TESAchievement {
	public static final Collection<TESAchievement> CONTENT = new ArrayList<>();

	public static final Map<ItemArmor.ArmorMaterial, TESAchievement> ARMOR_ACHIEVEMENTS = new EnumMap<>(ItemArmor.ArmorMaterial.class);

	public static TESAchievement obama;
	public static TESAchievement marry;
	public static TESAchievement gainHighAlcoholTolerance;
	public static TESAchievement hireGoldenCompany;
	public static TESAchievement mineValyrian;
	public static TESAchievement mineGlowstone;
	public static TESAchievement cookKebab;
	public static TESAchievement factionConquest;
	public static TESAchievement findFourLeafClover;
	public static TESAchievement findPlantain;
	public static TESAchievement fishRing;
	public static TESAchievement freeman;
	public static TESAchievement getConcrete;
	public static TESAchievement getDrunk;
	public static TESAchievement getPouch;
	public static TESAchievement growBaobab;
	public static TESAchievement hundreds;
	public static TESAchievement bannerProtect;
	public static TESAchievement brewDrinkInBarrel;
	public static TESAchievement catchButterfly;
	public static TESAchievement collectCraftingTables;
	public static TESAchievement collectCrossbowBolts;
	public static TESAchievement combineSmithScrolls;
	public static TESAchievement craftBomb;
	public static TESAchievement craftBronze;
	public static TESAchievement craftCopper;
	public static TESAchievement craftSaddle;
	public static TESAchievement craftWildFire;
	public static TESAchievement defeatInvasion;
	public static TESAchievement doLegendaryQuest;
	public static TESAchievement doMiniquestHunter5;
	public static TESAchievement doMiniquestHunter;
	public static TESAchievement doQuest;
	public static TESAchievement drinkFire;
	public static TESAchievement drinkPlantainBrew;
	public static TESAchievement drinkSkull;
	public static TESAchievement drinkTermite;
	public static TESAchievement earnManyCoins;
	public static TESAchievement engraveOwnership;
	public static TESAchievement lightBeacon;
	public static TESAchievement pledgeService;
	public static TESAchievement reforge;
	public static TESAchievement rideCamel;
	public static TESAchievement shootDownMidges;
	public static TESAchievement smeltObsidianShard;
	public static TESAchievement speakToDrunkard;
	public static TESAchievement steal;
	public static TESAchievement stealArborGrapes;
	public static TESAchievement trade;
	public static TESAchievement travel100;
	public static TESAchievement travel20;
	public static TESAchievement travel40;
	public static TESAchievement travel60;
	public static TESAchievement travel80;
	public static TESAchievement unsmelt;
	public static TESAchievement useCrossbow;
	public static TESAchievement useSpearFromFar;
	public static TESAchievement useThrowingAxe;
	public static TESAchievement enterKnownWorld;
	
	public static TESAchievement killBeaver;
	public static TESAchievement killer;
	public static TESAchievement killButterfly;
	public static TESAchievement killHuntingPlayer;
	public static TESAchievement killLargeMobWithSlingshot;
	public static TESAchievement killMammoth;
	public static TESAchievement killProstitute;
	public static TESAchievement killThievingBandit;
	public static TESAchievement killUlthos;
	public static TESAchievement killUsingOnlyPlates;
	public static TESAchievement killWerewolf;
	public static TESAchievement killWhileDrunk;

	private final Collection<TESFaction> allyFactions = new ArrayList<>();
	private final Category category;
	private final ItemStack icon;
	private final String name;
	private final int id;

	protected boolean isSpecial;

	private boolean isBiomeAchievement;
	private TESTitle achievementTitle;

	@SuppressWarnings("WeakerAccess")
	public TESAchievement(Category c, int i, Item item, String s) {
		this(c, i, new ItemStack(item), s);
	}

	@SuppressWarnings("WeakerAccess")
	public TESAchievement(Category c, int i, Block block, String s) {
		this(c, i, new ItemStack(block), s);
	}

	@SuppressWarnings("WeakerAccess")
	public TESAchievement(Category c, int i, ItemStack itemstack, String s) {
		category = c;
		id = i;
		icon = itemstack;
		name = s;
		for (TESAchievement achievement : category.getList()) {
			if (achievement.id != id) {
				continue;
			}
			throw new IllegalArgumentException("Duplicate ID " + id + " for TES achievement category " + category.name());
		}
		category.getList().add(this);
		getDimension().getAllAchievements().add(this);
		CONTENT.add(this);
	}

	public static TESAchievement achievementForCategoryAndID(Category category, int ID) {
		if (category == null) {
			return null;
		}
		for (TESAchievement achievement : category.getList()) {
			if (achievement.id != ID) {
				continue;
			}
			return achievement;
		}
		return null;
	}

	public static Category categoryForName(String name) {
		for (Category category : Category.values()) {
			if (category.name().equals(name)) {
				return category;
			}
		}
		return null;
	}

	private static TESAchievement createArmorAchievement(TESAchievement.Category category, int id, Item item, String name) {
		TESAchievement achievement = new TESAchievement(category, id, item, name);
		ARMOR_ACHIEVEMENTS.put(((ItemArmor) item).getArmorMaterial(), achievement);
		return achievement;
	}

	public static TESAchievement findByName(String name) {
		for (Category category : Category.values()) {
			for (TESAchievement achievement : category.getList()) {
				if (achievement.name.equalsIgnoreCase(name)) {
					return achievement;
				}
			}
		}
		return null;
	}

	public static List<TESAchievement> getAllAchievements() {
		List<TESAchievement> list = new ArrayList<>();
		for (Category category : Category.values()) {
			list.addAll(category.getList());
		}
		return list;
	}

	@SuppressWarnings({"UnusedAssignment", "ValueOfIncrementOrDecrementUsed"})
	public static void onInit() {
		int genId = 1;
		
		obama = new TESAchievement(Category.GENERAL, genId++, TESItems.banana, "OBAMA");
		marry = new TESAchievement(Category.GENERAL, genId++, TESItems.goldRing, "MARRY");
		gainHighAlcoholTolerance = new TESAchievement(Category.GENERAL, genId++, TESItems.mugAle, "GAIN_HIGH_ALCOHOL_TOLERANCE");
		hireGoldenCompany = new TESAchievement(Category.GENERAL, genId++, TESItems.goldHelmet, "HIRE_GOLDEN_COMPANY");
		mineValyrian = new TESAchievement(Category.GENERAL, genId++, TESBlocks.oreValyrian, "MINE_VALYRIAN");
		mineGlowstone = new TESAchievement(Category.GENERAL, genId++, TESBlocks.oreGlowstone, "MINE_GLOWSTONE");
		enterKnownWorld = new TESAchievement(Category.GENERAL, genId++, TESItems.gregorCleganeSword, "ENTER_KNOWN_WORLD");
		freeman = new TESAchievement(Category.GENERAL, genId++, TESItems.crowbar, "FREEMAN");
		bannerProtect = new TESAchievement(Category.GENERAL, genId++, TESItems.banner, "BANNER_PROTECT");
		brewDrinkInBarrel = new TESAchievement(Category.GENERAL, genId++, TESItems.mugEthanol, "BREW_DRINK_IN_BARREL");
		catchButterfly = new TESAchievement(Category.GENERAL, genId++, TESBlocks.butterflyJar, "CATCH_BUTTERFLY");
		collectCraftingTables = new TESAchievement(Category.GENERAL, genId++, Blocks.crafting_table, "COLLECT_CRAFTING_TABLES");
		collectCrossbowBolts = new TESAchievement(Category.GENERAL, genId++, TESItems.crossbowBolt, "COLLECT_CROSSBOW_BOLTS");
		combineSmithScrolls = new TESAchievement(Category.GENERAL, genId++, TESItems.smithScroll, "COMBINE_SMITH_SCROLLS");
		cookKebab = new TESAchievement(Category.GENERAL, genId++, TESItems.kebab, "COOK_KEBAB");
		craftBomb = new TESAchievement(Category.GENERAL, genId++, TESBlocks.bomb, "CRAFT_BOMB");
		craftBronze = new TESAchievement(Category.GENERAL, genId++, TESItems.bronzeIngot, "GET_BRONZE");
		craftCopper = new TESAchievement(Category.GENERAL, genId++, TESItems.copperIngot, "GET_COPPER");
		craftSaddle = new TESAchievement(Category.GENERAL, genId++, Items.saddle, "CRAFT_SADDLE");
		craftWildFire = new TESAchievement(Category.GENERAL, genId++, TESBlocks.wildFireJar, "CRAFT_WILD_FIRE");
		defeatInvasion = new TESAchievement(Category.GENERAL, genId++, TESItems.gregorCleganeSword, "DEFEAT_INVASION");
		doLegendaryQuest = new TESAchievement(Category.GENERAL, genId++, Blocks.dragon_egg, "DO_LEGENDARY_QUEST");
		doMiniquestHunter = new TESAchievement(Category.GENERAL, genId++, TESItems.questBook, "DO_MINIQUEST_HUNTER");
		doMiniquestHunter5 = new TESAchievement(Category.GENERAL, genId++, TESItems.bountyTrophy, "DO_MINIQUEST_HUNTER5");
		doQuest = new TESAchievement(Category.GENERAL, genId++, TESItems.questBook, "DO_QUEST");
		drinkFire = new TESAchievement(Category.GENERAL, genId++, TESItems.mugWildFire, "DRINK_FIRE");
		drinkPlantainBrew = new TESAchievement(Category.GENERAL, genId++, TESItems.mugPlantainBrew, "DRINK_PLANTAIN_BREW");
		drinkSkull = new TESAchievement(Category.GENERAL, genId++, TESItems.skullCup, "DRINK_SKULL");
		drinkTermite = new TESAchievement(Category.GENERAL, genId++, TESItems.mugTermiteTequila, "DRINK_TERMITE");
		earnManyCoins = new TESAchievement(Category.GENERAL, genId++, TESItems.coin, "EARN_MANY_COINS");
		engraveOwnership = new TESAchievement(Category.GENERAL, genId++, Blocks.anvil, "ENGRAVE_OWNERSHIP");
		factionConquest = new TESAchievement(Category.GENERAL, genId++, TESItems.gregorCleganeSword, "FACTION_CONQUEST");
		findFourLeafClover = new TESAchievement(Category.GENERAL, genId++, new ItemStack(TESBlocks.clover, 1, 1), "FIND_FOUR_LEAF_CLOVER");
		findPlantain = new TESAchievement(Category.GENERAL, genId++, TESBlocks.plantain, "FIND_PLANTAIN");
		fishRing = new TESAchievement(Category.GENERAL, genId++, Items.fishing_rod, "FISH_RING");
		getConcrete = new TESAchievement(Category.GENERAL, genId++, TESBlocks.CONCRETE_POWDER.get(TESEnumDyeColor.LIME), "GET_CONCRETE");
		getDrunk = new TESAchievement(Category.GENERAL, genId++, TESItems.mugAle, "GET_DRUNK");
		getPouch = new TESAchievement(Category.GENERAL, genId++, TESItems.pouch, "GET_POUCH");
		growBaobab = new TESAchievement(Category.GENERAL, genId++, new ItemStack(TESBlocks.sapling4, 1, 1), "GROW_BAOBAB");
		hundreds = new TESAchievement(Category.GENERAL, genId++, TESItems.gregorCleganeSword, "HUNDREDS");
		lightBeacon = new TESAchievement(Category.GENERAL, genId++, TESBlocks.beacon, "LIGHT_BEACON");
		pledgeService = new TESAchievement(Category.GENERAL, genId++, TESItems.gregorCleganeSword, "PLEDGE_SERVICE");
		reforge = new TESAchievement(Category.GENERAL, genId++, Blocks.anvil, "REFORGE");
		rideCamel = new TESAchievement(Category.GENERAL, genId++, Items.saddle, "RIDE_CAMEL");
		shootDownMidges = new TESAchievement(Category.GENERAL, genId++, TESItems.ironCrossbow, "SHOOT_DOWN_MIDGES");
		smeltObsidianShard = new TESAchievement(Category.GENERAL, genId++, TESItems.obsidianShard, "SMELT_OBSIDIAN_SHARD");
		speakToDrunkard = new TESAchievement(Category.GENERAL, genId++, TESItems.mugAle, "SPEAK_TO_DRUNKARD");
		steal = new TESAchievement(Category.GENERAL, genId++, TESItems.coin, "STEAL");
		stealArborGrapes = new TESAchievement(Category.GENERAL, genId++, TESItems.grapeRed, "STEAL_ARBOR_GRAPES");
		trade = new TESAchievement(Category.GENERAL, genId++, TESItems.coin, "TRADE");
		travel20 = new TESAchievement(Category.GENERAL, genId++, Items.map, "TRAVEL20");
		travel40 = new TESAchievement(Category.GENERAL, genId++, Items.map, "TRAVEL40");
		travel60 = new TESAchievement(Category.GENERAL, genId++, Items.map, "TRAVEL60");
		travel80 = new TESAchievement(Category.GENERAL, genId++, Items.map, "TRAVEL80");
		travel100 = new TESAchievement(Category.GENERAL, genId++, Items.map, "TRAVEL100");
		unsmelt = new TESAchievement(Category.GENERAL, genId++, TESBlocks.unsmeltery, "UNSMELT");
		useCrossbow = new TESAchievement(Category.GENERAL, genId++, TESItems.ironCrossbow, "USE_CROSSBOW");
		useSpearFromFar = new TESAchievement(Category.GENERAL, genId++, TESItems.ironSpear, "USE_SPEAR_FROM_FAR");
		useThrowingAxe = new TESAchievement(Category.GENERAL, genId++, TESItems.ironThrowingAxe, "USE_THROWING_AXE");
		
		int killId = 1;
		
		killThievingBandit = new TESAchievement(Category.KILL, killId++, TESItems.leatherHat, "KILL_THIEVING_BANDIT");
		killUlthos = new TESAchievement(Category.KILL, killId++, TESItems.mysteryWeb, "KILL_ULTHOS");
		killWhileDrunk = new TESAchievement(Category.KILL, killId++, TESItems.mugAle, "KILL_WHILE_DRUNK");
		killer = new TESAchievement(Category.KILL, killId++, Items.iron_axe, "KILLER");
		killBeaver = new TESAchievement(Category.KILL, killId++, TESItems.beaverTail, "KILL_BEAVER");
		killButterfly = new TESAchievement(Category.KILL, killId++, Items.iron_sword, "KILL_BUTTERFLY");
		killHuntingPlayer = new TESAchievement(Category.KILL, killId++, Items.iron_sword, "KILL_HUNTING_PLAYER");
		killLargeMobWithSlingshot = new TESAchievement(Category.KILL, killId++, TESItems.sling, "KILL_LARGE_MOB_WITH_SLINGSHOT");
		killMammoth = new TESAchievement(Category.KILL, killId++, TESItems.stoneSpear, "KILL_MAMMOTH");
		killProstitute = new TESAchievement(Category.KILL, killId++, TESItems.ironCrossbow, "KILL_PROSTITUTE");
		killUsingOnlyPlates = new TESAchievement(Category.KILL, killId++, TESItems.plate, "KILL_USING_ONLY_PLATES");
	}

	public static Comparator<TESAchievement> sortForDisplay(EntityPlayer entityplayer) {
		return (ach1, ach2) -> {
			if (ach1.isSpecial) {
				if (!ach2.isSpecial) {
					return -1;
				}
				return Integer.compare(ach1.id, ach2.id);
			}
			if (ach2.isSpecial) {
				return 1;
			}
			if (ach1.isBiomeAchievement) {
				if (ach2.isBiomeAchievement) {
					return ach1.getTitle(entityplayer).compareTo(ach2.getTitle(entityplayer));
				}
				return -1;
			}
			if (!ach2.isBiomeAchievement) {
				return ach1.getTitle(entityplayer).compareTo(ach2.getTitle(entityplayer));
			}
			return 1;
		};
	}

	public void broadcastEarning(EntityPlayer entityplayer) {
		IChatComponent earnName = getChatComponentForEarn(entityplayer);
		IChatComponent msg = new ChatComponentTranslation("TES.chat.achievement", entityplayer.func_145748_c_(), earnName);
		MinecraftServer.getServer().getConfigurationManager().sendChatMsg(msg);
	}

	public boolean canPlayerEarn(EntityPlayer entityplayer) {
		float alignment;
		TESPlayerData playerData = TESLevelData.getData(entityplayer);
		if (!allyFactions.isEmpty()) {
			boolean anyAllies = false;
			for (TESFaction f : allyFactions) {
				alignment = playerData.getAlignment(f);
				if (alignment < 0.0f) {
					continue;
				}
				anyAllies = true;
			}
			return anyAllies;
		}
		return true;
	}

	private TESAchievement createTitle() {
		return createTitle(null);
	}

	private TESAchievement createTitle(String s) {
		if (achievementTitle != null) {
			throw new IllegalArgumentException("TES achievement " + name + " already has an associated title!");
		}
		achievementTitle = new TESTitle(s, this);
		return this;
	}

	public IChatComponent getAchievementChatComponent(EntityPlayer entityplayer) {
		ChatComponentTranslation component = new ChatComponentTranslation(getUntranslatedTitle(entityplayer)).createCopy();
		component.getChatStyle().setColor(EnumChatFormatting.YELLOW);
		component.getChatStyle().setChatHoverEvent(new HoverEvent(TESChatEvents.showGotAchievement, new ChatComponentText(category.name() + '$' + id)));
		return component;
	}

	private IChatComponent getChatComponentForEarn(EntityPlayer entityplayer) {
		IChatComponent base = getAchievementChatComponent(entityplayer);
		IChatComponent component = new ChatComponentText("[").appendSibling(base).appendText("]");
		component.setChatStyle(base.getChatStyle());
		return component;
	}

	public String getDescription() {
		return StatCollector.translateToLocal("TES.achievement." + name + ".desc");
	}

	public TESDimension getDimension() {
		return category.getDimension();
	}

	public String getTitle(EntityPlayer entityplayer) {
		return StatCollector.translateToLocal(getUntranslatedTitle(entityplayer));
	}

	public String getTitle() {
		return StatCollector.translateToLocal("TES.achievement." + name + ".title");
	}

	public String getUntranslatedTitle(EntityPlayer entityplayer) {
		return "TES.achievement." + name + ".title";
	}

	public void setRequiresAlly(TESFaction... f) {
		allyFactions.addAll(Arrays.asList(f));
	}

	public Category getCategory() {
		return category;
	}

	public int getId() {
		return id;
	}

	public ItemStack getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}

	public boolean isBiomeAchievement() {
		return isBiomeAchievement;
	}

	private TESAchievement setBiomeAchievement() {
		isBiomeAchievement = true;
		return this;
	}

	public void setAchievementTitle(TESTitle achievementTitle) {
		this.achievementTitle = achievementTitle;
	}

	public enum Category { 
		TITLES(TESFaction.EMPIRE), GENERAL(TESFaction.EMPIRE), KILL(TESFaction.EMPIRE), WEAR(TESFaction.EMPIRE), ENTER(TESFaction.EMPIRE), LEGENDARY(TESFaction.EMPIRE);

		private final Collection<TESAchievement> list = new ArrayList<>();
		private final String codeName;
		private final float[] categoryColors;
		private final TESDimension dimension;

		private int nextRankAchID = 1000;

		Category(TESFaction faction) {
			this(faction.getFactionColor());
		}

		Category(int color) {
			this(TESDimension.GAME_OF_THRONES, color);
		}

		Category(TESDimension dim, int color) {
			codeName = name();
			categoryColors = new Color(color).getColorComponents(null);
			dimension = dim;
			dimension.getAchievementCategories().add(this);
		}

		private String codeName() {
			return codeName;
		}

		public float[] getCategoryRGB() {
			return categoryColors;
		}

		public String getDisplayName() {
			return StatCollector.translateToLocal("TES.achievement.category." + codeName());
		}

		public int getNextRankAchID() {
			++nextRankAchID;
			return nextRankAchID;
		}

		public Collection<TESAchievement> getList() {
			return list;
		}

		public TESDimension getDimension() {
			return dimension;
		}
	}
}