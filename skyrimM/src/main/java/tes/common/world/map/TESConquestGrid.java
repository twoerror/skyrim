package tes.common.world.map;

import com.google.common.math.IntMath;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.TES;
import tes.common.TESConfig;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESAchievement;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionRank;
import tes.common.network.TESPacketConquestGrid;
import tes.common.network.TESPacketConquestNotification;
import tes.common.network.TESPacketHandler;
import tes.common.util.TESLog;
import tes.common.world.biome.TESBiome;
import tes.common.world.genlayer.TESGenLayerWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.io.File;
import java.util.*;

public class TESConquestGrid {
	private static final int MAP_GRID_SCALE = IntMath.pow(2, 3);
	private static final Map<GridCoordPair, TESConquestZone> ZONE_MAP = new HashMap<>();
	private static final TESConquestZone DUMMY_ZONE = new TESConquestZone(-999, -999).setDummyZone();
	private static final Collection<GridCoordPair> DIRTY_ZONES = new HashSet<>();
	private static final Map<GridCoordPair, List<TESFaction>> CACHED_ZONE_FACTIONS = new HashMap<>();

	private static boolean needsLoad = true;

	private TESConquestGrid() {
	}

	public static boolean anyChangedZones() {
		return !DIRTY_ZONES.isEmpty();
	}

	public static ConquestViewableQuery canPlayerViewConquest(EntityPlayer entityplayer, TESFaction fac) {
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		TESFaction pledged = pd.getPledgeFaction();
		if (pledged != null) {
			if (fac == pledged) {
				return ConquestViewableQuery.canView();
			}
			float align = pd.getAlignment(pledged);
			TESFactionRank pledgeRank = pledged.getPledgeRank();
			if (fac.isAlly(pledged) || fac.isBadRelation(pledged)) {
				return ConquestViewableQuery.canView();
			}
			TESFactionRank higherRank = pledged.getRankNAbove(pledgeRank, 2);
			if (align >= higherRank.getAlignment()) {
				return ConquestViewableQuery.canView();
			}
			return new ConquestViewableQuery(ConquestViewable.NEED_RANK, higherRank);
		}
		return new ConquestViewableQuery(ConquestViewable.UNPLEDGED, null);
	}

	private static void checkNotifyConquest(TESConquestZone zone, EntityPlayer originPlayer, TESFaction faction, float newConq, float prevConq, boolean isCleansing) {
		if (MathHelper.floor_double(newConq / 50.0f) != MathHelper.floor_double(prevConq / 50.0f) || newConq == 0.0f && prevConq != newConq) {
			World world = originPlayer.worldObj;
			List<EntityPlayer> players = world.playerEntities;
			for (EntityPlayer player : players) {
				TESFaction pledgeFac;
				TESPlayerData pd = TESLevelData.getData(player);
				if (player.getDistanceSqToEntity(originPlayer) > 40000.0 || getZoneByEntityCoords(player) != zone) {
					continue;
				}
				boolean playerApplicable = isCleansing ? (pledgeFac = pd.getPledgeFaction()) != null && pledgeFac.isBadRelation(faction) : pd.isPledgedTo(faction);
				if (!playerApplicable) {
					continue;
				}
				IMessage pkt = new TESPacketConquestNotification(faction, newConq, isCleansing);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(pkt, (EntityPlayerMP) player);
			}
		}
	}

	public static boolean conquestEnabled(World world) {
		return TESConfig.enableConquest && world.getWorldInfo().getTerrainType() != TES.worldTypeTESClassic;
	}

	public static float doRadialConquest(World world, TESConquestZone centralZone, EntityPlayer killingPlayer, TESFaction pledgeFaction, TESFaction enemyFaction, float conqGain, float conqCleanse) {
		if (!centralZone.isDummyZone()) {
			float centralConqBonus = 0.0f;
			for (int i1 = -3; i1 <= 3; ++i1) {
				for (int k1 = -3; k1 <= 3; ++k1) {
					float enemyConq;
					int distSq = i1 * i1 + k1 * k1;
					if (distSq > 12.25f) {
						continue;
					}
					int zoneX = centralZone.getGridX() + i1;
					int zoneZ = centralZone.getGridZ() + k1;
					float dist = MathHelper.sqrt_float(distSq);
					float frac = 1.0f - dist / 3.5f;
					float conqGainHere = frac * conqGain;
					float conqCleanseHere = frac * conqCleanse;
					TESConquestZone zone = getZoneByGridCoords(zoneX, zoneZ);
					if (zone.isDummyZone()) {
						continue;
					}
					boolean doneEnemyCleansing = false;
					if (enemyFaction != null && (enemyConq = zone.getConquestStrength(enemyFaction, world)) > 0.0f) {
						zone.addConquestStrength(enemyFaction, -conqCleanseHere, world);
						float newEnemyConq = zone.getConquestStrength(enemyFaction, world);
						if (zone == centralZone) {
							centralConqBonus = newEnemyConq - enemyConq;
						}
						if (killingPlayer != null) {
							checkNotifyConquest(zone, killingPlayer, enemyFaction, newEnemyConq, enemyConq, true);
						}
						doneEnemyCleansing = true;
					}
					if (doneEnemyCleansing || pledgeFaction == null) {
						continue;
					}
					float prevZoneConq = zone.getConquestStrength(pledgeFaction, world);
					zone.addConquestStrength(pledgeFaction, conqGainHere, world);
					float newZoneConq = zone.getConquestStrength(pledgeFaction, world);
					if (zone == centralZone) {
						centralConqBonus = newZoneConq - prevZoneConq;
					}
					if (killingPlayer == null) {
						continue;
					}
					checkNotifyConquest(zone, killingPlayer, pledgeFaction, newZoneConq, prevZoneConq, false);
				}
			}
			return centralConqBonus;
		}
		return 0.0f;
	}

	private static File getConquestDir() {
		File dir = new File(TESLevelData.getOrCreateTESDir(), "conquest_zones");
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			if (!created) {
				TESLog.getLogger().info("TESConquestGrid: file wasn't created");
			}
		}
		return dir;
	}

	public static ConquestEffective getConquestEffectIn(World world, TESConquestZone zone, TESFaction theFaction) {
		GridCoordPair gridCoords;
		if (!TESGenLayerWorld.loadedBiomeImage()) {
			TESGenLayerWorld.tryLoadBiomeImage();
		}
		List<TESFaction> cachedFacs = CACHED_ZONE_FACTIONS.get(gridCoords = GridCoordPair.forZone(zone));
		if (cachedFacs == null) {
			TESBiome biome;
			cachedFacs = new ArrayList<>();
			Collection<TESBiome> includedBiomes = new ArrayList<>();
			int[] mapMin = getMinCoordsOnMap(zone);
			int[] mapMax = getMaxCoordsOnMap(zone);
			int mapXMin = mapMin[0];
			int mapXMax = mapMax[0];
			int mapZMin = mapMin[1];
			int mapZMax = mapMax[1];
			for (int i = mapXMin; i < mapXMax; ++i) {
				for (int k = mapZMin; k < mapZMax; ++k) {
					biome = TESGenLayerWorld.getBiomeOrOcean(i, k);
					if (includedBiomes.contains(biome)) {
						continue;
					}
					includedBiomes.add(biome);
				}
			}
			block2:
			for (TESFaction fac : TESFaction.getPlayableAlignmentFactions()) {
				for (TESBiome biome2 : includedBiomes) {
					if (!biome2.getNPCSpawnList().isFactionPresent(world, fac)) {
						continue;
					}
					cachedFacs.add(fac);
					continue block2;
				}
			}
			CACHED_ZONE_FACTIONS.put(gridCoords, cachedFacs);
		}
		if (cachedFacs.contains(theFaction)) {
			return ConquestEffective.EFFECTIVE;
		}
		for (TESFaction allyFac : theFaction.getConquestBoostRelations()) {
			if (!cachedFacs.contains(allyFac)) {
				continue;
			}
			return ConquestEffective.ALLY_BOOST;
		}
		return ConquestEffective.NO_EFFECT;
	}

	public static int[] getMaxCoordsOnMap(TESConquestZone zone) {
		return new int[]{gridToMapCoord(zone.getGridX() + 1), gridToMapCoord(zone.getGridZ() + 1)};
	}

	public static int[] getMinCoordsOnMap(TESConquestZone zone) {
		return new int[]{gridToMapCoord(zone.getGridX()), gridToMapCoord(zone.getGridZ())};
	}

	private static TESConquestZone getZoneByEntityCoords(Entity entity) {
		int i = MathHelper.floor_double(entity.posX);
		int k = MathHelper.floor_double(entity.posZ);
		return getZoneByWorldCoords(i, k);
	}

	private static TESConquestZone getZoneByGridCoords(int i, int k) {
		if (i < 0 || i >= MathHelper.ceiling_float_int((float) TESGenLayerWorld.getImageWidth() / MAP_GRID_SCALE) || k < 0 || k >= MathHelper.ceiling_float_int((float) TESGenLayerWorld.getImageHeight() / MAP_GRID_SCALE)) {
			return DUMMY_ZONE;
		}
		GridCoordPair key = new GridCoordPair(i, k);
		TESConquestZone zone = ZONE_MAP.get(key);
		if (zone == null) {
			File zoneDat = getZoneDat(key);
			zone = loadZoneFromFile(zoneDat);
			if (zone == null) {
				zone = new TESConquestZone(i, k);
			}
			ZONE_MAP.put(key, zone);
		}
		return zone;
	}

	public static TESConquestZone getZoneByWorldCoords(int i, int k) {
		int x = worldToGridX(i);
		int z = worldToGridZ(k);
		return getZoneByGridCoords(x, z);
	}

	private static File getZoneDat(TESConquestZone zone) {
		GridCoordPair key = GridCoordPair.forZone(zone);
		return getZoneDat(key);
	}

	private static File getZoneDat(GridCoordPair key) {
		return new File(getConquestDir(), key.getGridX() + "." + key.getGridZ() + ".dat");
	}

	private static int gridToMapCoord(int i) {
		return i << 3;
	}

	public static void loadAllZones() {
		try {
			ZONE_MAP.clear();
			DIRTY_ZONES.clear();
			File dir = getConquestDir();
			if (dir.exists()) {
				for (File zoneDat : dir.listFiles()) {
					TESConquestZone zone;
					if (zoneDat.isDirectory() || !zoneDat.getName().endsWith(".dat") || (zone = loadZoneFromFile(zoneDat)) == null) {
						continue;
					}
					GridCoordPair key = GridCoordPair.forZone(zone);
					ZONE_MAP.put(key, zone);
				}
			}
			needsLoad = false;
			FMLLog.info("Hummel009: Loaded %s conquest zones", ZONE_MAP.size());
		} catch (Exception e) {
			FMLLog.severe("Error loading TES conquest zones");
			e.printStackTrace();
		}
	}

	private static TESConquestZone loadZoneFromFile(File zoneDat) {
		try {
			NBTTagCompound nbt = TESLevelData.loadNBTFromFile(zoneDat);
			if (nbt.hasNoTags()) {
				return null;
			}
			TESConquestZone zone = TESConquestZone.readFromNBT(nbt);
			if (zone.isEmpty()) {
				return null;
			}
			return zone;
		} catch (Exception e) {
			FMLLog.severe("Error loading TES conquest zone from %s", zoneDat.getName());
			e.printStackTrace();
			return null;
		}
	}

	public static void markZoneDirty(TESConquestZone zone) {
		GridCoordPair key = GridCoordPair.forZone(zone);
		if (ZONE_MAP.containsKey(key)) {
			DIRTY_ZONES.add(key);
		}
	}

	public static float onConquestKill(EntityPlayer entityplayer, TESFaction pledgeFaction, TESFaction enemyFaction, float alignBonus) {
		World world = entityplayer.worldObj;
		if (!world.isRemote && conquestEnabled(world) && TESLevelData.getData(entityplayer).getEnableConquestKills() && entityplayer.dimension == TESDimension.GAME_OF_THRONES.getDimensionID()) {
			TESConquestZone centralZone = getZoneByEntityCoords(entityplayer);
			float conqAmount = alignBonus * TESLevelData.getConquestRate();
			return doRadialConquest(world, centralZone, entityplayer, pledgeFaction, enemyFaction, conqAmount, conqAmount);
		}
		return 0.0f;
	}

	public static void saveChangedZones() {
		try {
			Collection<GridCoordPair> removes = new HashSet<>();
			for (GridCoordPair key : DIRTY_ZONES) {
				TESConquestZone zone = ZONE_MAP.get(key);
				if (zone == null) {
					continue;
				}
				saveZoneToFile(zone);
				if (!zone.isEmpty()) {
					continue;
				}
				removes.add(key);
			}
			DIRTY_ZONES.clear();
			for (GridCoordPair key : removes) {
				ZONE_MAP.remove(key);
			}
		} catch (Exception e) {
			FMLLog.severe("Error saving TES conquest zones");
			e.printStackTrace();
		}
	}

	private static void saveZoneToFile(TESConquestZone zone) {
		File zoneDat = getZoneDat(zone);
		try {
			if (zone.isEmpty()) {
				boolean deleted = zoneDat.delete();
				if (!deleted) {
					TESLog.getLogger().info("TESConquestGrid: file wasn't deleted");
				}
			} else {
				NBTTagCompound nbt = new NBTTagCompound();
				zone.writeToNBT(nbt);
				TESLevelData.saveNBTToFile(zoneDat, nbt);
			}
		} catch (Exception e) {
			FMLLog.severe("Error saving TES conquest zone to %s", zoneDat.getName());
			e.printStackTrace();
		}
	}

	public static void sendConquestGridTo(EntityPlayerMP entityplayer, TESFaction fac) {
		IMessage pkt = new TESPacketConquestGrid(fac, ZONE_MAP.values(), entityplayer.worldObj);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(pkt, entityplayer);
		TESPlayerData pd = TESLevelData.getData(entityplayer);
		if (fac == pd.getPledgeFaction()) {
			pd.addAchievement(TESAchievement.factionConquest);
		}
	}

	public static void updateZones(World world) {
		MinecraftServer srv;
		if (conquestEnabled(world) && (srv = MinecraftServer.getServer()) != null) {
			int tick = srv.getTickCounter();
			int interval = 6000;
			for (TESConquestZone zone : ZONE_MAP.values()) {
				if (zone.isEmpty() || IntMath.mod(tick, interval) != IntMath.mod(zone.hashCode(), interval)) {
					continue;
				}
				zone.checkForEmptiness(world);
			}
		}
	}

	private static int worldToGridX(int i) {
		int mapX = i >> 7;
		return mapX + TESGenLayerWorld.ORIGIN_X >> 3;
	}

	private static int worldToGridZ(int k) {
		int mapZ = k >> 7;
		return mapZ + TESGenLayerWorld.ORIGIN_Z >> 3;
	}

	public static boolean isNeedsLoad() {
		return needsLoad;
	}

	public static void setNeedsLoad(boolean needsLoad) {
		TESConquestGrid.needsLoad = needsLoad;
	}

	public enum ConquestEffective {
		EFFECTIVE, ALLY_BOOST, NO_EFFECT
	}

	public enum ConquestViewable {
		UNPLEDGED, CAN_VIEW, NEED_RANK
	}

	public static class ConquestViewableQuery {
		private final ConquestViewable result;
		private final TESFactionRank needRank;

		private ConquestViewableQuery(ConquestViewable res, TESFactionRank rank) {
			result = res;
			needRank = rank;
		}

		private static ConquestViewableQuery canView() {
			return new ConquestViewableQuery(ConquestViewable.CAN_VIEW, null);
		}

		public TESFactionRank getNeedRank() {
			return needRank;
		}

		public ConquestViewable getResult() {
			return result;
		}
	}

	private static class GridCoordPair {
		private final int gridX;
		private final int gridZ;

		private GridCoordPair(int i, int k) {
			gridX = i;
			gridZ = k;
		}

		private static GridCoordPair forZone(TESConquestZone zone) {
			return new GridCoordPair(zone.getGridX(), zone.getGridZ());
		}

		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (!(other instanceof GridCoordPair)) {
				return false;
			}
			GridCoordPair otherPair = (GridCoordPair) other;
			return gridX == otherPair.gridX && gridZ == otherPair.gridZ;
		}

		@Override
		public int hashCode() {
			int i = 1664525 * gridX + 1013904223;
			int j = 1664525 * (gridZ ^ 0xDEADBEEF) + 1013904223;
			return i ^ j;
		}

		private int getGridX() {
			return gridX;
		}

		private int getGridZ() {
			return gridZ;
		}
	}
}