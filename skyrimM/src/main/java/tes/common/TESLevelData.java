package tes.common;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.TES;
import tes.common.database.TESTitle;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipData;
import tes.common.network.*;
import tes.common.util.TESLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.StatCollector;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TESLevelData {
	private static final Map<UUID, TESPlayerData> PLAYER_DATA_MAP = new HashMap<>();
	private static final Map<UUID, Optional<TESTitle.PlayerTitle>> PLAYER_TITLE_OFFLINE_CACHE_MAP = new HashMap<>();

	private static int madePortal;
	private static int madeGameOfThronesPortal;
	private static int overworldPortalX;
	private static int overworldPortalY;
	private static int overworldPortalZ;
	private static int gameOfThronesPortalX;
	private static int gameOfThronesPortalY;
	private static int gameOfThronesPortalZ;
	private static boolean clientSideThisServerFeastMode;
	private static boolean clientSideThisServerFellowshipCreation;
	private static int clientSideThisServerFellowshipMaxSize;
	private static boolean clientSideThisServerEnchanting;
	private static boolean clientSideThisServerEnchantingTES;
	private static boolean clientSideThisServerStrictFactionTitleRequirements;
	private static int clientSideThisServerCustomWaypointMinY;

	private static boolean needsLoad = true;

	private static int structuresBanned;
	private static int waypointCooldownMax;
	private static int waypointCooldownMin;
	private static boolean enableAlignmentZones;
	private static float conquestRate = 1.0f;
	private static EnumDifficulty difficulty;
	private static boolean difficultyLock;
	private static boolean needsSave;

	private TESLevelData() {
	}

	public static boolean anyDataNeedsSave() {
		if (needsSave || TESSpawnDamping.isNeedsSave()) {
			return true;
		}
		for (TESPlayerData pd : PLAYER_DATA_MAP.values()) {
			if (pd.needsSave()) {
				return true;
			}
		}
		return false;
	}

	public static void destroyAllPlayerData() {
		PLAYER_DATA_MAP.clear();
	}

	public static Set<String> getBannedStructurePlayersUsernames() {
		Set<String> players = new HashSet<>();
		for (UUID uuid : PLAYER_DATA_MAP.keySet()) {
			String username;
			if (getData(uuid).getStructuresBanned()) {
				GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(uuid);
				if (StringUtils.isBlank(profile.getName())) {
					MinecraftServer.getServer().func_147130_as().fillProfileProperties(profile, true);
				}
				if (!StringUtils.isBlank(username = profile.getName())) {
					players.add(username);
				}
			}
		}
		return players;
	}

	public static float getConquestRate() {
		return conquestRate;
	}

	public static void setConquestRate(float f) {
		conquestRate = f;
		markDirty();
	}

	public static TESPlayerData getData(EntityPlayer entityplayer) {
		return getData(entityplayer.getUniqueID());
	}

	public static TESPlayerData getData(UUID player) {
		TESPlayerData pd = PLAYER_DATA_MAP.get(player);
		if (pd == null) {
			pd = loadData(player);
			PLAYER_TITLE_OFFLINE_CACHE_MAP.remove(player);
			if (pd == null) {
				pd = new TESPlayerData(player);
			}
			PLAYER_DATA_MAP.put(player, pd);
		}
		return pd;
	}

	private static File getTESDat() {
		return new File(getOrCreateTESDir(), "TES.dat");
	}

	private static File getTESPlayerDat(UUID player) {
		File playerDir = new File(getOrCreateTESDir(), "players");
		if (!playerDir.exists()) {
			boolean created = playerDir.mkdirs();
			if (!created) {
				TESLog.getLogger().info("TESLevelData: directory wasn't created");
			}
		}
		return new File(playerDir, player.toString() + ".dat");
	}

	public static String getHMSTime_Seconds(int secs) {
		return getHMSTime_Ticks(secs * 20);
	}

	public static String getHMSTime_Ticks(int ticks) {
		int hours = ticks / 72000;
		int minutes = ticks % 72000 / 1200;
		int seconds = ticks % 72000 % 1200 / 20;
		String sHours = StatCollector.translateToLocalFormatted("TES.gui.time.hours", hours);
		String sMinutes = StatCollector.translateToLocalFormatted("TES.gui.time.minutes", minutes);
		String sSeconds = StatCollector.translateToLocalFormatted("TES.gui.time.seconds", seconds);
		if (hours > 0) {
			return StatCollector.translateToLocalFormatted("TES.gui.time.format.hms", sHours, sMinutes, sSeconds);
		}
		if (minutes > 0) {
			return StatCollector.translateToLocalFormatted("TES.gui.time.format.ms", sMinutes, sSeconds);
		}
		return StatCollector.translateToLocalFormatted("TES.gui.time.format.s", sSeconds);
	}

	public static File getOrCreateTESDir() {
		File file = new File(DimensionManager.getCurrentSaveRootDirectory(), "TES");
		if (!file.exists()) {
			boolean created = file.mkdirs();
			if (!created) {
				TESLog.getLogger().info("TESLevelData: directory wasn't created");
			}
		}
		return file;
	}

	public static TESTitle.PlayerTitle getPlayerTitleWithOfflineCache(UUID player) {
		if (PLAYER_DATA_MAP.containsKey(player)) {
			return PLAYER_DATA_MAP.get(player).getPlayerTitle();
		}
		if (PLAYER_TITLE_OFFLINE_CACHE_MAP.containsKey(player)) {
			return PLAYER_TITLE_OFFLINE_CACHE_MAP.get(player).orElse(null);
		}
		TESPlayerData pd = loadData(player);
		if (pd != null) {
			TESTitle.PlayerTitle playerTitle = pd.getPlayerTitle();
			PLAYER_TITLE_OFFLINE_CACHE_MAP.put(player, Optional.ofNullable(playerTitle));
			return playerTitle;
		}
		return null;
	}

	public static int getWaypointCooldownMax() {
		return waypointCooldownMax;
	}

	public static int getWaypointCooldownMin() {
		return waypointCooldownMin;
	}

	public static boolean isDifficultyLocked() {
		return difficultyLock;
	}

	public static void setDifficultyLocked(boolean flag) {
		difficultyLock = flag;
		markDirty();
	}

	public static boolean isPlayerBannedForStructures(EntityPlayer entityplayer) {
		return getData(entityplayer).getStructuresBanned();
	}

	public static void load() {
		try {
			NBTTagCompound levelData = loadNBTFromFile(getTESDat());
			File oldTESDat = new File(DimensionManager.getCurrentSaveRootDirectory(), "TES.dat");
			if (oldTESDat.exists()) {
				levelData = loadNBTFromFile(oldTESDat);
				boolean created = oldTESDat.delete();
				if (!created) {
					TESLog.getLogger().info("TESLevelData: file wasn't deleted");
				}
				if (levelData.hasKey("PlayerData")) {
					NBTTagList playerDataTags = levelData.getTagList("PlayerData", 10);
					for (int i = 0; i < playerDataTags.tagCount(); ++i) {
						NBTTagCompound nbt = playerDataTags.getCompoundTagAt(i);
						UUID player = UUID.fromString(nbt.getString("PlayerUUID"));
						saveNBTToFile(getTESPlayerDat(player), nbt);
					}
				}
			}
			madePortal = levelData.getInteger("MadePortal");
			madeGameOfThronesPortal = levelData.getInteger("MadeGameOfThronesPortal");
			overworldPortalX = levelData.getInteger("OverworldX");
			overworldPortalY = levelData.getInteger("OverworldY");
			overworldPortalZ = levelData.getInteger("OverworldZ");
			gameOfThronesPortalX = levelData.getInteger("GameOfThronesX");
			gameOfThronesPortalY = levelData.getInteger("GameOfThronesY");
			gameOfThronesPortalZ = levelData.getInteger("GameOfThronesZ");
			structuresBanned = levelData.getInteger("StructuresBanned");
			if (levelData.hasKey("WpCdMax")) {
				waypointCooldownMax = levelData.getInteger("WpCdMax");
			} else {
				waypointCooldownMax = 600;
			}
			if (levelData.hasKey("WpCdMin")) {
				waypointCooldownMin = levelData.getInteger("WpCdMin");
			} else {
				waypointCooldownMin = 60;
			}
			enableAlignmentZones = !levelData.hasKey("AlignmentZones") || levelData.getBoolean("AlignmentZones");
			if (levelData.hasKey("ConqRate")) {
				conquestRate = levelData.getFloat("ConqRate");
			} else {
				conquestRate = 1.0f;
			}
			if (levelData.hasKey("SavedDifficulty")) {
				int id = levelData.getInteger("SavedDifficulty");
				difficulty = EnumDifficulty.getDifficultyEnum(id);
				TES.proxy.setClientDifficulty(difficulty);
			} else {
				difficulty = null;
			}
			difficultyLock = levelData.getBoolean("DifficultyLock");
			destroyAllPlayerData();
			TESDate.loadDates(levelData);
			TESSpawnDamping.loadAll();
			needsLoad = false;
			needsSave = true;
			save();
		} catch (Exception e) {
			FMLLog.severe("Error loading TES data");
			e.printStackTrace();
		}
	}

	private static TESPlayerData loadData(UUID player) {
		try {
			NBTTagCompound nbt = loadNBTFromFile(getTESPlayerDat(player));
			TESPlayerData pd = new TESPlayerData(player);
			pd.load(nbt);
			return pd;
		} catch (Exception e) {
			FMLLog.severe("Error loading TES player data for %s", player);
			e.printStackTrace();
			return null;
		}
	}

	public static NBTTagCompound loadNBTFromFile(File file) throws IOException {
		if (file.exists()) {
			FileInputStream fis = new FileInputStream(file);
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(fis);
			fis.close();
			return nbt;
		}
		return new NBTTagCompound();
	}

	public static void markDirty() {
		needsSave = true;
	}

	public static void markGameOfThronesPortalLocation(int i, int j, int k) {
		IMessage packet = new TESPacketPortalPos(i, j, k);
		TESPacketHandler.NETWORK_WRAPPER.sendToAll(packet);
		markDirty();
	}

	public static void markOverworldPortalLocation(int i, int j, int k) {
		overworldPortalX = i;
		overworldPortalY = j;
		overworldPortalZ = k;
		markDirty();
	}

	public static void save() {
		try {
			if (needsSave) {
				File TES_dat = getTESDat();
				if (!TES_dat.exists()) {
					saveNBTToFile(TES_dat, new NBTTagCompound());
				}
				NBTTagCompound levelData = new NBTTagCompound();
				levelData.setInteger("MadePortal", madePortal);
				levelData.setInteger("MadeGameOfThronesPortal", madeGameOfThronesPortal);
				levelData.setInteger("OverworldX", overworldPortalX);
				levelData.setInteger("OverworldY", overworldPortalY);
				levelData.setInteger("OverworldZ", overworldPortalZ);
				levelData.setInteger("GameOfThronesX", gameOfThronesPortalX);
				levelData.setInteger("GameOfThronesY", gameOfThronesPortalY);
				levelData.setInteger("GameOfThronesZ", gameOfThronesPortalZ);
				levelData.setInteger("StructuresBanned", structuresBanned);
				levelData.setInteger("WpCdMax", waypointCooldownMax);
				levelData.setInteger("WpCdMin", waypointCooldownMin);
				levelData.setBoolean("AlignmentZones", enableAlignmentZones);
				levelData.setFloat("ConqRate", conquestRate);
				if (difficulty != null) {
					levelData.setInteger("SavedDifficulty", difficulty.getDifficultyId());
				}
				levelData.setBoolean("DifficultyLock", difficultyLock);
				TESDate.saveDates(levelData);
				saveNBTToFile(TES_dat, levelData);
				needsSave = false;
			}
			for (Map.Entry<UUID, TESPlayerData> e : PLAYER_DATA_MAP.entrySet()) {
				UUID player = e.getKey();
				TESPlayerData pd = e.getValue();
				if (pd.needsSave()) {
					saveData(player);
				}
			}
			if (TESSpawnDamping.isNeedsSave()) {
				TESSpawnDamping.saveAll();
			}
		} catch (Exception e) {
			FMLLog.severe("Error saving TES data");
			e.printStackTrace();
		}
	}

	private static void saveAndClearData(UUID player) {
		TESPlayerData pd = PLAYER_DATA_MAP.get(player);
		if (pd != null) {
			if (pd.needsSave()) {
				saveData(player);
			}
			PLAYER_TITLE_OFFLINE_CACHE_MAP.put(player, Optional.ofNullable(pd.getPlayerTitle()));
			PLAYER_DATA_MAP.remove(player);
			return;
		}
		FMLLog.severe("Attempted to clear TES player data for %s; no data found", player);
	}

	public static void saveAndClearUnusedPlayerData() {
		Collection<UUID> clearing = new ArrayList<>();
		for (UUID player : PLAYER_DATA_MAP.keySet()) {
			boolean foundPlayer = false;
			for (WorldServer world : MinecraftServer.getServer().worldServers) {
				if (world.func_152378_a(player) != null) {
					foundPlayer = true;
					break;
				}
			}
			if (!foundPlayer) {
				clearing.add(player);
			}
		}
		for (UUID player : clearing) {
			saveAndClearData(player);
		}
	}

	private static void saveData(UUID player) {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			TESPlayerData pd = PLAYER_DATA_MAP.get(player);
			pd.save(nbt);
			saveNBTToFile(getTESPlayerDat(player), nbt);
		} catch (Exception e) {
			FMLLog.severe("Error saving TES player data for %s", player);
			e.printStackTrace();
		}
	}

	public static void saveNBTToFile(File file, NBTTagCompound nbt) throws IOException {
		CompressedStreamTools.writeCompressed(nbt, Files.newOutputStream(file.toPath()));
	}

	public static void sendAlignmentToAllPlayersInWorld(Entity entityplayer, World world) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer worldPlayer : players) {
			IMessage packet = new TESPacketAlignment(entityplayer.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) worldPlayer);
		}
	}

	public static void sendAllAlignmentsInWorldToPlayer(EntityPlayer entityplayer, World world) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer worldPlayer : players) {
			IMessage packet = new TESPacketAlignment(worldPlayer.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public static void sendAllCapesInWorldToPlayer(EntityPlayer entityplayer, World world) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer worldPlayer : players) {
			IMessage packet = new TESPacketCape(worldPlayer.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public static void sendAllShieldsInWorldToPlayer(EntityPlayer entityplayer, World world) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer worldPlayer : players) {
			IMessage packet = new TESPacketShield(worldPlayer.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public static void sendCapeToAllPlayersInWorld(EntityPlayer entityplayer, World world) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer worldPlayer : players) {
			IMessage packet = new TESPacketCape(entityplayer.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) worldPlayer);
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
		}
	}

	public static void sendLoginPacket(EntityPlayerMP entityplayer) {
		TESPacketLogin packet = new TESPacketLogin();
		packet.setSwordPortalX(gameOfThronesPortalX);
		packet.setSwordPortalY(gameOfThronesPortalY);
		packet.setSwordPortalZ(gameOfThronesPortalZ);
		packet.setFtCooldownMax(waypointCooldownMax);
		packet.setFtCooldownMin(waypointCooldownMin);
		packet.setDifficulty(difficulty);
		packet.setDifficultyLocked(difficultyLock);
		packet.setAlignmentZones(enableAlignmentZones);
		packet.setFeastMode(TESConfig.canAlwaysEat);
		packet.setFellowshipCreation(TESConfig.enableFellowshipCreation);
		packet.setFellowshipMaxSize(TESConfig.fellowshipMaxSize);
		packet.setEnchanting(TESConfig.enchantingVanilla);
		packet.setEnchantingTES(TESConfig.enchantingTES);
		packet.setStrictFactionTitleRequirements(TESConfig.strictFactionTitleRequirements);
		packet.setCustomWaypointMinY(TESConfig.customWaypointMinY);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
	}

	public static void sendPlayerData(EntityPlayerMP entityplayer) {
		try {
			TESPlayerData pd = getData(entityplayer);
			pd.sendPlayerData(entityplayer);
		} catch (Exception e) {
			FMLLog.severe("Failed to send player data to player " + entityplayer.getCommandSenderName());
			e.printStackTrace();
		}
	}

	public static void sendPlayerLocationsToPlayer(EntityPlayer sendPlayer, World world) {
		TESPacketUpdatePlayerLocations packetLocations = new TESPacketUpdatePlayerLocations();
		boolean isOp = MinecraftServer.getServer().getConfigurationManager().func_152596_g(sendPlayer.getGameProfile());
		boolean creative = sendPlayer.capabilities.isCreativeMode;
		TESPlayerData playerData = getData(sendPlayer);
		Collection<TESFellowship> fellowshipsMapShow = new ArrayList<>();
		for (UUID fsID : playerData.getFellowshipIDs()) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null && fs.getShowMapLocations()) {
				fellowshipsMapShow.add(fs);
			}
		}
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer otherPlayer : players) {
			boolean show;
			if (otherPlayer != sendPlayer) {
				show = !getData(otherPlayer).getHideMapLocation();
				if (!isOp && getData(otherPlayer).getAdminHideMap() || TESConfig.forceMapLocations == 1) {
					show = false;
				} else if (TESConfig.forceMapLocations == 2) {
					show = true;
				} else if (!show) {
					if (isOp && creative) {
						show = true;
					} else if (!playerData.isSiegeActive()) {
						for (TESFellowship fs : fellowshipsMapShow) {
							if (fs.containsPlayer(otherPlayer.getUniqueID())) {
								show = true;
								break;
							}
						}
					}
				}
				if (show) {
					packetLocations.addPlayerLocation(otherPlayer.getGameProfile(), otherPlayer.posX, otherPlayer.posZ);
				}
			}
		}
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packetLocations, (EntityPlayerMP) sendPlayer);
	}

	public static void sendShieldToAllPlayersInWorld(Entity entityplayer, World world) {
		List<EntityPlayer> players = world.playerEntities;
		for (EntityPlayer worldPlayer : players) {
			IMessage packet = new TESPacketShield(entityplayer.getUniqueID());
			TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) worldPlayer);
		}
	}

	public static void setPlayerBannedForStructures(String username, boolean flag) {
		UUID uuid = UUID.fromString(PreYggdrasilConverter.func_152719_a(username));
		getData(uuid).setStructuresBanned(flag);
	}

	public static void setSavedDifficulty(EnumDifficulty d) {
		difficulty = d;
		markDirty();
	}

	public static void setWaypointCooldown(int max, int min) {
		int min1 = min;
		int max1 = Math.max(0, max);
		min1 = Math.max(0, min1);
		if (min1 > max1) {
			min1 = max1;
		}
		waypointCooldownMax = max1;
		waypointCooldownMin = min1;
		markDirty();
		if (!TES.proxy.isClient()) {
			List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for (EntityPlayerMP entityplayer : players) {
				IMessage packet = new TESPacketFTCooldown(waypointCooldownMax, waypointCooldownMin);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
			}
		}
	}

	public static int getMadePortal() {
		return madePortal;
	}

	public static void setMadePortal(int i) {
		madePortal = i;
		markDirty();
	}

	public static int getMadeGameOfThronesPortal() {
		return madeGameOfThronesPortal;
	}

	public static void setMadeGameOfThronesPortal(int i) {
		madeGameOfThronesPortal = i;
		markDirty();
	}

	public static int getStructuresBanned() {
		return structuresBanned;
	}

	public static void setStructuresBanned(boolean banned) {
		if (banned) {
			structuresBanned = 1;
		} else {
			structuresBanned = 0;
		}
		markDirty();
	}

	public static boolean isEnableAlignmentZones() {
		return enableAlignmentZones;
	}

	public static void setEnableAlignmentZones(boolean flag) {
		enableAlignmentZones = flag;
		markDirty();
		if (!TES.proxy.isClient()) {
			List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
			for (EntityPlayerMP entityplayer : players) {
				IMessage packet = new TESPacketEnableAlignmentZones(enableAlignmentZones);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, entityplayer);
			}
		}
	}

	public static int getOverworldPortalX() {
		return overworldPortalX;
	}

	public static int getOverworldPortalY() {
		return overworldPortalY;
	}

	public static int getOverworldPortalZ() {
		return overworldPortalZ;
	}

	public static int getGameOfThronesPortalX() {
		return gameOfThronesPortalX;
	}

	public static void setGameOfThronesPortalX(int gameOfThronesPortalX) {
		TESLevelData.gameOfThronesPortalX = gameOfThronesPortalX;
	}

	public static int getGameOfThronesPortalY() {
		return gameOfThronesPortalY;
	}

	public static void setGameOfThronesPortalY(int gameOfThronesPortalY) {
		TESLevelData.gameOfThronesPortalY = gameOfThronesPortalY;
	}

	public static int getGameOfThronesPortalZ() {
		return gameOfThronesPortalZ;
	}

	public static void setGameOfThronesPortalZ(int gameOfThronesPortalZ) {
		TESLevelData.gameOfThronesPortalZ = gameOfThronesPortalZ;
	}

	public static boolean isClientSideThisServerFeastMode() {
		return clientSideThisServerFeastMode;
	}

	public static void setClientSideThisServerFeastMode(boolean clientSideThisServerFeastMode) {
		TESLevelData.clientSideThisServerFeastMode = clientSideThisServerFeastMode;
	}

	public static boolean isClientSideThisServerFellowshipCreation() {
		return clientSideThisServerFellowshipCreation;
	}

	public static void setClientSideThisServerFellowshipCreation(boolean clientSideThisServerFellowshipCreation) {
		TESLevelData.clientSideThisServerFellowshipCreation = clientSideThisServerFellowshipCreation;
	}

	public static int getClientSideThisServerFellowshipMaxSize() {
		return clientSideThisServerFellowshipMaxSize;
	}

	public static void setClientSideThisServerFellowshipMaxSize(int clientSideThisServerFellowshipMaxSize) {
		TESLevelData.clientSideThisServerFellowshipMaxSize = clientSideThisServerFellowshipMaxSize;
	}

	public static boolean isClientSideThisServerEnchanting() {
		return clientSideThisServerEnchanting;
	}

	public static void setClientSideThisServerEnchanting(boolean clientSideThisServerEnchanting) {
		TESLevelData.clientSideThisServerEnchanting = clientSideThisServerEnchanting;
	}

	public static boolean isClientSideThisServerEnchantingTES() {
		return clientSideThisServerEnchantingTES;
	}

	public static void setClientSideThisServerEnchantingTES(boolean clientSideThisServerEnchantingTES) {
		TESLevelData.clientSideThisServerEnchantingTES = clientSideThisServerEnchantingTES;
	}

	public static boolean isClientSideThisServerStrictFactionTitleRequirements() {
		return clientSideThisServerStrictFactionTitleRequirements;
	}

	public static void setClientSideThisServerStrictFactionTitleRequirements(boolean clientSideThisServerStrictFactionTitleRequirements) {
		TESLevelData.clientSideThisServerStrictFactionTitleRequirements = clientSideThisServerStrictFactionTitleRequirements;
	}

	public static int getClientSideThisServerCustomWaypointMinY() {
		return clientSideThisServerCustomWaypointMinY;
	}

	public static void setClientSideThisServerCustomWaypointMinY(int clientSideThisServerCustomWaypointMinY) {
		TESLevelData.clientSideThisServerCustomWaypointMinY = clientSideThisServerCustomWaypointMinY;
	}

	public static boolean isNeedsLoad() {
		return needsLoad;
	}

	public static void setNeedsLoad(boolean needsLoad) {
		TESLevelData.needsLoad = needsLoad;
	}
}