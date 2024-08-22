package tes.common.faction;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLLog;
import tes.common.TESLevelData;
import tes.common.util.TESLog;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class TESFactionBounties {
	private static final Map<TESFaction, TESFactionBounties> FACTION_BOUNTY_MAP = new EnumMap<>(TESFaction.class);

	private static boolean needsLoad = true;

	private final TESFaction theFaction;
	private final Map<UUID, PlayerData> playerList = new HashMap<>();

	private boolean needsSave;

	private TESFactionBounties(TESFaction f) {
		theFaction = f;
	}

	public static boolean anyDataNeedsSave() {
		for (TESFactionBounties fb : FACTION_BOUNTY_MAP.values()) {
			if (!fb.needsSave) {
				continue;
			}
			return true;
		}
		return false;
	}

	public static TESFactionBounties forFaction(TESFaction fac) {
		TESFactionBounties bounties = FACTION_BOUNTY_MAP.get(fac);
		if (bounties == null) {
			bounties = loadFaction(fac);
			if (bounties == null) {
				bounties = new TESFactionBounties(fac);
			}
			FACTION_BOUNTY_MAP.put(fac, bounties);
		}
		return bounties;
	}

	private static File getBountiesDir() {
		File dir = new File(TESLevelData.getOrCreateTESDir(), "factionbounties");
		if (!dir.exists()) {
			boolean created = dir.mkdirs();
			if (!created) {
				TESLog.getLogger().info("TESFactionBounties: directory wasn't created");
			}
		}
		return dir;
	}

	private static File getFactionFile(TESFaction f, boolean findLegacy) {
		File defaultFile = new File(getBountiesDir(), f.codeName() + ".dat");
		if (findLegacy) {
			boolean created = defaultFile.exists();
			if (created) {
				TESLog.getLogger().info("TESFactionBounties: file already exists");
			}
		}
		return defaultFile;
	}

	public static void loadAll() {
		try {
			FACTION_BOUNTY_MAP.clear();
			needsLoad = false;
			saveAll();
		} catch (Exception e) {
			FMLLog.severe("Error loading TES faction bounty data");
			e.printStackTrace();
		}
	}

	private static TESFactionBounties loadFaction(TESFaction fac) {
		File file = getFactionFile(fac, true);
		try {
			NBTTagCompound nbt = TESLevelData.loadNBTFromFile(file);
			if (nbt.hasNoTags()) {
				return null;
			}
			TESFactionBounties fb = new TESFactionBounties(fac);
			fb.readFromNBT(nbt);
			return fb;
		} catch (Exception e) {
			FMLLog.severe("Error loading TES faction bounty data for %s", fac.codeName());
			e.printStackTrace();
			return null;
		}
	}

	public static void saveAll() {
		try {
			for (TESFactionBounties fb : FACTION_BOUNTY_MAP.values()) {
				if (!fb.needsSave) {
					continue;
				}
				saveFaction(fb);
				fb.needsSave = false;
			}
		} catch (Exception e) {
			FMLLog.severe("Error saving TES faction bounty data");
			e.printStackTrace();
		}
	}

	private static void saveFaction(TESFactionBounties fb) {
		try {
			NBTTagCompound nbt = new NBTTagCompound();
			fb.writeToNBT(nbt);
			TESLevelData.saveNBTToFile(getFactionFile(fb.theFaction, false), nbt);
		} catch (Exception e) {
			FMLLog.severe("Error saving TES faction bounty data for %s", fb.theFaction.codeName());
			e.printStackTrace();
		}
	}

	public static void updateAll() {
		for (TESFactionBounties fb : FACTION_BOUNTY_MAP.values()) {
			fb.update();
		}
	}

	public static boolean isNeedsLoad() {
		return needsLoad;
	}

	public static void setNeedsLoad(boolean needsLoad) {
		TESFactionBounties.needsLoad = needsLoad;
	}

	public List<PlayerData> findBountyTargets(int killAmount) {
		List<PlayerData> players = new ArrayList<>();
		for (PlayerData pd : playerList.values()) {
			if (pd.recentlyBountyKilled() || pd.getNumKills() < killAmount) {
				continue;
			}
			players.add(pd);
		}
		return players;
	}

	public PlayerData forPlayer(EntityPlayer entityplayer) {
		return forPlayer(entityplayer.getUniqueID());
	}

	public PlayerData forPlayer(UUID id) {
		return playerList.computeIfAbsent(id, k -> new PlayerData(this, id));
	}

	@SuppressWarnings("MethodOnlyUsedFromInnerClass")
	private void markDirty() {
		needsSave = true;
	}

	private void readFromNBT(NBTTagCompound nbt) {
		playerList.clear();
		if (nbt.hasKey("PlayerList")) {
			NBTTagList playerTags = nbt.getTagList("PlayerList", 10);
			for (int i = 0; i < playerTags.tagCount(); ++i) {
				NBTTagCompound playerData = playerTags.getCompoundTagAt(i);
				UUID id = UUID.fromString(playerData.getString("UUID"));
				PlayerData pd = new PlayerData(this, id);
				pd.readFromNBT(playerData);
				playerList.put(id, pd);
			}
		}
	}

	private void update() {
		for (PlayerData pd : playerList.values()) {
			pd.update();
		}
	}

	private void writeToNBT(NBTTagCompound nbt) {
		NBTTagList playerTags = new NBTTagList();
		for (Map.Entry<UUID, PlayerData> e : playerList.entrySet()) {
			UUID id = e.getKey();
			PlayerData pd = e.getValue();
			if (!pd.shouldSave()) {
				continue;
			}
			NBTTagCompound playerData = new NBTTagCompound();
			playerData.setString("UUID", id.toString());
			pd.writeToNBT(playerData);
			playerTags.appendTag(playerData);
		}
		nbt.setTag("PlayerList", playerTags);
	}

	public static class PlayerData {
		private final Collection<KillRecord> killRecords = new ArrayList<>();
		private final TESFactionBounties bountyList;
		private final UUID playerID;

		private String username;
		private int recentBountyKilled;

		private PlayerData(TESFactionBounties b, UUID id) {
			bountyList = b;
			playerID = id;
		}

		public String findUsername() {
			if (username == null) {
				GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(playerID);
				if (profile == null || StringUtils.isBlank(profile.getName())) {
					String name = UsernameCache.getLastKnownUsername(playerID);
					if (name != null) {
						profile = new GameProfile(playerID, name);
					} else {
						profile = new GameProfile(playerID, "");
						MinecraftServer.getServer().func_147130_as().fillProfileProperties(profile, true);
					}
				}
				username = profile.getName();
			}
			return username;
		}

		public int getNumKills() {
			return killRecords.size();
		}

		private void markDirty() {
			bountyList.markDirty();
		}

		private void readFromNBT(NBTTagCompound nbt) {
			killRecords.clear();
			if (nbt.hasKey("KillRecords")) {
				NBTTagList recordTags = nbt.getTagList("KillRecords", 10);
				for (int i = 0; i < recordTags.tagCount(); ++i) {
					NBTTagCompound killData = recordTags.getCompoundTagAt(i);
					KillRecord kr = new KillRecord();
					kr.readFromNBT(killData);
					killRecords.add(kr);
				}
			}
			recentBountyKilled = nbt.getInteger("RecentBountyKilled");
		}

		public boolean recentlyBountyKilled() {
			return recentBountyKilled > 0;
		}

		public void recordBountyKilled() {
			recentBountyKilled = 864000;
			markDirty();
		}

		public void recordNewKill() {
			killRecords.add(new KillRecord());
			markDirty();
		}

		private boolean shouldSave() {
			return !killRecords.isEmpty() || recentBountyKilled > 0;
		}

		private void update() {
			boolean minorChanges = false;
			if (recentBountyKilled > 0) {
				--recentBountyKilled;
				minorChanges = true;
			}
			Collection<KillRecord> toRemove = new ArrayList<>();
			for (KillRecord kr : killRecords) {
				kr.setTimeElapsed(kr.getTimeElapsed() - 1);
				if (kr.getTimeElapsed() <= 0) {
					toRemove.add(kr);
				}
				minorChanges = true;
			}
			if (!toRemove.isEmpty()) {
				killRecords.removeAll(toRemove);
			}
			if (minorChanges && MinecraftServer.getServer().getTickCounter() % 600 == 0) {
				markDirty();
			}
		}

		private void writeToNBT(NBTTagCompound nbt) {
			NBTTagList recordTags = new NBTTagList();
			for (KillRecord kr : killRecords) {
				NBTTagCompound killData = new NBTTagCompound();
				kr.writeToNBT(killData);
				recordTags.appendTag(killData);
			}
			nbt.setTag("KillRecords", recordTags);
			nbt.setInteger("RecentBountyKilled", recentBountyKilled);
		}

		public UUID getPlayerID() {
			return playerID;
		}

		private static class KillRecord {
			private int timeElapsed = 3456000;

			private void readFromNBT(NBTTagCompound nbt) {
				timeElapsed = nbt.getInteger("Time");
			}

			private void writeToNBT(NBTTagCompound nbt) {
				nbt.setInteger("Time", timeElapsed);
			}

			private int getTimeElapsed() {
				return timeElapsed;
			}

			private void setTimeElapsed(int timeElapsed) {
				this.timeElapsed = timeElapsed;
			}
		}
	}
}