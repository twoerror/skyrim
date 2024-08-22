package tes.common;

import tes.common.database.TESAchievement;
import tes.common.faction.TESFaction;
import tes.common.world.TESWorldProvider;
import tes.common.world.biome.TESBiome;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionManager;

import java.util.*;

public enum TESDimension {
	GAME_OF_THRONES("GameOfThrones", 99, TESWorldProvider.class, true, 99, EnumSet.of(DimensionRegion.TAMRIEL, DimensionRegion.OTHER));
	private final TESBiome[] biomeList = new TESBiome[256];

	private final Collection<TESFaction> factionList = new ArrayList<>();
	private final Collection<TESAchievement> allAchievements = new ArrayList<>();
	private final List<TESBiome> majorBiomes = new ArrayList<>();
	private final List<DimensionRegion> dimensionRegions = new ArrayList<>();
	private final List<TESAchievement.Category> achievementCategories = new ArrayList<>();
	private final Map<Integer, Integer> colorsToBiomeIDs = new HashMap<>();

	private final Class<? extends WorldProvider> providerClass;
	private final String dimensionName;

	private final boolean loadSpawn;
	private final int dimensionID;
	private final int spawnCap;

	TESDimension(String s, int i, Class<? extends WorldProvider> c, boolean flag, int spawns, Collection<DimensionRegion> regions) {
		dimensionName = s;
		dimensionID = i;
		providerClass = c;
		loadSpawn = flag;
		spawnCap = spawns;
		dimensionRegions.addAll(regions);
		for (DimensionRegion r : dimensionRegions) {
			r.setDimension(this);
		}
	}

	public static TESDimension forName(String s) {
		for (TESDimension dim : values()) {
			if (dim.dimensionName.equals(s)) {
				return dim;
			}
		}
		return null;
	}

	public static void onInit() {
		for (TESDimension dim : values()) {
			DimensionManager.registerProviderType(dim.dimensionID, dim.providerClass, dim.loadSpawn);
			DimensionManager.registerDimension(dim.dimensionID, dim.dimensionID);
		}
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public String getTranslatedDimensionName() {
		return StatCollector.translateToLocal(getUntranslatedDimensionName());
	}

	private String getUntranslatedDimensionName() {
		return "tes.dimension." + dimensionName;
	}

	public int getDimensionID() {
		return dimensionID;
	}

	public Map<Integer, Integer> getColorsToBiomeIDs() {
		return colorsToBiomeIDs;
	}

	public List<TESBiome> getMajorBiomes() {
		return majorBiomes;
	}

	public Collection<TESFaction> getFactionList() {
		return factionList;
	}

	public List<DimensionRegion> getDimensionRegions() {
		return dimensionRegions;
	}

	public int getSpawnCap() {
		return spawnCap;
	}

	public List<TESAchievement.Category> getAchievementCategories() {
		return achievementCategories;
	}

	public Collection<TESAchievement> getAllAchievements() {
		return allAchievements;
	}

	public TESBiome[] getBiomeList() {
		return biomeList;
	}

	public enum DimensionRegion {
		TAMRIEL("tamriel"), OTHER("other");

		private final List<TESFaction> factionList = new ArrayList<>();
		private final String regionName;

		private TESDimension dimension;

		DimensionRegion(String s) {
			regionName = s;
		}

		public static DimensionRegion forID(int ID) {
			for (DimensionRegion r : values()) {
				if (r.ordinal() == ID) {
					return r;
				}
			}
			return null;
		}

		public static DimensionRegion forName(String regionName) {
			for (DimensionRegion r : values()) {
				if (r.codeName().equals(regionName)) {
					return r;
				}
			}
			return null;
		}

		public String codeName() {
			return regionName;
		}

		public TESDimension getDimension() {
			return dimension;
		}

		public void setDimension(TESDimension dim) {
			dimension = dim;
		}

		public String getRegionName() {
			return StatCollector.translateToLocal("tes.dimension." + dimension.dimensionName + '.' + codeName());
		}

		public List<TESFaction> getFactionList() {
			return factionList;
		}
	}
}