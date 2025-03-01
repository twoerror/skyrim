package tes.common.util;

import tes.common.TESAchievementRank;
import tes.common.TESDate;
import tes.common.block.other.TESBlockOreGem;
import tes.common.block.other.TESBlockRock;
import tes.common.database.*;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.TESEntitySpiderBase;
import tes.common.entity.other.iface.*;
import tes.common.entity.other.utils.TESEntityUtils;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionRank;
import tes.common.item.other.TESItemBanner;
import tes.common.world.biome.TESBiome;
import tes.common.world.biome.TESBiomeDecorator;
import tes.common.world.biome.TESClimateType;
import tes.common.world.biome.variant.TESBiomeVariant;
import tes.common.world.biome.variant.TESBiomeVariantList;
import tes.common.world.feature.TESTreeType;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESFixer;
import tes.common.world.map.TESWaypoint;
import tes.common.world.spawning.TESFactionContainer;
import tes.common.world.spawning.TESSpawnEntry;
import tes.common.world.spawning.TESSpawnListContainer;
import tes.common.world.structure.TESStructureRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TESWikiGenerator {
	private static final Map<Class<? extends Entity>, Entity> ENTITY_CLASS_TO_ENTITY = new HashMap<>();
	private static final Map<Class<? extends Entity>, TESWaypoint> ENTITY_CLASS_TO_WP = new HashMap<Class<? extends Entity>, TESWaypoint>();

	private static final Map<TESFaction, String> FACTION_TO_PAGENAME = new EnumMap<>(TESFaction.class);
	private static final Map<Class<? extends Entity>, String> ENTITY_CLASS_TO_PAGENAME = new HashMap<>();
	private static final Map<TESBiome, String> BIOME_TO_PAGENAME = new HashMap<>();

	private static final Iterable<Item> ITEMS = new ArrayList<Item>(TESItems.CONTENT);
	private static final Iterable<TESUnitTradeEntries> UNIT_TRADE_ENTRIES = new ArrayList<TESUnitTradeEntries>(TESUnitTradeEntries.CONTENT);
	private static final Iterable<Class<? extends Entity>> ENTITY_CLASSES = new HashSet<Class<? extends Entity>>(TESEntityRegistry.CONTENT);
	private static final Iterable<Class<? extends WorldGenerator>> STRUCTURE_CLASSES = new HashSet<Class<? extends WorldGenerator>>(TESStructureRegistry.CONTENT);
	private static final Iterable<TESAchievement> ACHIEVEMENTS = new HashSet<TESAchievement>(TESAchievement.CONTENT);

	private static final Collection<TESBiome> BIOMES = new HashSet<TESBiome>(TESBiome.CONTENT);

	private static final Iterable<TESFaction> FACTIONS = EnumSet.allOf(TESFaction.class);
	private static final Iterable<TESTreeType> TREES = EnumSet.allOf(TESTreeType.class);
	private static final Iterable<TESWaypoint> WAYPOINTS = EnumSet.allOf(TESWaypoint.class);
	private static final Iterable<TESCapes> CAPES = EnumSet.allOf(TESCapes.class);
	private static final Iterable<TESShields> SHIELDS = EnumSet.allOf(TESShields.class);

	private static final Collection<String> MINERALS = new HashSet<String>();

	private static final String BEGIN = "</title>\n<ns>10</ns>\n<revision>\n<text>&lt;includeonly&gt;{{#switch: {{{1}}}";
	private static final String END = "\n}}&lt;/includeonly&gt;&lt;noinclude&gt;[[" + Lang.CATEGORY + "]]&lt;/noinclude&gt;</text>\n</revision>\n</page>\n";
	private static final String TITLE = "<page>\n<title>";
	private static final String TITLE_SINGLE = "<page><title>";
	private static final String PAGE_LEFT = "</title><revision><text>";
	private static final String PAGE_RIGHT = "</text></revision></page>\n";
	private static final String UTF_8 = "UTF-8";
	private static final String TEMPLATE = "Template:";
	private static final String NL = "\n";
	private static final String TRUE = "True";
	private static final String FALSE = "False";
	private static final String N_A = "N/A";

	static {
		BIOMES.remove(TESBiome.ocean1);
		BIOMES.remove(TESBiome.ocean2);
		BIOMES.remove(TESBiome.ocean3);
		BIOMES.remove(TESBiome.beach);
		BIOMES.remove(TESBiome.beachGravel);
		BIOMES.remove(TESBiome.beachWhite);
		BIOMES.remove(TESBiome.lake);
		BIOMES.remove(TESBiome.river);
		BIOMES.remove(TESBiome.island);
		BIOMES.remove(TESBiome.kingSpears);
		BIOMES.remove(TESBiome.bleedingBeach);
		BIOMES.remove(TESBiome.valyriaSea1);
		BIOMES.remove(TESBiome.valyriaSea2);
	}

	private TESWikiGenerator() {
	}

	public static void generate(Type type, World world, EntityPlayer entityPlayer) {
		long time = System.nanoTime();

		try {
			Files.createDirectories(Paths.get("hummel"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		switch (type) {
			case TABLES:
				Collection<Runnable> tableGens = new HashSet<>();

				tableGens.add(TESWikiGenerator::genTableShields);
				tableGens.add(TESWikiGenerator::genTableCapes);
				tableGens.add(TESWikiGenerator::genTableUnits);
				tableGens.add(TESWikiGenerator::genTableArmor);
				tableGens.add(TESWikiGenerator::genTableWeapons);
				tableGens.add(TESWikiGenerator::genTableFood);
				tableGens.add(() -> genTableAchievements(entityPlayer));
				tableGens.add(() -> genTableWaypoints(entityPlayer));

				tableGens.parallelStream().forEach(Runnable::run);

				break;
			case XML:
				try (PrintWriter printWriter = new PrintWriter("hummel/import.xml", UTF_8)) {
					StringBuilder sb = new StringBuilder();

					TESDate.Season season = TESDate.AegonCalendar.getSeason();

					sb.append("<mediawiki xmlns=\"http://www.mediawiki.org/xml/export-0.11/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mediawiki.org/xml/export-0.11/ http://www.mediawiki.org/xml/export-0.11.xsd\" version=\"0.11\" xml:lang=\"ru\">");

					Collection<Runnable> runnables = new HashSet<>();

					runnables.add(TESWikiGenerator::searchForMinerals);
					runnables.add(TESWikiGenerator::searchForPagenamesEntity);
					runnables.add(TESWikiGenerator::searchForPagenamesBiome);
					runnables.add(TESWikiGenerator::searchForPagenamesFaction);
					runnables.add(() -> searchForEntities(world));
					runnables.add(() -> searchForStructures(world));

					runnables.parallelStream().forEach(Runnable::run);

					Collection<Supplier<StringBuilder>> suppliers = new HashSet<>();

					Set<String> existingPages = getExistingPages();
					Collection<String> neededPages = new HashSet<>();

					suppliers.add(() -> addPagesMinerals(neededPages, existingPages));
					suppliers.add(() -> addPagesEntities(neededPages, existingPages));
					suppliers.add(() -> addPagesBiomes(neededPages, existingPages));
					suppliers.add(() -> addPagesFactions(neededPages, existingPages));
					suppliers.add(() -> addPagesTrees(neededPages, existingPages));
					suppliers.add(() -> addPagesStructures(neededPages, existingPages));

					suppliers.parallelStream().map(Supplier::get).forEach(sb::append);
					suppliers.clear();

					markPagesForRemoval(neededPages, existingPages);

					suppliers.add(TESWikiGenerator::genTemplateMineralBiomes);
					suppliers.add(TESWikiGenerator::genTemplateTreeBiomes);

					suppliers.add(TESWikiGenerator::genTemplateStructureBiomes);
					suppliers.add(TESWikiGenerator::genTemplateStructureEntities);

					suppliers.add(TESWikiGenerator::genTemplateBiomeAnimals);
					suppliers.add(TESWikiGenerator::genTemplateBiomeBandits);
					suppliers.add(TESWikiGenerator::genTemplateBiomeClimate);
					suppliers.add(TESWikiGenerator::genTemplateBiomeConquestFactions);
					suppliers.add(TESWikiGenerator::genTemplateBiomeInvasionFactions);
					suppliers.add(TESWikiGenerator::genTemplateBiomeMinerals);
					suppliers.add(TESWikiGenerator::genTemplateBiomeMusic);
					suppliers.add(TESWikiGenerator::genTemplateBiomeNPCs);
					suppliers.add(TESWikiGenerator::genTemplateBiomeName);
					suppliers.add(TESWikiGenerator::genTemplateBiomeRainfall);
					suppliers.add(TESWikiGenerator::genTemplateBiomeStructures);
					suppliers.add(TESWikiGenerator::genTemplateBiomeTemperature);
					suppliers.add(TESWikiGenerator::genTemplateBiomeTrees);
					suppliers.add(TESWikiGenerator::genTemplateBiomeVariants);
					suppliers.add(TESWikiGenerator::genTemplateBiomeVisitAchievement);
					suppliers.add(TESWikiGenerator::genTemplateBiomeWaypoints);

					suppliers.add(TESWikiGenerator::genTemplateFactionBanners);
					suppliers.add(TESWikiGenerator::genTemplateFactionCharacters);
					suppliers.add(TESWikiGenerator::genTemplateFactionCodename);
					suppliers.add(TESWikiGenerator::genTemplateFactionConquestBiomes);
					suppliers.add(TESWikiGenerator::genTemplateFactionEnemies);
					suppliers.add(TESWikiGenerator::genTemplateFactionFriends);
					suppliers.add(TESWikiGenerator::genTemplateFactionInvasionBiomes);
					suppliers.add(TESWikiGenerator::genTemplateFactionNPCs);
					suppliers.add(TESWikiGenerator::genTemplateFactionName);
					suppliers.add(TESWikiGenerator::genTemplateFactionPledgeRank);
					suppliers.add(TESWikiGenerator::genTemplateFactionRanks);
					suppliers.add(TESWikiGenerator::genTemplateFactionRegion);
					suppliers.add(TESWikiGenerator::genTemplateFactionShieldsCapes);
					suppliers.add(TESWikiGenerator::genTemplateFactionSpawnBiomes);
					suppliers.add(TESWikiGenerator::genTemplateFactionWarCrimes);
					suppliers.add(TESWikiGenerator::genTemplateFactionWaypoints);

					suppliers.add(TESWikiGenerator::genTemplateEntityBannerBearer);
					suppliers.add(TESWikiGenerator::genTemplateEntityBiomes);
					suppliers.add(TESWikiGenerator::genTemplateEntityBuyPools);
					suppliers.add(TESWikiGenerator::genTemplateEntityCharacter);
					suppliers.add(TESWikiGenerator::genTemplateEntityFaction);
					suppliers.add(TESWikiGenerator::genTemplateEntityFarmhand);
					suppliers.add(TESWikiGenerator::genTemplateEntityHealth);
					suppliers.add(TESWikiGenerator::genTemplateEntityHireAlignment);
					suppliers.add(TESWikiGenerator::genTemplateEntityHirePrice);
					suppliers.add(TESWikiGenerator::genTemplateEntityHirePricePledge);
					suppliers.add(TESWikiGenerator::genTemplateEntityHireable);
					suppliers.add(TESWikiGenerator::genTemplateEntityImmuneToFire);
					suppliers.add(TESWikiGenerator::genTemplateEntityImmuneToFrost);
					suppliers.add(TESWikiGenerator::genTemplateEntityImmuneToHeat);
					suppliers.add(TESWikiGenerator::genTemplateEntityKillAchievement);
					suppliers.add(TESWikiGenerator::genTemplateEntityKillAlignment);
					suppliers.add(TESWikiGenerator::genTemplateEntityLegendaryDrop);
					suppliers.add(TESWikiGenerator::genTemplateEntityMarriage);
					suppliers.add(TESWikiGenerator::genTemplateEntityMercenary);
					suppliers.add(TESWikiGenerator::genTemplateEntityOwners);
					suppliers.add(TESWikiGenerator::genTemplateEntityRideableAnimal);
					suppliers.add(TESWikiGenerator::genTemplateEntityRideableNPC);
					suppliers.add(TESWikiGenerator::genTemplateEntitySellPools);
					suppliers.add(TESWikiGenerator::genTemplateEntitySellUnitPools);
					suppliers.add(TESWikiGenerator::genTemplateEntitySmith);
					suppliers.add(TESWikiGenerator::genTemplateEntitySpawnsInDarkness);
					suppliers.add(TESWikiGenerator::genTemplateEntityStructures);
					suppliers.add(TESWikiGenerator::genTemplateEntityTargetSeeker);
					suppliers.add(TESWikiGenerator::genTemplateEntityTradeable);
					suppliers.add(TESWikiGenerator::genTemplateEntityUnitTradeable);
					suppliers.add(() -> genTemplateEntityWaypoint(world));

					suppliers.parallelStream().map(Supplier::get).forEach(sb::append);
					suppliers.clear();

					sb.append("</mediawiki>");

					TESDate.AegonCalendar.getDate().getMonth().setSeason(season);

					printWriter.write(sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				break;
		}

		long newTime = System.nanoTime();

		IChatComponent component = new ChatComponentText("Generated in " + (newTime - time) / 1.0E6 + " milliseconds");
		entityPlayer.addChatMessage(component);
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	private static void genTableAchievements(EntityPlayer entityPlayer) {
		Collection<String> data = new TreeSet<>();

		for (TESAchievement achievement : ACHIEVEMENTS) {
			if (!(achievement instanceof TESAchievementRank)) {
				StringBuilder sb = new StringBuilder();

				sb.append(NL).append("| ");
				sb.append(achievement.getTitle(entityPlayer));
				sb.append(" || ").append(achievement.getDescription());
				sb.append(NL).append("|-");

				data.add(sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/achievements.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void genTableArmor() {
		Collection<String> data = new TreeSet<>();

		for (Item item : ITEMS) {
			if (item instanceof ItemArmor) {
				StringBuilder sb = new StringBuilder();

				float damage = ((ItemArmor) item).damageReduceAmount;
				ItemArmor.ArmorMaterial material = ((ItemArmor) item).getArmorMaterial();

				sb.append(NL).append("| ");
				sb.append(getItemName(item));
				sb.append(" || ").append(getItemFilename(item));
				sb.append(" || ").append(item.getMaxDamage());
				sb.append(" || ").append(damage);

				sb.append(" || ");
				if (material == null || material.customCraftingMaterial == null) {
					sb.append(N_A);
				} else {
					sb.append(getItemName(material.customCraftingMaterial));
				}

				sb.append(NL).append("|-");

				data.add(sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/armor.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	private static void genTableCapes() {
		Collection<String> data = new TreeSet<>();

		for (TESCapes cape : CAPES) {
			StringBuilder sb = new StringBuilder();

			sb.append(NL).append("| ");
			sb.append(cape.getCapeName());
			sb.append(" || ").append(cape.getCapeDesc());
			sb.append(" || ").append(getCapeFilename(cape));
			sb.append(NL).append("|-");

			data.add(sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/capes.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private static void genTableFood() {
		Collection<String> data = new TreeSet<>();

		DecimalFormat decimalFormat = new DecimalFormat("#.##");

		for (Item item : ITEMS) {
			if (item instanceof ItemFood) {
				StringBuilder sb = new StringBuilder();

				int heal = ((ItemFood) item).func_150905_g(null);
				float saturation = ((ItemFood) item).func_150906_h(null);

				sb.append(NL).append("| ");
				sb.append(getItemName(item));
				sb.append(" || ").append(getItemFilename(item));
				sb.append(" || ").append("{{Bar|bread|").append(decimalFormat.format(saturation * heal * 2)).append("}}");
				sb.append(" || ").append("{{Bar|food|").append(heal).append("}}");
				sb.append(" || ").append(item.getItemStackLimit());
				sb.append(NL).append("|-");

				data.add(sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/food.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	private static void genTableShields() {
		Collection<String> data = new TreeSet<>();

		for (TESShields shield : SHIELDS) {
			StringBuilder sb = new StringBuilder();

			sb.append(NL).append("| ");
			sb.append(shield.getShieldName());
			sb.append(" || ").append(shield.getShieldDesc());
			sb.append(" || ").append(getShieldFilename(shield));
			sb.append(NL).append("|-");

			data.add(sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/shields.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void genTableUnits() {
		Collection<String> data = new TreeSet<>();

		for (TESUnitTradeEntries unitTradeEntries : UNIT_TRADE_ENTRIES) {
			for (TESUnitTradeEntry entry : unitTradeEntries.getTradeEntries()) {
				StringBuilder sb = new StringBuilder();

				sb.append(NL).append("| ");
				sb.append(getEntityLink(entry.getEntityClass()));

				if (entry.getMountClass() != null) {
					sb.append(Lang.RIDER);
				}

				int cost = entry.getInitialCost();
				int alignment = (int) entry.getAlignmentRequired();

				if (entry.getPledgeType() == TESUnitTradeEntry.PledgeType.NONE) {
					sb.append(" || ").append("{{Coins|").append(cost * 2).append("}}");
					sb.append(" || ").append("{{Coins|").append(cost).append("}}");
					sb.append(" || ").append('+').append(alignment);
					sb.append(" || ").append('-');
				} else {
					sb.append(" || ").append(N_A);
					sb.append(" || ").append("{{Coins|").append(cost).append("}}");
					sb.append(" || ").append('+').append(Math.max(alignment, 100));
					sb.append(" || ").append('+');
				}

				sb.append(NL).append("|-");

				data.add(sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/units.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("StringBufferReplaceableByString")
	private static void genTableWaypoints(EntityPlayer entityPlayer) {
		Collection<String> data = new TreeSet<>();

		for (TESWaypoint wp : WAYPOINTS) {
			StringBuilder sb = new StringBuilder();

			sb.append(NL).append("| ");
			sb.append(wp.getDisplayName());
			sb.append(" || ").append(wp.getLoreText(entityPlayer));
			sb.append(NL).append("|-");

			data.add(sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/waypoints.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void genTableWeapons() {
		Collection<String> data = new TreeSet<>();

		for (Item item : ITEMS) {
			if (item instanceof ItemSword) {
				StringBuilder sb = new StringBuilder();

				float damage = TESReflection.getDamageAmount(item);
				Item.ToolMaterial material = TESReflection.getToolMaterial(item);

				sb.append(NL).append("| ");
				sb.append(getItemName(item));
				sb.append(" || ").append(getItemFilename(item));
				sb.append(" || ").append(item.getMaxDamage());
				sb.append(" || ").append(damage);

				sb.append(" || ");
				if (material == null || material.getRepairItemStack() == null) {
					sb.append(N_A);
				} else {
					sb.append(getItemName(material.getRepairItemStack().getItem()));
				}

				sb.append(NL).append("|-");

				data.add(sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		for (String datum : data) {
			sb.append(datum);
		}

		try (PrintWriter printWriter = new PrintWriter("hummel/weapon.txt", UTF_8)) {
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static StringBuilder genTemplateBiomeAnimals() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			Collection<BiomeGenBase.SpawnListEntry> spawnListEntries = new HashSet<>(biome.getSpawnableList(EnumCreatureType.ambient));
			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.waterCreature));
			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.creature));
			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.monster));
			spawnListEntries.addAll(biome.getSpawnableList(TESBiome.CREATURE_TYPE_TES_AMBIENT));

			for (BiomeGenBase.SpawnListEntry spawnListEntry : spawnListEntries) {
				data.get(biome).add(getEntityLink(spawnListEntry.entityClass));
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Animals");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_ANIMALS, Lang.BIOME_NO_ANIMALS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeBandits() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, biome.getBanditChance().toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Bandits");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeClimate() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			if (biome.getClimateType() != null) {
				switch (biome.getClimateType()) {
					case COLD:
						data.put(biome, Lang.CLIMATE_COLD.toString());
						break;
					case COLD_AZ:
						data.put(biome, Lang.CLIMATE_COLD_AZ.toString());
						break;
					case NORMAL:
						data.put(biome, Lang.CLIMATE_NORMAL.toString());
						break;
					case NORMAL_AZ:
						data.put(biome, Lang.CLIMATE_NORMAL_AZ.toString());
						break;
					case SUMMER:
						data.put(biome, Lang.CLIMATE_SUMMER.toString());
						break;
					case SUMMER_AZ:
						data.put(biome, Lang.CLIMATE_SUMMER_AZ.toString());
						break;
					case WINTER:
						data.put(biome, Lang.CLIMATE_WINTER.toString());
						break;
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Climate");
		sb.append(BEGIN);

		appendDefault(sb, Lang.CLIMATE_NULL.toString());

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeConquestFactions() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			for (TESFactionContainer factionContainer : biome.getNPCSpawnList().getFactionContainers()) {
				if (factionContainer.getBaseWeight() <= 0) {
					for (TESSpawnListContainer spawnListContainer : factionContainer.getSpawnListContainers()) {
						for (TESSpawnEntry spawnEntry : spawnListContainer.getSpawnList().getSpawnEntries()) {
							Entity entity = ENTITY_CLASS_TO_ENTITY.get(spawnEntry.entityClass);
							if (entity instanceof TESEntityNPC) {
								TESFaction faction = ((TESEntityNPC) entity).getFaction();
								data.get(biome).add(getFactionLink(faction));
								break;
							}
						}
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-ConquestFactions");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_CONQUEST_FACTIONS, Lang.BIOME_NO_CONQUEST_FACTIONS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeInvasionFactions() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			for (TESInvasions invasion : biome.getInvasionSpawns().getRegisteredInvasions()) {
				for (TESInvasions.InvasionSpawnEntry invasionSpawnEntry : invasion.getInvasionMobs()) {
					Entity entity = ENTITY_CLASS_TO_ENTITY.get(invasionSpawnEntry.getEntityClass());
					if (entity instanceof TESEntityNPC) {
						TESFaction faction = ((TESEntityNPC) entity).getFaction();
						data.get(biome).add(getFactionLink(faction));
						break;
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-InvasionFactions");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_INVASION_FACTIONS, Lang.BIOME_NO_INVASION_FACTIONS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeMinerals() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			Collection<TESBiomeDecorator.OreGenerant> oreGenerants = new HashSet<>(biome.getDecorator().getBiomeSoils());
			oreGenerants.addAll(biome.getDecorator().getBiomeOres());
			oreGenerants.addAll(biome.getDecorator().getBiomeGems());

			for (TESBiomeDecorator.OreGenerant oreGenerant : oreGenerants) {
				Block block = TESReflection.getOreGenBlock(oreGenerant.getOreGen());
				int meta = TESReflection.getOreGenMeta(oreGenerant.getOreGen());

				String blockLink;
				if (block instanceof TESBlockOreGem || block instanceof BlockDirt || block instanceof TESBlockRock) {
					blockLink = getMineralLink(block, meta);
				} else {
					blockLink = getMineralLink(block);
				}

				String stats = "(" + oreGenerant.getOreChance() + "%; Y: " + oreGenerant.getMinHeight() + '-' + oreGenerant.getMaxHeight() + ')';

				data.get(biome).add(blockLink + ' ' + stats);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Minerals");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_MINERALS, Lang.BIOME_NO_MINERALS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeMusic() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			if (biome.getBiomeMusic() != null) {
				data.put(biome, biome.getBiomeMusic().getSubregion());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Music");
		sb.append(BEGIN);

		appendDefault(sb, N_A);

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeName() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, getBiomeName(biome));
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Name");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeNPCs() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			for (TESFactionContainer factionContainer : biome.getNPCSpawnList().getFactionContainers()) {
				if (factionContainer.getBaseWeight() > 0) {
					for (TESSpawnListContainer spawnListContainer : factionContainer.getSpawnListContainers()) {
						for (TESSpawnEntry spawnEntry : spawnListContainer.getSpawnList().getSpawnEntries()) {
							data.get(biome).add(getEntityLink(spawnEntry.entityClass));
						}
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-NPCs");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_NPCS, Lang.BIOME_NO_NPCS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeRainfall() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			StringBuilder sb = new StringBuilder();

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.WINTER);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append(Lang.SEASON_WINTER).append(": ").append(biome.rainfall);

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.SPRING);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append("&lt;br&gt;");
			sb.append(Lang.SEASON_SPRING).append(": ").append(biome.rainfall);

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.SUMMER);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append("&lt;br&gt;");
			sb.append(Lang.SEASON_SUMMER).append(": ").append(biome.rainfall);

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.AUTUMN);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append("&lt;br&gt;");
			sb.append(Lang.SEASON_AUTUMN).append(": ").append(biome.rainfall);

			data.put(biome, sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Rainfall");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeStructures() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			for (TESBiomeDecorator.Structure structure : biome.getDecorator().getStructures()) {
				data.get(biome).add(getStructureLink(structure.getStructureGen().getClass()));
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Structures");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_STRUCTURES, Lang.BIOME_NO_STRUCTURES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeTemperature() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			StringBuilder sb = new StringBuilder();

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.WINTER);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append(Lang.SEASON_WINTER).append(": ").append(biome.temperature);

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.SPRING);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append("&lt;br&gt;");
			sb.append(Lang.SEASON_SPRING).append(": ").append(biome.temperature);

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.SUMMER);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append("&lt;br&gt;");
			sb.append(Lang.SEASON_SUMMER).append(": ").append(biome.temperature);

			TESDate.AegonCalendar.getDate().getMonth().setSeason(TESDate.Season.AUTUMN);
			TESClimateType.performSeasonalChangesServerSide();
			sb.append("&lt;br&gt;");
			sb.append(Lang.SEASON_AUTUMN).append(": ").append(biome.temperature);

			data.put(biome, sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Temperature");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeTrees() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			Collection<TESTreeType.WeightedTreeType> weightedTreeTypes = biome.getDecorator().getTreeTypes();

			Collection<TESTreeType> excludedTreeTypes = EnumSet.noneOf(TESTreeType.class);

			for (TESTreeType.WeightedTreeType weightedTreeType : weightedTreeTypes) {
				TESTreeType treeType = weightedTreeType.getTreeType();

				data.get(biome).add(getTreeLink(treeType));

				excludedTreeTypes.add(treeType);
			}

			for (TESBiomeVariantList.VariantBucket variantBucket : biome.getBiomeVariants().getVariantList()) {
				for (TESTreeType.WeightedTreeType weightedTreeType : variantBucket.getVariant().getTreeTypes()) {
					TESTreeType treeType = weightedTreeType.getTreeType();

					if (!excludedTreeTypes.contains(treeType)) {
						data.get(biome).add(getTreeLink(treeType) + " (" + getBiomeVariantName(variantBucket.getVariant()).toLowerCase(Locale.ROOT) + ')');
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Trees");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_TREES, Lang.BIOME_NO_TREES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeVariants() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			for (TESBiomeVariantList.VariantBucket variantBucket : biome.getBiomeVariants().getVariantList()) {
				data.get(biome).add(getBiomeVariantName(variantBucket.getVariant()));
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Variants");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_VARIANTS, Lang.BIOME_NO_VARIANTS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeVisitAchievement() {
		Map<TESBiome, String> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			TESAchievement achievement = biome.getBiomeAchievement();

			if (achievement != null) {
				data.put(biome, '«' + achievement.getTitle() + '»');
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-VisitAchievement");
		sb.append(BEGIN);

		appendDefault(sb, N_A);

		for (Map.Entry<TESBiome, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateBiomeWaypoints() {
		Map<TESBiome, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			data.put(biome, new TreeSet<>());

			for (TESWaypoint wp : biome.getBiomeWaypoints().getWaypoints()) {
				data.get(biome).add(wp.getDisplayName() + " (" + getFactionLink(wp.getFaction()) + ')');
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Biome-Waypoints");
		sb.append(BEGIN);

		for (Map.Entry<TESBiome, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getBiomePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.BIOME_HAS_WAYPOINTS, Lang.BIOME_NO_WAYPOINTS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityBannerBearer() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESBannerBearer) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-BannerBearer");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityBiomes() {
		Map<Class<? extends Entity>, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			Collection<BiomeGenBase.SpawnListEntry> spawnListEntries = new HashSet<>();
			Collection<Class<? extends Entity>> conquestEntityClasses = new HashSet<>();
			Collection<Class<? extends Entity>> invasionEntityClasses = new HashSet<>();

			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.ambient));
			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.waterCreature));
			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.creature));
			spawnListEntries.addAll(biome.getSpawnableList(EnumCreatureType.monster));
			spawnListEntries.addAll(biome.getSpawnableTESAmbientList());

			for (TESFactionContainer factionContainer : biome.getNPCSpawnList().getFactionContainers()) {
				if (factionContainer.getBaseWeight() > 0) {
					for (TESSpawnListContainer spawnListContainer : factionContainer.getSpawnListContainers()) {
						spawnListEntries.addAll(spawnListContainer.getSpawnList().getSpawnEntries());
					}
				} else {
					for (TESSpawnListContainer spawnListContainer : factionContainer.getSpawnListContainers()) {
						for (BiomeGenBase.SpawnListEntry spawnListEntry : spawnListContainer.getSpawnList().getSpawnEntries()) {
							conquestEntityClasses.add(spawnListEntry.entityClass);
						}
					}
				}
			}

			for (TESInvasions invasion : biome.getInvasionSpawns().getRegisteredInvasions()) {
				for (TESInvasions.InvasionSpawnEntry invasionSpawnEntry : invasion.getInvasionMobs()) {
					invasionEntityClasses.add(invasionSpawnEntry.getEntityClass());
				}
			}

			Collection<Class<? extends Entity>> bothConquestInvasion = new HashSet<>(conquestEntityClasses);
			bothConquestInvasion.retainAll(invasionEntityClasses);

			conquestEntityClasses.removeAll(bothConquestInvasion);
			invasionEntityClasses.removeAll(bothConquestInvasion);

			for (BiomeGenBase.SpawnListEntry entry : spawnListEntries) {
				data.computeIfAbsent(entry.entityClass, s -> new TreeSet<>());
				data.get(entry.entityClass).add(getBiomeLink(biome));
			}

			for (Class<? extends Entity> entityClass : conquestEntityClasses) {
				data.computeIfAbsent(entityClass, s -> new TreeSet<>());
				data.get(entityClass).add(getBiomeLink(biome) + ' ' + Lang.ENTITY_CONQUEST);
			}

			for (Class<? extends Entity> entityClass : invasionEntityClasses) {
				data.computeIfAbsent(entityClass, s -> new TreeSet<>());
				data.get(entityClass).add(getBiomeLink(biome) + ' ' + Lang.ENTITY_INVASION);
			}

			for (Class<? extends Entity> entityClass : bothConquestInvasion) {
				data.computeIfAbsent(entityClass, s -> new TreeSet<>());
				data.get(entityClass).add(getBiomeLink(biome) + ' ' + Lang.ENTITY_CONQUEST_INVASION);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Biomes");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_BIOMES, Lang.ENTITY_NO_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityBuyPools() {
		Map<Class<? extends Entity>, Set<String>> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESTradeable) {
				data.put(entityEntry.getKey(), new TreeSet<>());

				TESTradeable tradeable = (TESTradeable) entityEntry.getValue();

				for (TESTradeEntry entry : tradeable.getSellPool().getTradeEntries()) {
					data.get(entityEntry.getKey()).add(entry.getTradeItem().getDisplayName() + ": {{Coins|" + entry.getCost() + "}};");
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-BuyPools");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_BUY_POOLS, Lang.ENTITY_NO_BUY_POOLS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityCharacter() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC && ((TESEntityNPC) entityEntry.getValue()).isLegendaryNPC()) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Character");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityFaction() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entityEntry.getValue();
				data.put(entityEntry.getKey(), getFactionLink(npc.getFaction()));
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Faction");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityFarmhand() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESFarmhand) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Farmhand");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityHealth() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			EntityLivingBase entity = (EntityLivingBase) entityEntry.getValue();
			data.put(entityEntry.getKey(), String.valueOf((int) entity.getMaxHealth()));
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Health");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityHireable() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (TESUnitTradeEntries entries : UNIT_TRADE_ENTRIES) {
			for (TESUnitTradeEntry entry : entries.getTradeEntries()) {
				data.put(entry.getEntityClass(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Hireable");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityHireAlignment() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (TESUnitTradeEntries entries : UNIT_TRADE_ENTRIES) {
			for (TESUnitTradeEntry entry : entries.getTradeEntries()) {
				int alignment = (int) entry.getAlignmentRequired();

				if (entry.getPledgeType() == TESUnitTradeEntry.PledgeType.NONE) {
					data.put(entry.getEntityClass(), "+" + alignment);
				} else {
					data.put(entry.getEntityClass(), "+" + Math.max(alignment, 100));
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-HireAlignment");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityHirePrice() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (TESUnitTradeEntries entries : UNIT_TRADE_ENTRIES) {
			for (TESUnitTradeEntry entry : entries.getTradeEntries()) {
				int cost = entry.getInitialCost();

				if (entry.getPledgeType() == TESUnitTradeEntry.PledgeType.NONE) {
					data.put(entry.getEntityClass(), "{{Coins|" + cost * 2 + "}}");
				} else {
					data.put(entry.getEntityClass(), N_A);
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-HirePrice");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityHirePricePledge() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (TESUnitTradeEntries entries : UNIT_TRADE_ENTRIES) {
			for (TESUnitTradeEntry entry : entries.getTradeEntries()) {
				int cost = entry.getInitialCost();

				if (entry.getPledgeType() == TESUnitTradeEntry.PledgeType.NONE) {
					data.put(entry.getEntityClass(), "{{Coins|" + cost + "}}");
				} else {
					data.put(entry.getEntityClass(), N_A);
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-HirePricePledge");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityImmuneToFire() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue().isImmuneToFire()) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-ImmuneToFire");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityImmuneToFrost() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC && entityEntry.getValue() instanceof TESBiome.ImmuneToFrost) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-ImmuneToFrost");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityImmuneToHeat() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESBiome.ImmuneToHeat) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-ImmuneToHeat");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityKillAchievement() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entityEntry.getValue();
				if (npc.getKillAchievement() != null) {
					data.put(entityEntry.getKey(), '«' + npc.getKillAchievement().getTitle() + '»');
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-KillAchievement");
		sb.append(BEGIN);

		appendDefault(sb, N_A);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityKillAlignment() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entityEntry.getValue();
				data.put(entityEntry.getKey(), "+" + (int) npc.getAlignmentBonus());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-KillAlignment");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityLegendaryDrop() {
		Map<Class<? extends Entity>, Set<String>> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC && ((TESEntityNPC) entityEntry.getValue()).isLegendaryNPC()) {
				data.put(entityEntry.getKey(), new TreeSet<>());

				TESEntityNPC npc = (TESEntityNPC) entityEntry.getValue();

				npc.dropFewItems(true, 999);

				for (Object obj : npc.getDrops()) {
					if (obj instanceof Item) {
						ItemStack itemStack = new ItemStack((Item) obj);
						data.get(entityEntry.getKey()).add(itemStack.getDisplayName());
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-LegendaryDrop");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_LEGENDARY_DROP, Lang.ENTITY_NO_LEGENDARY_DROP);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityMarriage() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC && TESEntityUtils.canBeMarried((TESEntityNPC) entityEntry.getValue())) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Marriage");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityMercenary() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESMercenary) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Mercenary");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityOwners() {
		Map<Class<? extends Entity>, Set<String>> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESUnitTradeable) {
				TESUnitTradeable tradeable = (TESUnitTradeable) entityEntry.getValue();
				for (TESUnitTradeEntry entry : tradeable.getUnits().getTradeEntries()) {
					data.computeIfAbsent(entry.getEntityClass(), s -> new TreeSet<>());
					data.get(entry.getEntityClass()).add(getEntityLink(entityEntry.getKey()));
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append("DB Entity-Owners");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_OWNERS, Lang.ENTITY_NO_OWNERS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityRideableAnimal() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESNPCMount) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-RideableAnimal");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityRideableNPC() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntitySpiderBase) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append(" DB Entity-RideableNPC");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntitySellPools() {
		Map<Class<? extends Entity>, Set<String>> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESTradeable) {
				data.put(entityEntry.getKey(), new TreeSet<>());

				TESTradeable tradeable = (TESTradeable) entityEntry.getValue();

				for (TESTradeEntry entry : tradeable.getBuyPool().getTradeEntries()) {
					data.get(entityEntry.getKey()).add(entry.getTradeItem().getDisplayName() + ": {{Coins|" + entry.getCost() + "}};");
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-SellPools");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_SELL_UNIT_POOLS, Lang.ENTITY_NO_SELL_UNIT_POOLS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntitySellUnitPools() {
		Map<Class<? extends Entity>, List<String>> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESUnitTradeable) {
				data.put(entityEntry.getKey(), new ArrayList<>());

				TESUnitTradeable tradeable = (TESUnitTradeable) entityEntry.getValue();

				for (TESUnitTradeEntry entry : tradeable.getUnits().getTradeEntries()) {
					StringBuilder sb = new StringBuilder();

					sb.append(getEntityLink(entry.getEntityClass()));
					if (entry.getMountClass() != null) {
						sb.append(Lang.RIDER);
					}
					sb.append(": ");

					int cost = entry.getInitialCost();
					int alignment = (int) entry.getAlignmentRequired();

					if (entry.getPledgeType() == TESUnitTradeEntry.PledgeType.NONE) {
						sb.append("{{Coins|").append(cost * 2).append("}} ").append(Lang.NO_PLEDGE).append(", ");
						sb.append("{{Coins|").append(cost).append("}} ").append(Lang.NEED_PLEDGE).append("; ");
						sb.append('+').append(alignment).append(' ').append(Lang.REPUTATION).append(';');
					} else {
						sb.append("N/A ").append(Lang.NO_PLEDGE).append(", ");
						sb.append("{{Coins|").append(cost).append("}} ").append(Lang.NEED_PLEDGE).append("; ");
						sb.append('+').append(Math.max(alignment, 100)).append(' ').append(Lang.REPUTATION).append(';');
					}

					data.get(entityEntry.getKey()).add(sb.toString());
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-SellUnitPools");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, List<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_SELL_UNIT_POOLS, Lang.ENTITY_NO_SELL_UNIT_POOLS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntitySmith() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESTradeable.Smith) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Smith");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntitySpawnsInDarkness() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC && ((TESEntityNPC) entityEntry.getValue()).isSpawnsInDarkness()) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-SpawnsInDarkness");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityStructures() {
		StringBuilder sb = new StringBuilder();

		Map<Class<? extends Entity>, Set<String>> data = new HashMap<>();
		

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Structures");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.ENTITY_HAS_STRUCTURES, Lang.ENTITY_NO_STRUCTURES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityTargetSeeker() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESEntityNPC && ((TESEntityNPC) entityEntry.getValue()).isTargetSeeker()) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-TargetSeeker");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityTradeable() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESTradeable) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Tradeable");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityUnitTradeable() {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			if (entityEntry.getValue() instanceof TESUnitTradeable) {
				data.put(entityEntry.getKey(), TRUE);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-UnitTradeable");
		sb.append(BEGIN);

		appendDefault(sb, FALSE);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateEntityWaypoint(World world) {
		Map<Class<? extends Entity>, String> data = new HashMap<>();

		for (Map.Entry<Class<? extends Entity>, TESWaypoint> entityEntry : ENTITY_CLASS_TO_WP.entrySet()) {
			data.put(entityEntry.getKey(), entityEntry.getValue().getDisplayName());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Entity-Waypoint");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends Entity>, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getEntityPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionBanners() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			data.put(faction, new TreeSet<>());

			for (TESItemBanner.BannerType banner : faction.getFactionBanners()) {
				data.get(faction).add(getBannerName(banner));
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Banners");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_BANNERS, Lang.FACTION_NO_BANNERS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionCharacters() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			Entity entity = entityEntry.getValue();
			if (entity instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entity;
				if (npc.isLegendaryNPC()) {
					data.computeIfAbsent(npc.getFaction(), s -> new TreeSet<>());
					data.get(npc.getFaction()).add(getEntityLink(entityEntry.getKey()));
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Characters");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_CHARACTERS, Lang.FACTION_NO_CHARACTERS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionCodename() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			data.put(faction, faction.codeName());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Codename");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionConquestBiomes() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (TESBiome biome : BIOMES) {
			for (TESFactionContainer factionContainer : biome.getNPCSpawnList().getFactionContainers()) {
				if (factionContainer.getBaseWeight() <= 0) {
					for (TESSpawnListContainer spawnListContainer : factionContainer.getSpawnListContainers()) {
						for (TESSpawnEntry spawnEntry : spawnListContainer.getSpawnList().getSpawnEntries()) {
							Entity entity = ENTITY_CLASS_TO_ENTITY.get(spawnEntry.entityClass);
							if (entity instanceof TESEntityNPC) {
								TESFaction faction = ((TESEntityNPC) entity).getFaction();
								data.computeIfAbsent(faction, s -> new TreeSet<>());
								data.get(faction).add(getBiomeLink(biome));
								break;
							}
						}
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-ConquestBiomes");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_CONQUEST_BIOMES, Lang.FACTION_NO_CONQUEST_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionEnemies() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			StringJoiner sj = new StringJoiner(" • ");

			for (TESFaction otherFaction : FACTIONS) {
				if (faction.isBadRelation(otherFaction) && faction != otherFaction) {
					sj.add(getFactionLink(otherFaction));
				}
			}

			data.put(faction, sj.toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Enemies");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}
		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionFriends() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			StringJoiner sj = new StringJoiner(" • ");

			for (TESFaction otherFaction : FACTIONS) {
				if (faction.isGoodRelation(otherFaction) && faction != otherFaction) {
					sj.add(getFactionLink(otherFaction));
				}
			}

			data.put(faction, sj.toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Friends");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}
		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionInvasionBiomes() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (TESBiome biome : BIOMES) {
			for (TESInvasions invasion : biome.getInvasionSpawns().getRegisteredInvasions()) {
				for (TESInvasions.InvasionSpawnEntry invasionSpawnEntry : invasion.getInvasionMobs()) {
					Entity entity = ENTITY_CLASS_TO_ENTITY.get(invasionSpawnEntry.getEntityClass());
					if (entity instanceof TESEntityNPC) {
						TESFaction faction = ((TESEntityNPC) entity).getFaction();
						data.computeIfAbsent(faction, s -> new TreeSet<>());
						data.get(faction).add(getBiomeLink(biome));
						break;
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-InvasionBiomes");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_INVASION_BIOMES, Lang.FACTION_NO_INVASION_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionName() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			data.put(faction, getFactionName(faction));
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Name");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionNPCs() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (Map.Entry<Class<? extends Entity>, Entity> entityEntry : ENTITY_CLASS_TO_ENTITY.entrySet()) {
			Entity entity = entityEntry.getValue();
			if (entity instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entity;
				if (!npc.isLegendaryNPC()) {
					data.computeIfAbsent(npc.getFaction(), s -> new TreeSet<>());
					data.get(npc.getFaction()).add(getEntityLink(entityEntry.getKey()));
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-NPCs");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_NPCS, Lang.FACTION_NO_NPCS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionPledgeRank() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			TESFactionRank rank = faction.getPledgeRank();

			if (rank != null) {
				StringBuilder sb = new StringBuilder();

				sb.append(rank.getDisplayName());

				String femRank = rank.getDisplayNameFem();
				if (!femRank.contains("tes")) {
					sb.append('/').append(femRank);
				}

				sb.append(" (+").append((int) faction.getPledgeAlignment()).append(')');

				data.put(faction, sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-PledgeRank");
		sb.append(BEGIN);

		appendDefault(sb, N_A);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionRanks() {
		Map<TESFaction, List<String>> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			data.put(faction, new ArrayList<>());

			for (TESFactionRank rank : faction.getRanksSortedDescending()) {
				StringBuilder sb = new StringBuilder();

				sb.append(rank.getDisplayFullName());

				String femRank = rank.getDisplayFullNameFem();
				if (!femRank.contains("tes")) {
					sb.append('/').append(femRank);
				}

				sb.append(" (+").append((int) rank.getAlignment()).append(')');

				data.get(faction).add(sb.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Ranks");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, List<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_RANKS, Lang.FACTION_NO_RANKS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionRegion() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			if (faction.getFactionRegion() != null) {
				data.put(faction, faction.getFactionRegion().getRegionName());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Region");
		sb.append(BEGIN);

		appendDefault(sb, N_A);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionShieldsCapes() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			StringBuilder sb = new StringBuilder();

			sb.append(NL).append("&lt;table class=\"wikitable shields-capes\"&gt;");

			for (TESShields shield : SHIELDS) {
				if (shield.getAlignmentFaction() == faction) {
					sb.append(NL + "&lt;tr&gt;&lt;td&gt;").append(shield.getShieldName()).append("&lt;/td&gt;&lt;td&gt;").append(shield.getShieldDesc()).append("&lt;/td&gt;&lt;td&gt;").append(getShieldFilename(shield)).append("&lt;/td&gt;&lt;/tr&gt;");
				}
			}

			for (TESCapes cape : CAPES) {
				if (cape.getAlignmentFaction() == faction) {
					sb.append(NL + "&lt;tr&gt;&lt;td&gt;").append(cape.getCapeName()).append("&lt;/td&gt;&lt;td&gt;").append(cape.getCapeDesc()).append("&lt;/td&gt;&lt;td&gt;").append(getCapeFilename(cape)).append("&lt;/td&gt;&lt;/tr&gt;");
				}
			}

			sb.append(NL).append("&lt;table&gt;");

			data.put(faction, sb.toString());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-ShieldsCapes");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), null, Lang.FACTION_NO_ATTRIBUTES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionSpawnBiomes() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (TESBiome biome : BIOMES) {
			for (TESFactionContainer factionContainer : biome.getNPCSpawnList().getFactionContainers()) {
				if (factionContainer.getBaseWeight() > 0) {
					for (TESSpawnListContainer spawnListContainer : factionContainer.getSpawnListContainers()) {
						for (TESSpawnEntry spawnEntry : spawnListContainer.getSpawnList().getSpawnEntries()) {
							Entity entity = ENTITY_CLASS_TO_ENTITY.get(spawnEntry.entityClass);
							if (entity instanceof TESEntityNPC) {
								TESFaction faction = ((TESEntityNPC) entity).getFaction();
								data.computeIfAbsent(faction, s -> new TreeSet<>());
								data.get(faction).add(getBiomeLink(biome));
								break;
							}
						}
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Spawn");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_SPAWN_BIOMES, Lang.FACTION_NO_SPAWN_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionWarCrimes() {
		Map<TESFaction, String> data = new EnumMap<>(TESFaction.class);

		for (TESFaction faction : FACTIONS) {
			if (!faction.isApprovesWarCrimes()) {
				data.put(faction, Lang.FACTION_NO_WAR_CRIMES.toString());
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-WarCrimes");
		sb.append(BEGIN);

		appendDefault(sb, Lang.FACTION_HAS_WAR_CRIMES.toString());

		for (Map.Entry<TESFaction, String> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateFactionWaypoints() {
		Map<TESFaction, Set<String>> data = new EnumMap<>(TESFaction.class);

		for (TESWaypoint wp : WAYPOINTS) {
			data.computeIfAbsent(wp.getFaction(), s -> new TreeSet<>());
			data.get(wp.getFaction()).add(wp.getDisplayName());
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Faction-Waypoints");
		sb.append(BEGIN);

		for (Map.Entry<TESFaction, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getFactionPagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.FACTION_HAS_WAYPOINTS, Lang.FACTION_NO_WAYPOINTS);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateMineralBiomes() {
		Map<String, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			Collection<TESBiomeDecorator.OreGenerant> oreGenerants = new HashSet<>(biome.getDecorator().getBiomeSoils());
			oreGenerants.addAll(biome.getDecorator().getBiomeOres());
			oreGenerants.addAll(biome.getDecorator().getBiomeGems());

			for (TESBiomeDecorator.OreGenerant oreGenerant : oreGenerants) {
				Block block = TESReflection.getOreGenBlock(oreGenerant.getOreGen());
				int meta = TESReflection.getOreGenMeta(oreGenerant.getOreGen());

				String blockLink;
				if (block instanceof TESBlockOreGem || block instanceof BlockDirt || block instanceof TESBlockRock) {
					blockLink = getMineralLink(block, meta);
				} else {
					blockLink = getMineralLink(block);
				}

				String stats = " (" + oreGenerant.getOreChance() + "%; Y: " + oreGenerant.getMinHeight() + '-' + oreGenerant.getMaxHeight() + ')';

				data.computeIfAbsent(blockLink, s -> new TreeSet<>());
				data.get(blockLink).add(getBiomeLink(biome) + stats);
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Mineral-Biomes");
		sb.append(BEGIN);

		for (Map.Entry<String, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(entry.getKey()).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.MINERAL_HAS_BIOMES, Lang.MINERAL_NO_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateStructureBiomes() {
		Map<Class<? extends WorldGenerator>, Set<String>> data = new HashMap<>();

		for (TESBiome biome : BIOMES) {
			for (TESBiomeDecorator.Structure structure : biome.getDecorator().getStructures()) {
				data.computeIfAbsent(structure.getStructureGen().getClass(), s -> new TreeSet<>());
				data.get(structure.getStructureGen().getClass()).add(getBiomeLink(biome));
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Structure-Biomes");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends WorldGenerator>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getStructurePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.STRUCTURE_HAS_BIOMES, Lang.STRUCTURE_NO_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateStructureEntities() {
		StringBuilder sb = new StringBuilder();

		Map<Class<? extends WorldGenerator>, Set<String>> data = new HashMap<>();

		sb.append(TITLE).append(TEMPLATE).append("DB Structure-Entities");
		sb.append(BEGIN);

		for (Map.Entry<Class<? extends WorldGenerator>, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getStructurePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.STRUCTURE_HAS_ENTITIES, Lang.STRUCTURE_NO_ENTITIES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder genTemplateTreeBiomes() {
		Map<TESTreeType, Set<String>> data = new EnumMap<>(TESTreeType.class);

		for (TESBiome biome : BIOMES) {
			Collection<TESTreeType.WeightedTreeType> weightedTreeTypes = biome.getDecorator().getTreeTypes();

			Collection<TESTreeType> excludedTreeTypes = EnumSet.noneOf(TESTreeType.class);

			for (TESTreeType.WeightedTreeType weightedTreeType : weightedTreeTypes) {
				TESTreeType treeType = weightedTreeType.getTreeType();

				data.computeIfAbsent(treeType, s -> new TreeSet<>());
				data.get(treeType).add(getBiomeLink(biome));

				excludedTreeTypes.add(treeType);
			}

			for (TESBiomeVariantList.VariantBucket variantBucket : biome.getBiomeVariants().getVariantList()) {
				for (TESTreeType.WeightedTreeType weightedTreeType : variantBucket.getVariant().getTreeTypes()) {
					TESTreeType treeType = weightedTreeType.getTreeType();

					if (!excludedTreeTypes.contains(treeType)) {
						data.computeIfAbsent(treeType, s -> new TreeSet<>());
						data.get(treeType).add(getBiomeLink(biome) + " (" + getBiomeVariantName(variantBucket.getVariant()) + ')');
					}
				}
			}
		}

		StringBuilder sb = new StringBuilder();

		sb.append(TITLE).append(TEMPLATE).append("DB Tree-Biomes");
		sb.append(BEGIN);

		for (Map.Entry<TESTreeType, Set<String>> entry : data.entrySet()) {
			sb.append(NL).append("| ");
			sb.append(getTreePagename(entry.getKey())).append(" = ");

			appendPreamble(sb, entry.getValue(), Lang.TREE_HAS_BIOMES, Lang.TREE_NO_BIOMES);
			appendSection(sb, entry.getValue());
		}

		sb.append(END);

		return sb;
	}

	private static StringBuilder addPagesBiomes(Collection<String> neededPages, Collection<String> existingPages) {
		StringBuilder sb = new StringBuilder();

		for (TESBiome biome : BIOMES) {
			String pageName = getBiomePagename(biome);
			neededPages.add(pageName);
			if (!existingPages.contains(pageName)) {
				sb.append(TITLE_SINGLE).append(pageName);
				sb.append(PAGE_LEFT).append("{{Статья Биом}}").append(PAGE_RIGHT);
			}
		}

		return sb;
	}

	private static StringBuilder addPagesEntities(Collection<String> neededPages, Collection<String> existingPages) {
		StringBuilder sb = new StringBuilder();

		for (Class<? extends Entity> entityClass : ENTITY_CLASSES) {
			String pageName = getEntityPagename(entityClass);
			neededPages.add(pageName);
			if (!existingPages.contains(pageName)) {
				sb.append(TITLE_SINGLE).append(pageName);
				sb.append(PAGE_LEFT).append("{{Статья Моб}}").append(PAGE_RIGHT);
			}
		}

		return sb;
	}

	private static StringBuilder addPagesFactions(Collection<String> neededPages, Collection<String> existingPages) {
		StringBuilder sb = new StringBuilder();

		for (TESFaction faction : FACTIONS) {
			String pageName = getFactionPagename(faction);
			neededPages.add(pageName);
			if (!existingPages.contains(pageName)) {
				sb.append(TITLE_SINGLE).append(pageName);
				sb.append(PAGE_LEFT).append("{{Статья Фракция}}").append(PAGE_RIGHT);
			}
		}

		return sb;
	}

	private static StringBuilder addPagesMinerals(Collection<String> neededPages, Collection<String> existingPages) {
		StringBuilder sb = new StringBuilder();

		for (String pageName : MINERALS) {
			neededPages.add(pageName);
			if (!existingPages.contains(pageName)) {
				sb.append(TITLE_SINGLE).append(pageName);
				sb.append(PAGE_LEFT).append("{{Статья Ископаемое}}").append(PAGE_RIGHT);
			}
		}

		return sb;
	}

	private static StringBuilder addPagesStructures(Collection<String> neededPages, Collection<String> existingPages) {
		StringBuilder sb = new StringBuilder();

		for (Class<? extends WorldGenerator> strClass : STRUCTURE_CLASSES) {
			String pageName = getStructurePagename(strClass);
			neededPages.add(pageName);
			if (!existingPages.contains(pageName)) {
				sb.append(TITLE_SINGLE).append(pageName);
				sb.append(PAGE_LEFT).append("{{Статья Структура}}").append(PAGE_RIGHT);
			}
		}

		return sb;
	}

	private static StringBuilder addPagesTrees(Collection<String> neededPages, Collection<String> existingPages) {
		StringBuilder sb = new StringBuilder();

		for (TESTreeType tree : TREES) {
			String pageName = getTreePagename(tree);
			neededPages.add(pageName);
			if (!existingPages.contains(pageName)) {
				sb.append(TITLE_SINGLE).append(pageName);
				sb.append(PAGE_LEFT).append("{{Статья Дерево}}").append(PAGE_RIGHT);
			}
		}

		return sb;
	}

	private static Set<String> getExistingPages() {
		try {
			File file = new File("hummel/sitemap.txt");
			if (!file.exists()) {
				boolean created = file.createNewFile();
				if (!created) {
					TESLog.getLogger().info("DatabaseGenerator: file wasn't created");
				}
			}
			try (Stream<String> lines = Files.lines(Paths.get("hummel/sitemap.txt"))) {
				return lines.collect(Collectors.toSet());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Collections.emptySet();
	}

	private static void markPagesForRemoval(Collection<String> neededPages, Iterable<String> existingPages) {
		try (PrintWriter printWriter = new PrintWriter("hummel/removal.txt", UTF_8)) {
			StringBuilder sb = new StringBuilder();

			for (String existing : existingPages) {
				if (!neededPages.contains(existing)) {
					sb.append(existing).append('\n');
				}
			}
			printWriter.write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void searchForEntities(World world) {
		for (Class<? extends Entity> entityClass : ENTITY_CLASSES) {
			ENTITY_CLASS_TO_ENTITY.put(entityClass, TESReflection.newEntity(entityClass, world));
		}
	}

	private static void searchForStructures(World world) {
		for (Class<? extends WorldGenerator> structureClass : STRUCTURE_CLASSES) {
			WorldGenerator generator = null;
			try {
				generator = structureClass.getConstructor(Boolean.TYPE).newInstance(true);
			} catch (Exception ignored) {
			}
		}
	}

	private static void searchForMinerals() {
		for (TESBiome biome : BIOMES) {
			Collection<TESBiomeDecorator.OreGenerant> oreGenerants = new HashSet<>(biome.getDecorator().getBiomeSoils());
			oreGenerants.addAll(biome.getDecorator().getBiomeOres());
			oreGenerants.addAll(biome.getDecorator().getBiomeGems());

			for (TESBiomeDecorator.OreGenerant oreGenerant : oreGenerants) {
				Block block = TESReflection.getOreGenBlock(oreGenerant.getOreGen());
				int meta = TESReflection.getOreGenMeta(oreGenerant.getOreGen());

				if (block instanceof TESBlockOreGem || block instanceof BlockDirt || block instanceof TESBlockRock) {
					MINERALS.add(getMineralName(block, meta));
				} else {
					MINERALS.add(getMineralName(block));
				}
			}
		}
	}

	private static void searchForPagenamesBiome() {
		next:
		for (TESBiome biome : BIOMES) {
			String preName = getBiomeName(biome);
			for (TESFaction faction : FACTIONS) {
				if (preName.equals(getFactionName(faction))) {
					BIOME_TO_PAGENAME.put(biome, preName + " (" + Lang.PAGE_BIOME + ')');
					continue next;
				}
			}
			for (Class<? extends Entity> entityClass : ENTITY_CLASSES) {
				if (preName.equals(getEntityName(entityClass))) {
					BIOME_TO_PAGENAME.put(biome, preName + " (" + Lang.PAGE_BIOME + ')');
					continue next;
				}
			}
			BIOME_TO_PAGENAME.put(biome, preName);
		}
	}

	private static void searchForPagenamesEntity() {
		next:
		for (Class<? extends Entity> entityClass : ENTITY_CLASSES) {
			String preName = getEntityName(entityClass);
			for (TESBiome biome : BIOMES) {
				if (preName.equals(getBiomeName(biome))) {
					ENTITY_CLASS_TO_PAGENAME.put(entityClass, preName + " (" + Lang.PAGE_ENTITY + ')');
					continue next;
				}
			}
			for (TESFaction faction : FACTIONS) {
				if (preName.equals(getFactionName(faction))) {
					ENTITY_CLASS_TO_PAGENAME.put(entityClass, preName + " (" + Lang.PAGE_ENTITY + ')');
					continue next;
				}
			}
			ENTITY_CLASS_TO_PAGENAME.put(entityClass, preName);
		}
	}

	private static void searchForPagenamesFaction() {
		next:
		for (TESFaction faction : FACTIONS) {
			String preName = getFactionName(faction);
			for (TESBiome biome : BIOMES) {
				if (preName.equals(getBiomeName(biome))) {
					FACTION_TO_PAGENAME.put(faction, preName + " (" + Lang.PAGE_FACTION + ')');
					continue next;
				}
			}
			for (Class<? extends Entity> entityClass : ENTITY_CLASSES) {
				if (preName.equals(getEntityName(entityClass))) {
					FACTION_TO_PAGENAME.put(faction, preName + " (" + Lang.PAGE_FACTION + ')');
					continue next;
				}
			}
			FACTION_TO_PAGENAME.put(faction, preName);
		}
	}

	private static String getBannerName(TESItemBanner.BannerType banner) {
		return StatCollector.translateToLocal("item.tes:banner." + banner.getBannerName() + ".name");
	}

	private static String getBiomeLink(TESBiome biome) {
		String biomeName = getBiomeName(biome);
		String biomePagename = getBiomePagename(biome);
		if (biomeName.equals(biomePagename)) {
			return "[[" + biomeName + "]]";
		}
		return "[[" + biomePagename + '|' + biomeName + "]]";
	}

	private static String getBiomeName(TESBiome biome) {
		return StatCollector.translateToLocal("tes.biome." + biome.biomeName + ".name");
	}

	private static String getBiomePagename(TESBiome biome) {
		return BIOME_TO_PAGENAME.get(biome);
	}

	private static String getBiomeVariantName(TESBiomeVariant variant) {
		return StatCollector.translateToLocal("tes.variant." + variant.getVariantName() + ".name");
	}

	private static String getMineralLink(Block block, int meta) {
		return "[[" + StatCollector.translateToLocal(block.getUnlocalizedName() + '.' + meta + ".name") + "]]";
	}

	private static String getMineralLink(Block block) {
		return "[[" + StatCollector.translateToLocal(block.getUnlocalizedName() + ".name") + "]]";
	}

	private static String getMineralName(Block block, int meta) {
		return StatCollector.translateToLocal(block.getUnlocalizedName() + '.' + meta + ".name");
	}

	private static String getMineralName(Block block) {
		return StatCollector.translateToLocal(block.getUnlocalizedName() + ".name");
	}

	private static String getCapeFilename(TESCapes cape) {
		return "[[File:Cape " + cape.name().toLowerCase(Locale.ROOT) + ".png]]";
	}

	private static String getEntityLink(Class<? extends Entity> entityClass) {
		String entityName = getEntityName(entityClass);
		if (entityName.contains("null")) {
			return StatCollector.translateToLocal("entity." + EntityList.classToStringMapping.get(entityClass) + ".name");
		}

		String entityPagename = getEntityPagename(entityClass);
		if (entityName.equals(entityPagename)) {
			return "[[" + entityPagename + "]]";
		}
		return "[[" + entityPagename + '|' + entityName + "]]";
	}

	private static String getEntityName(Class<? extends Entity> entityClass) {
		return StatCollector.translateToLocal("entity.tes." + TESEntityRegistry.ENTITY_CLASS_TO_NAME.get(entityClass) + ".name");
	}

	private static String getEntityPagename(Class<? extends Entity> entityClass) {
		return ENTITY_CLASS_TO_PAGENAME.get(entityClass);
	}

	private static String getFactionLink(TESFaction fac) {
		String facName = getFactionName(fac);
		String facPagename = getFactionPagename(fac);
		if (facName.equals(facPagename)) {
			return "[[" + facPagename + "]]";
		}
		return "[[" + facPagename + '|' + facName + "]]";
	}

	private static String getFactionName(TESFaction fac) {
		return StatCollector.translateToLocal("tes.faction." + fac.codeName() + ".name");
	}

	private static String getFactionPagename(TESFaction fac) {
		return FACTION_TO_PAGENAME.get(fac);
	}

	private static String getItemFilename(Item item) {
		return "[[File:" + item.getUnlocalizedName().substring("item.tes:".length()) + ".png|32px|link=]]";
	}

	private static String getItemName(Item item) {
		return StatCollector.translateToLocal(item.getUnlocalizedName() + ".name");
	}

	private static String getShieldFilename(TESShields shield) {
		return "[[File:Shield " + shield.name().toLowerCase(Locale.ROOT) + ".png]]";
	}

	private static String getStructureLink(Class<? extends WorldGenerator> structureClass) {
		return "[[" + StatCollector.translateToLocal("tes.structure." + TESStructureRegistry.CLASS_TO_NAME_MAPPING.get(structureClass) + ".name") + "]]";
	}

	private static String getStructurePagename(Class<? extends WorldGenerator> structureClass) {
		return StatCollector.translateToLocal("tes.structure." + TESStructureRegistry.CLASS_TO_NAME_MAPPING.get(structureClass) + ".name");
	}

	private static String getTreePagename(TESTreeType tree) {
		return StatCollector.translateToLocal("tes.tree." + tree.name().toLowerCase(Locale.ROOT) + ".name");
	}

	private static String getTreeLink(TESTreeType tree) {
		return "[[" + StatCollector.translateToLocal("tes.tree." + tree.name().toLowerCase(Locale.ROOT) + ".name") + "]]";
	}

	private static void appendPreamble(StringBuilder sb, Collection<String> section, Lang full, Lang empty) {
		sb.append(section.isEmpty() ? empty : full);
	}

	private static void appendPreamble(StringBuilder sb, String value, Lang full, Lang empty) {
		if (full != null) {
			sb.append(value.isEmpty() ? empty : full);
		}
	}

	private static void appendSection(StringBuilder sb, Collection<String> section) {
		for (String item : section) {
			sb.append(NL).append("* ").append(item).append(';');
		}

		section.clear();
	}

	private static void appendSection(StringBuilder sb, String value) {
		sb.append(value);
	}

	private static void appendDefault(StringBuilder sb, String value) {
		sb.append(NL).append("| #default = ").append(value);
	}

	public enum Lang {
		BIOME_HAS_ANIMALS, BIOME_HAS_CONQUEST_FACTIONS, BIOME_HAS_INVASION_FACTIONS, BIOME_HAS_MINERALS, BIOME_HAS_NPCS, BIOME_HAS_STRUCTURES, BIOME_HAS_TREES, BIOME_HAS_VARIANTS, BIOME_HAS_WAYPOINTS, BIOME_NO_ANIMALS, BIOME_NO_CONQUEST_FACTIONS, BIOME_NO_INVASION_FACTIONS, BIOME_NO_MINERALS, BIOME_NO_NPCS, BIOME_NO_STRUCTURES, BIOME_NO_TREES, BIOME_NO_VARIANTS, BIOME_NO_WAYPOINTS, CATEGORY, CLIMATE_COLD, CLIMATE_COLD_AZ, CLIMATE_NORMAL, CLIMATE_NORMAL_AZ, CLIMATE_NULL, CLIMATE_SUMMER, CLIMATE_SUMMER_AZ, CLIMATE_WINTER, ENTITY_CONQUEST, ENTITY_CONQUEST_INVASION, ENTITY_HAS_BIOMES, ENTITY_HAS_BUY_POOLS, ENTITY_HAS_LEGENDARY_DROP, ENTITY_HAS_OWNERS, ENTITY_HAS_SELL_UNIT_POOLS, ENTITY_HAS_STRUCTURES, ENTITY_INVASION, ENTITY_NO_BIOMES, ENTITY_NO_BUY_POOLS, ENTITY_NO_LEGENDARY_DROP, ENTITY_NO_OWNERS, ENTITY_NO_SELL_UNIT_POOLS, ENTITY_NO_STRUCTURES, FACTION_HAS_BANNERS, FACTION_HAS_CHARACTERS, FACTION_HAS_CONQUEST_BIOMES, FACTION_HAS_INVASION_BIOMES, FACTION_HAS_NPCS, FACTION_HAS_RANKS, FACTION_HAS_SPAWN_BIOMES, FACTION_HAS_WAR_CRIMES, FACTION_HAS_WAYPOINTS, FACTION_NO_ATTRIBUTES, FACTION_NO_BANNERS, FACTION_NO_CHARACTERS, FACTION_NO_CONQUEST_BIOMES, FACTION_NO_INVASION_BIOMES, FACTION_NO_NPCS, FACTION_NO_RANKS, FACTION_NO_SPAWN_BIOMES, FACTION_NO_WAR_CRIMES, FACTION_NO_WAYPOINTS, MINERAL_HAS_BIOMES, MINERAL_NO_BIOMES, NEED_PLEDGE, NO_PLEDGE, PAGE_BIOME, PAGE_ENTITY, PAGE_FACTION, REPUTATION, RIDER, SEASON_AUTUMN, SEASON_SPRING, SEASON_SUMMER, SEASON_WINTER, STRUCTURE_HAS_BIOMES, STRUCTURE_HAS_ENTITIES, STRUCTURE_NO_BIOMES, STRUCTURE_NO_ENTITIES, TREE_HAS_BIOMES, TREE_NO_BIOMES;

		@Override
		public String toString() {
			return StatCollector.translateToLocal("tes.db." + name() + ".name");
		}
	}

	public enum Type {
		XML("xml"), TABLES("tables");

		private final String codeName;

		Type(String name) {
			codeName = name;
		}

		public static Type forName(String name) {
			for (Type db : values()) {
				if (db.codeName.equals(name)) {
					return db;
				}
			}
			return null;
		}

		public static Set<String> getNames() {
			Set<String> names = new HashSet<>();
			for (Type db : values()) {
				names.add(db.codeName);
			}
			return names;
		}
	}
}