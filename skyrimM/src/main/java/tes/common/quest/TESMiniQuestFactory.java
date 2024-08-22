package tes.common.quest;

import cpw.mods.fml.common.FMLLog;
import tes.common.TESLore;
import tes.common.database.TESAchievement;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import tes.common.entity.animal.TESEntityCrocodile;
import tes.common.entity.other.TESEntityNPC;
import tes.common.faction.TESFaction;
import tes.common.item.other.TESItemBanner;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.*;

public enum TESMiniQuestFactory {
	CRIMINAL(true), IBBEN(true), SUMMER(true), SOTHORYOS(true), ASSHAI(true), WILDLING(true), MOSSOVY(true), HOWLAND, BALON, DAENERYS, VARYS, OBERYN, STANNIS, JONSNOW, RENLY, KITRA, BUGAI, TYRION, CERSEI, RAMSAY, SANDOR, MELISANDRA, DORAN, MARGAERY, ELLARYA, ARYA, OLENNA, SAMWELL, LYSA, CATELYN, DAVEN, ARIANNE, MELLARIO, NORTH(true), RIVERLANDS(true), DORNE(true), REACH(true), STORMLANDS(true), IRONBORN(true), WESTERLANDS(true), ARRYN(true), CROWNLANDS(true), DRAGONSTONE(true), GIFT(true), HILLMEN(true), BRAAVOS(true), LORATH(true), NORVOS(true), QOHOR(true), PENTOS(true), LYS(true), MYR(true), TYROSH(true), VOLANTIS(true), GHISCAR(true), QARTH(true), LHAZAR(true), YI_TI(true), DOTHRAKI(true), JOGOS(true);

	private static final Map<Class<? extends TESMiniQuest>, Integer> QUEST_CLASS_WEIGHTS = new HashMap<>();
	private static final Random RANDOM = new Random();

	private final Map<Class<? extends TESMiniQuest>, List<TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest>>> questFactories = new HashMap<>();
	private final List<TESLore.LoreCategory> loreCategories = new ArrayList<>();
	private final String baseName;

	private boolean noAlignRewardForEnemy;
	private TESMiniQuestFactory baseSpeechGroup;
	private TESAchievement questAchievement;
	private TESFaction alignmentRewardOverride;

	TESMiniQuestFactory() {
		this(false);
	}

	TESMiniQuestFactory(boolean isStandard) {
		if (isStandard) {
			baseName = "standard";
			setAchievement(TESAchievement.doQuest);
		} else {
			baseName = "legendary";
			setAchievement(TESAchievement.doLegendaryQuest);
		}
	}

	public static TESMiniQuestFactory forName(String name) {
		for (TESMiniQuestFactory group : values()) {
			if (group.baseName.equals(name)) {
				return group;
			}
		}
		return null;
	}

	private static int getQuestClassWeight(Class<? extends TESMiniQuest> questClass) {
		Integer i = QUEST_CLASS_WEIGHTS.get(questClass);
		if (i == null) {
			throw new RuntimeException("Encountered a registered quest class " + questClass.toString() + " which is not assigned a weight");
		}
		return i;
	}

	private static TESFaction getRandomEnemy(TESFaction owner) {
		ArrayList<TESFaction> enemies = new ArrayList<>();
		for (TESFaction fac : TESFaction.values()) {
		}
		return enemies.get(RANDOM.nextInt(enemies.size()));
	}

	private static int getTotalQuestClassWeight(TESMiniQuestFactory factory) {
		Collection<Class<? extends TESMiniQuest>> registeredQuestTypes = new HashSet<>();
		for (Map.Entry<Class<? extends TESMiniQuest>, List<TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest>>> entry : factory.questFactories.entrySet()) {
			Class<? extends TESMiniQuest> questType = entry.getKey();
			registeredQuestTypes.add(questType);
		}
		int totalWeight = 0;
		for (Class<? extends TESMiniQuest> c : registeredQuestTypes) {
			totalWeight += getQuestClassWeight(c);
		}
		return totalWeight;
	}

	public static void onInit() {
		registerQuestClass(TESMiniQuestCollect.class, 5);
		registerQuestClass(TESMiniQuestKill.class, 2);
		registerQuestClass(TESMiniQuestBounty.class, 2);
		registerQuestClass(TESMiniQuestPickpocket.class, 1);
		/*/
		BALON.addQuest(new TESMiniQuestKillEntity.QFKillEntity("balon").setKillEntity(TESEntityEuronGreyjoy.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		VARYS.addQuest(new TESMiniQuestKillEntity.QFKillEntity("varys").setKillEntity(TESEntityDaenerysTargaryen.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		OBERYN.addQuest(new TESMiniQuestKillEntity.QFKillEntity("oberyn").setKillEntity(TESEntityGeroldDayne.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		STANNIS.addQuest(new TESMiniQuestKillEntity.QFKillEntity("stannis").setKillEntity(TESEntityRenlyBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		RENLY.addQuest(new TESMiniQuestKillEntity.QFKillEntity("renly").setKillEntity(TESEntityStannisBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		BUGAI.addQuest(new TESMiniQuestKillEntity.QFKillEntity("bugai").setKillEntity(TESEntityTugarKhan.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		MARGAERY.addQuest(new TESMiniQuestKillEntity.QFKillEntity("margaery").setKillEntity(TESEntityCerseiLannister.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		HOWLAND.addQuest(new TESMiniQuestKillEntity.QFKillEntity("howland").setKillEntity(TESEntityCrocodile.class, 50, 50).setRewardFactor(50.0f).setIsLegendary());
		OLENNA.addQuest(new TESMiniQuestKillEntity.QFKillEntity("olenna").setKillEntity(TESEntityJoffreyBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		ELLARYA.addQuest(new TESMiniQuestKillEntity.QFKillEntity("ellarya").setKillEntity(TESEntityTywinLannister.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		LYSA.addQuest(new TESMiniQuestKillEntity.QFKillEntity("lysa").setKillEntity(TESEntityTyrionLannister.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		CATELYN.addQuest(new TESMiniQuestKillEntity.QFKillEntity("catelyn").setKillEntity(TESEntityTheonGreyjoy.TheonGreyjoyNormal.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		DAVEN.addQuest(new TESMiniQuestKillEntity.QFKillEntity("daven").setKillEntity(TESEntityRickardKarstark.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		ARIANNE.addQuest(new TESMiniQuestKillEntity.QFKillEntity("arianne").setKillEntity(TESEntityTommenBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		MELLARIO.addQuest(new TESMiniQuestKillEntity.QFKillEntity("mellario").setKillEntity(TESEntityDoranMartell.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		MELISANDRA.addQuest(new TESMiniQuestCollect.QFCollect<>("melisandra").setCollectItem(new ItemStack(TESItems.bloodOfTrueKings), 1, 1).setRewardFactor(100.0f).setIsLegendary());
		TYRION.addQuest(new TESMiniQuestCollect.QFCollect<>("tyrion").setCollectItem(new ItemStack(TESItems.mugRedWine), 1, 10).setRewardFactor(5.0f).setIsLegendary());
		CERSEI.addQuest(new TESMiniQuestCollect.QFCollect<>("cersei").setCollectItem(new ItemStack(TESBlocks.wildFireJar), 1, 10).setRewardFactor(0.0f).setIsLegendary());
		RAMSAY.addQuest(new TESMiniQuestCollect.QFCollect<>("ramsay").setCollectItem(new ItemStack(TESItems.brandingIron), 1, 1).setRewardFactor(5.0f).setIsLegendary());
		DAENERYS.addQuest(new TESMiniQuestCollect.QFCollect<>("daenerys").setCollectItem(new ItemStack(Blocks.dragon_egg), 3, 3).setRewardFactor(100.0f).setIsLegendary());
		JONSNOW.addQuest(new TESMiniQuestCollect.QFCollect<>("jonsnow").setCollectItem(new ItemStack(TESItems.valyrianSword), 1, 1).setHiring());
		SANDOR.addQuest(new TESMiniQuestCollect.QFCollect<>("sandor").setCollectItem(new ItemStack(Items.cooked_chicken), 1, 10).setHiring());
		DORAN.addQuest(new TESMiniQuestCollect.QFCollect<>("doran").setCollectItem(new ItemStack(TESItems.mugPoppyMilk), 1, 10).setRewardFactor(5.0f).setIsLegendary());
		ARYA.addQuest(new TESMiniQuestCollect.QFCollect<>("arya").setCollectItem(new ItemStack(Items.wooden_sword), 1, 1).setHiring());
		SAMWELL.addQuest(new TESMiniQuestCollect.QFCollect<>("samwell").setCollectItem(new ItemStack(TESItems.sothoryosDagger), 1, 1).setRewardFactor(50.0f).setIsLegendary());

		Map<TESMiniQuestFactory, TESFaction> kingdoms = new EnumMap<>(TESMiniQuestFactory.class);
		kingdoms.put(NORTH, TESFaction.NORTH);
		kingdoms.put(RIVERLANDS, TESFaction.RIVERLANDS);
		kingdoms.put(WESTERLANDS, TESFaction.WESTERLANDS);
		kingdoms.put(IRONBORN, TESFaction.IRONBORN);
		kingdoms.put(ARRYN, TESFaction.ARRYN);
		kingdoms.put(CROWNLANDS, TESFaction.CROWNLANDS);
		kingdoms.put(DRAGONSTONE, TESFaction.DRAGONSTONE);
		kingdoms.put(STORMLANDS, TESFaction.STORMLANDS);
		kingdoms.put(REACH, TESFaction.REACH);
		kingdoms.put(DORNE, TESFaction.DORNE);
		kingdoms.put(GIFT, TESFaction.NIGHT_WATCH);

		Map<TESMiniQuestFactory, TESFaction> cities = new EnumMap<>(TESMiniQuestFactory.class);
		cities.put(BRAAVOS, TESFaction.BRAAVOS);
		cities.put(LORATH, TESFaction.LORATH);
		cities.put(NORVOS, TESFaction.NORVOS);
		cities.put(QOHOR, TESFaction.QOHOR);
		cities.put(PENTOS, TESFaction.PENTOS);
		cities.put(MYR, TESFaction.MYR);
		cities.put(LYS, TESFaction.LYS);
		cities.put(TYROSH, TESFaction.TYROSH);
		cities.put(VOLANTIS, TESFaction.VOLANTIS);
		cities.put(GHISCAR, TESFaction.GHISCAR);
		cities.put(QARTH, TESFaction.QARTH);

		for (Map.Entry<TESMiniQuestFactory, TESFaction> kingdom : kingdoms.entrySet()) {
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 5, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugAle), 5, 10).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCider), 5, 10).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugMead), 5, 10).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugRum), 5, 10).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 5, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitCooked), 5, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 5, 20).setRewardFactor(1.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 5, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_chicken), 5, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_porkchop), 5, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.planks, 1, 0), 50, 100).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.planks, 1, 1), 50, 100).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.stonebrick), 50, 100).setRewardFactor(0.5f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 0), 6, 15).setRewardFactor(1.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 12), 6, 15).setRewardFactor(1.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 14), 6, 15).setRewardFactor(1.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 15), 6, 15).setRewardFactor(1.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood2, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood2, 1, 1), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood4, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood6, 1, 1), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.chisel), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironBattleaxe), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironDagger), 1, 5).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironPike), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironThrowingAxe), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosBow), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosDagger), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosDaggerPoisoned), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosHammer), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosHorseArmor), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosPike), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosSpear), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.westerosSword), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.arrow), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bucket), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_axe), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_boots), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_chestplate), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_helmet), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_hoe), 1, 5).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_ingot), 1, 5).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_leggings), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_shovel), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_sword), 1, 5).setRewardFactor(3.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.shears), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.cobblestone), 30, 80).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.gold_ore), 10, 5).setRewardFactor(5.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.iron_ore), 10, 20).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.oreCopper), 10, 5).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.oreTin), 10, 5).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.rock, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.rock, 1, 1), 20, 50).setRewardFactor(0.25f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.copperIngot), 1, 5).setRewardFactor(2.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.silverIngot), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.tinIngot), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.coal), 20, 50).setRewardFactor(0.5f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.gold_ingot), 1, 5).setRewardFactor(4.0f));
			kingdom.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.leather), 5, 20).setRewardFactor(0.5f));
			kingdom.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(kingdom.getValue()), 10, 30));
			kingdom.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(kingdom.getValue()), 30, 50));
			kingdom.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(kingdom.getValue()), 50, 70));
			kingdom.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(kingdom.getValue()), 70, 100));
			kingdom.getKey().addQuest(new TESMiniQuestBounty.QFBounty<>());
		}
		for (Map.Entry<TESMiniQuestFactory, TESFaction> city : cities.entrySet()) {
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.date), 8, 15).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 5, 10).setRewardFactor(1.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.kebab), 4, 8).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lemon), 4, 12).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lime), 4, 12).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lionCooked), 2, 4).setRewardFactor(4.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugAraq), 3, 5).setRewardFactor(4.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugBananaBeer), 4, 10).setRewardFactor(2.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCactusLiqueur), 4, 10).setRewardFactor(2.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCarrotWine), 4, 10).setRewardFactor(2.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCornLiquor), 4, 10).setRewardFactor(2.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugRum), 4, 10).setRewardFactor(2.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 4, 8).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.orange), 4, 12).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.plum), 4, 12).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitCooked), 5, 10).setRewardFactor(1.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.waterskin), 8, 20).setRewardFactor(0.75f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 4, 8).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_porkchop), 4, 8).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.chest), 8, 16).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 0), 6, 15).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 12), 6, 15).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 14), 6, 15).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.wool, 1, 15), 6, 15).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.brick1, 1, 15), 30, 60).setRewardFactor(0.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.brick3, 1, 13), 30, 60).setRewardFactor(0.75f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.brick6, 1, 6), 30, 60).setRewardFactor(0.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.chestBasket), 5, 10).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.driedReeds), 10, 20).setRewardFactor(0.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks2, 1, 11), 60, 120).setRewardFactor(0.2f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks2, 1, 2), 60, 120).setRewardFactor(0.125f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks2, 1, 2), 60, 120).setRewardFactor(0.2f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks3, 1, 3), 60, 120).setRewardFactor(0.2f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.pouch, 1, 0), 3, 5).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.rope), 5, 12).setRewardFactor(1.1f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.rope), 5, 12).setRewardFactor(1.1f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.slabSingleThatch, 1, 1), 20, 40).setRewardFactor(0.25f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.thatch, 1, 1), 10, 20).setRewardFactor(0.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood4, 1, 2), 30, 60).setRewardFactor(0.25f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.string), 5, 12).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 2, 4).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.essosDagger), 1, 2).setRewardFactor(20.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.essosHammer), 1, 3).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.essosPike), 1, 1).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.essosSpear), 1, 3).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.essosSword), 1, 3).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.sandstone, 1, 0), 30, 80).setRewardFactor(0.25f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.stone, 1, 0), 30, 80).setRewardFactor(0.25f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.rock, 1, 0), 30, 50).setRewardFactor(0.5f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.gold_ingot), 3, 6).setRewardFactor(4.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_ingot), 4, 8).setRewardFactor(2.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.lava_bucket), 2, 4).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.leather), 10, 20).setRewardFactor(0.75f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.string), 5, 12).setRewardFactor(1.0f));
			city.getKey().addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.water_bucket), 3, 5).setRewardFactor(5.0f));
			city.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(city.getValue()), 10, 30));
			city.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(city.getValue()), 30, 50));
			city.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(city.getValue()), 50, 70));
			city.getKey().addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(city.getValue()), 70, 100));
			city.getKey().addQuest(new TESMiniQuestBounty.QFBounty<>());
		}
		ASSHAI.addQuest(new TESMiniQuestBounty.QFBounty<>());
		ASSHAI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.asshaiFlower), 6, 15).setRewardFactor(1.0f));
		ASSHAI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 6, 15).setRewardFactor(1.0f));

		CRIMINAL.addQuest(new TESMiniQuestPickpocket.QFPickpocket<>("criminal").setPickpocketNumber(1, 9).setIsLegendary());

		WILDLING.addQuest(new TESMiniQuestBounty.QFBounty<>());
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_boots), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_chestplate), 2, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_helmet), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_leggings), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.furChestplate), 2, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.furHelmet), 2, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.furBoots), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.furLeggings), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeChestplate), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeHelmet), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeBoots), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeLeggings), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_chicken), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_porkchop), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeIngot), 3, 10).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.copperIngot), 3, 10).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_ingot), 3, 10).setRewardFactor(2.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugEthanol), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeThrowingAxe), 3, 6).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironThrowingAxe), 3, 6).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.silverIngot), 3, 6).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.gold_ingot), 3, 6).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.wildlingHammer), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeBattleaxe), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironBattleaxe), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ironPike), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.wildlingPolearm), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.wildlingSword), 1, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeSword), 1, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_sword), 1, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.WILDLING), 10, 30));
		WILDLING.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.WILDLING), 30, 50));
		WILDLING.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.WILDLING), 50, 70));
		WILDLING.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.WILDLING), 70, 100));

		DOTHRAKI.addQuest(new TESMiniQuestBounty.QFBounty<>());
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 1), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 10), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 11), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 13), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 14), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 3), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 5), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.date), 8, 15).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lemon), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lime), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.orange), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.plum), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 2, 4).setRewardFactor(5.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.dothrakiChestplate), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.dothrakiHelmet), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.nomadSword), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.dothrakiBoots), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.dothrakiLeggings), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.nomadBattleaxe), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.DOTHRAKI), 10, 30));
		DOTHRAKI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.DOTHRAKI), 30, 50));
		DOTHRAKI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.DOTHRAKI), 50, 70));
		DOTHRAKI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.DOTHRAKI), 70, 100));

		HILLMEN.addQuest(new TESMiniQuestBounty.QFBounty<>());
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.cobblestone), 30, 80).setRewardFactor(0.25f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 0), 30, 80).setRewardFactor(0.25f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 1), 30, 80).setRewardFactor(0.25f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.hillmenBoots), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.hillmenChestplate), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.hillmenHelmet), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.hillmenLeggings), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugAle), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCider), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugMead), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugRum), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitCooked), 3, 12).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 5, 15).setRewardFactor(1.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.coal), 10, 30).setRewardFactor(0.5f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_chicken), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_porkchop), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_ingot), 3, 10).setRewardFactor(1.5f));
		HILLMEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.leather), 10, 30).setRewardFactor(0.5f));
		HILLMEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 10, 30));
		HILLMEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 30, 50));
		HILLMEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 50, 70));
		HILLMEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 70, 100));

		IBBEN.addQuest(new TESMiniQuestBounty.QFBounty<>());
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ibbenBoots), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ibbenChestplate), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ibbenHarpoon), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ibbenLeggings), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.ibbenSword), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_ingot), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitCooked), 3, 12).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 5, 15).setRewardFactor(1.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_chicken), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_porkchop), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugMead), 3, 20).setRewardFactor(1.0f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.cobblestone), 30, 80).setRewardFactor(0.25f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 0), 20, 60).setRewardFactor(0.25f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 1), 20, 60).setRewardFactor(0.25f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.planks, 1, 0), 80, 160).setRewardFactor(0.125f));
		IBBEN.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.planks, 1, 1), 80, 160).setRewardFactor(0.125f));
		IBBEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.IBBEN), 10, 30));
		IBBEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.IBBEN), 30, 50));
		IBBEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.IBBEN), 50, 70));
		IBBEN.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.IBBEN), 70, 100));

		JOGOS.addQuest(new TESMiniQuestBounty.QFBounty<>());
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 1), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 10), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 11), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 13), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 14), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 3), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.carpet, 5), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.date), 8, 15).setRewardFactor(2.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lemon), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lime), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.orange), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.plum), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 2, 4).setRewardFactor(5.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.jogosChestplate), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.jogosHelmet), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.nomadSword), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.jogosBoots), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.jogosLeggings), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.nomadBow), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.JOGOS), 10, 30));
		JOGOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.JOGOS), 30, 50));
		JOGOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.JOGOS), 50, 70));
		JOGOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.JOGOS), 70, 100));

		LHAZAR.addQuest(new TESMiniQuestBounty.QFBounty<>());
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.date), 8, 15).setRewardFactor(2.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarClub), 2, 3).setRewardFactor(6.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarSpear), 2, 3).setRewardFactor(4.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarSword), 2, 3).setRewardFactor(5.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 2, 4).setRewardFactor(5.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarDagger), 1, 2).setRewardFactor(20.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarBoots), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarChestplate), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarHelmet), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lhazarLeggings), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.sandstone, 1, 0), 30, 80).setRewardFactor(0.25f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.boneBlock, 1, 0), 5, 10).setRewardFactor(2.0f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.brick1, 1, 15), 30, 60).setRewardFactor(0.5f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.brick3, 1, 13), 30, 60).setRewardFactor(0.5f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks3, 1, 3), 60, 120).setRewardFactor(0.125f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks3, 1, 4), 60, 120).setRewardFactor(0.125f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.thatch, 1, 1), 10, 20).setRewardFactor(0.5f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood8, 1, 3), 30, 60).setRewardFactor(0.25f));
		LHAZAR.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood9, 1, 0), 30, 60).setRewardFactor(0.25f));
		LHAZAR.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.LHAZAR), 10, 30));
		LHAZAR.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.LHAZAR), 30, 50));
		LHAZAR.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.LHAZAR), 50, 70));
		LHAZAR.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.LHAZAR), 70, 100));

		MOSSOVY.addQuest(new TESMiniQuestBounty.QFBounty<>());
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bucket), 1, 4).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugAle), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCherryLiqueur), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCider), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugMead), 1, 6).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugPerry), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.appleCrumble), 2, 5).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.appleGreen), 3, 12).setRewardFactor(1.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.cherryPie), 2, 5).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugAle), 5, 15).setRewardFactor(1.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugCider), 5, 15).setRewardFactor(1.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.pear), 3, 12).setRewardFactor(1.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitCooked), 3, 15).setRewardFactor(1.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitStew), 3, 8).setRewardFactor(2.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.apple), 3, 12).setRewardFactor(1.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.baked_potato), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_chicken), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_porkchop), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 0), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood2, 1, 1), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood3, 1, 0), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood4, 1, 0), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood6, 1, 1), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mossovyBoots), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mossovyChestplate), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mossovyLeggings), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mossovySword), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeAxe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeHoe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bronzeShovel), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.chisel), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bucket), 1, 4).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_axe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_hoe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.iron_shovel), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.pipeweed), 20, 40).setRewardFactor(0.25f));
		MOSSOVY.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 10, 30));
		MOSSOVY.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 30, 50));
		MOSSOVY.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 50, 70));
		MOSSOVY.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.MOSSOVY), 70, 100));

		SOTHORYOS.addQuest(new TESMiniQuestBounty.QFBounty<>());
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.banana), 4, 6).setRewardFactor(4.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bananaBread), 5, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.banner, 1, TESItemBanner.BannerType.SOTHORYOS.getBannerID()), 5, 15).setRewardFactor(1.5f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.corn), 6, 12).setRewardFactor(1.5f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.cornBread), 5, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.cornCooked), 5, 10).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.dart), 20, 40).setRewardFactor(0.5f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.dartPoisoned), 10, 20).setRewardFactor(1.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mango), 4, 6).setRewardFactor(4.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.melonSoup), 3, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.obsidianShard), 10, 30).setRewardFactor(0.75f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosAxe), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosBattleaxe), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosDagger), 1, 4).setRewardFactor(4.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosDaggerPoisoned), 1, 3).setRewardFactor(6.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosHammer), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosPike), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosSpear), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sothoryosSword), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 5, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.dye, 1, 3), 8, 20).setRewardFactor(1.0f));
		SOTHORYOS.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.melon), 10, 20).setRewardFactor(0.75f));
		SOTHORYOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SOTHORYOS), 10, 30));
		SOTHORYOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SOTHORYOS), 30, 50));
		SOTHORYOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SOTHORYOS), 50, 70));
		SOTHORYOS.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SOTHORYOS), 70, 100));

		SUMMER.addQuest(new TESMiniQuestBounty.QFBounty<>());
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.water_bucket), 3, 5).setRewardFactor(5.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 0), 20, 60).setRewardFactor(0.25f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.planks, 1, 0), 80, 160).setRewardFactor(0.125f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.sandstone, 1, 0), 30, 80).setRewardFactor(0.25f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.sandstone, 1, 1), 15, 40).setRewardFactor(0.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.planks2, 1, 2), 80, 160).setRewardFactor(0.125f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.thatch, 1, 1), 20, 40).setRewardFactor(0.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood4, 1, 2), 20, 60).setRewardFactor(0.25f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.rock, 1, 0), 30, 50).setRewardFactor(0.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.date), 8, 15).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lemon), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lime), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.orange), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.plum), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 5, 12).setRewardFactor(1.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lemon), 4, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.orange), 4, 8).setRewardFactor(2.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitCooked), 5, 12).setRewardFactor(1.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 5, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 2, 4).setRewardFactor(5.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.arrow), 20, 40).setRewardFactor(0.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.doubleFlower, 1, 3), 5, 15).setRewardFactor(1.5f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lionFur), 3, 6).setRewardFactor(3.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.olive), 10, 20).setRewardFactor(1.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.dye, 1, 4), 3, 8).setRewardFactor(3.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.banana), 2, 4).setRewardFactor(4.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lemon), 4, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.lionCooked), 3, 6).setRewardFactor(3.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mango), 2, 4).setRewardFactor(4.0f));
		SUMMER.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.orange), 4, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SUMMER_ISLANDS), 10, 30));
		SUMMER.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SUMMER_ISLANDS), 30, 50));
		SUMMER.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SUMMER_ISLANDS), 50, 70));
		SUMMER.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.SUMMER_ISLANDS), 70, 100));

		YI_TI.addQuest(new TESMiniQuestBounty.QFBounty<>());
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.date), 4, 12).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.pomegranate), 4, 12).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.sapling8, 1, 1), 3, 5).setRewardFactor(3.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.yitiFlower, 1, 0), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.yitiFlower, 1, 1), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.yitiFlower, 1, 2), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.yitiFlower, 1, 3), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.yitiFlower, 1, 4), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerCooked), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.deerRaw), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugRedWine), 3, 8).setRewardFactor(3.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.mugWhiteWine), 3, 8).setRewardFactor(3.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.muttonCooked), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.oliveBread), 5, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.rabbitRaw), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.raisins), 6, 12).setRewardFactor(1.5f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.whiteBisonHorn), 1, 2).setRewardFactor(10.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.beef), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.bread), 5, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_beef), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.cooked_fished), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.feather), 8, 16).setRewardFactor(1.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Items.leather), 8, 16).setRewardFactor(1.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.bottlePoison), 2, 4).setRewardFactor(5.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sulfur), 4, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiBoots), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiChestplate), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiHelmet), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiLeggings), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiSpear), 3, 5).setRewardFactor(3.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiSteelIngot), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.yitiSword), 3, 5).setRewardFactor(3.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.cobblestone, 1, 0), 40, 100).setRewardFactor(0.25f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(Blocks.log, 1, 0), 30, 60).setRewardFactor(0.25f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.brick5, 1, 11), 40, 100).setRewardFactor(0.2f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.saltpeter), 4, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.sulfur), 4, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESItems.whiteBisonHorn), 1, 1).setRewardFactor(30.0f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood6, 1, 2), 30, 60).setRewardFactor(0.25f));
		YI_TI.addQuest(new TESMiniQuestCollect.QFCollect<>().setCollectItem(new ItemStack(TESBlocks.wood8, 1, 1), 30, 60).setRewardFactor(0.25f));
		YI_TI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.YI_TI), 10, 30));
		YI_TI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.YI_TI), 30, 50));
		YI_TI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.YI_TI), 50, 70));
		YI_TI.addQuest(new TESMiniQuestKillFaction.QFKillFaction().setKillFaction(getRandomEnemy(TESFaction.YI_TI), 70, 100));
	/*/
	}

	private static void registerQuestClass(Class<? extends TESMiniQuest> questClass, int weight) {
		QUEST_CLASS_WEIGHTS.put(questClass, weight);
	}

	private void addQuest(TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest> factory) {
		Class<?> questClass = factory.getQuestClass();
		Class<? extends TESMiniQuest> registryClass = null;
		for (Class<? extends TESMiniQuest> c : QUEST_CLASS_WEIGHTS.keySet()) {
			if (questClass == c) {
				registryClass = c;
				break;
			}
		}
		if (registryClass == null) {
			for (Class<? extends TESMiniQuest> c : QUEST_CLASS_WEIGHTS.keySet()) {
				if (c.isAssignableFrom(questClass)) {
					registryClass = c;
					break;
				}
			}
		}
		if (registryClass == null) {
			throw new IllegalArgumentException("Could not find registered quest class for " + questClass.toString());
		}
		factory.setFactoryGroup(this);
		questFactories.computeIfAbsent(registryClass, k -> new ArrayList<>()).add(factory);
	}

	public TESFaction checkAlignmentRewardFaction(TESFaction fac) {
		if (alignmentRewardOverride != null) {
			return alignmentRewardOverride;
		}
		return fac;
	}

	public TESMiniQuest createQuest(TESEntityNPC npc) {
		int totalWeight = getTotalQuestClassWeight(this);
		if (totalWeight <= 0) {
			FMLLog.warning("Hummel009: No quests registered for %s!", baseName);
			return null;
		}
		int i = RANDOM.nextInt(totalWeight);
		List<TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest>> chosenFactoryList = null;
		for (Map.Entry<Class<? extends TESMiniQuest>, List<TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest>>> next : questFactories.entrySet()) {
			chosenFactoryList = next.getValue();
			i -= getQuestClassWeight(next.getKey());
			if (i < 0) {
				break;
			}
		}
		TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest> factory = chosenFactoryList.get(RANDOM.nextInt(chosenFactoryList.size()));
		TESMiniQuest quest = factory.createQuest(npc, RANDOM);
		if (quest != null) {
			quest.setQuestGroup(this);
		}
		return quest;
	}

	public TESAchievement getAchievement() {
		return questAchievement;
	}

	private void setAchievement(TESAchievement a) {
		if (questAchievement != null) {
			throw new IllegalArgumentException("Miniquest achievement is already registered");
		}
		questAchievement = a;
	}

	public String getBaseName() {
		return baseName;
	}

	public TESMiniQuestFactory getBaseSpeechGroup() {
		if (baseSpeechGroup != null) {
			return baseSpeechGroup;
		}
		return this;
	}

	public void setBaseSpeechGroup(TESMiniQuestFactory baseSpeechGroup) {
		this.baseSpeechGroup = baseSpeechGroup;
	}

	public List<TESLore.LoreCategory> getLoreCategories() {
		return loreCategories;
	}

	public boolean isNoAlignRewardForEnemy() {
		return noAlignRewardForEnemy;
	}

	public void setNoAlignRewardForEnemy(boolean noAlignRewardForEnemy) {
		this.noAlignRewardForEnemy = noAlignRewardForEnemy;
	}

	public Map<Class<? extends TESMiniQuest>, List<TESMiniQuest.QuestFactoryBase<? extends TESMiniQuest>>> getQuestFactories() {
		return questFactories;
	}

	public void setQuestAchievement(TESAchievement questAchievement) {
		this.questAchievement = questAchievement;
	}

	public void setAlignmentRewardOverride(TESFaction alignmentRewardOverride) {
		this.alignmentRewardOverride = alignmentRewardOverride;
	}
}