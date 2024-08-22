package tes.common.world.map;

import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import tes.common.fellowship.TESFellowshipData;
import tes.common.network.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TESCustomWaypoint implements TESAbstractWaypoint {
	private final int mapX;
	private final int mapY;
	private final int xCoord;
	private final int zCoord;
	private final int ID;
	private String customName;
	private int yCoord;
	private List<UUID> sharedFellowshipIDs = new ArrayList<>();
	private UUID sharingPlayer;
	private String sharingPlayerName;
	private boolean sharedUnlocked;
	private boolean sharedHidden;

	public TESCustomWaypoint(String name, int i, int j, int posX, int posY, int posZ, int id) {
		customName = name;
		mapX = i;
		mapY = j;
		xCoord = posX;
		yCoord = posY;
		zCoord = posZ;
		ID = id;
	}

	public static void createForPlayer(String name, EntityPlayer entityplayer) {
		TESPlayerData playerData = TESLevelData.getData(entityplayer);
		int cwpID = playerData.getNextCwpID();
		int i = MathHelper.floor_double(entityplayer.posX);
		int j = MathHelper.floor_double(entityplayer.boundingBox.minY);
		int k = MathHelper.floor_double(entityplayer.posZ);
		int mapX = TESWaypoint.worldToMapX(i);
		int mapY = TESWaypoint.worldToMapZ(k);
		TESCustomWaypoint cwp = new TESCustomWaypoint(name, mapX, mapY, i, j, k, cwpID);
		playerData.addCustomWaypoint(cwp);
		playerData.incrementNextCwpID();
	}

	public static TESCustomWaypoint readFromNBT(NBTTagCompound nbt, TESPlayerData pd) {
		String name = nbt.getString("Name");
		int x = nbt.getInteger("X");
		int y = nbt.getInteger("Y");
		int xCoord = nbt.getInteger("XCoord");
		int zCoord = nbt.getInteger("ZCoord");
		int yCoord = nbt.hasKey("YCoord") ? nbt.getInteger("YCoord") : -1;
		int ID = nbt.getInteger("ID");
		TESCustomWaypoint cwp = new TESCustomWaypoint(name, x, y, xCoord, yCoord, zCoord, ID);
		cwp.sharedFellowshipIDs.clear();
		if (nbt.hasKey("SharedFellowships")) {
			NBTTagList sharedFellowshipTags = nbt.getTagList("SharedFellowships", 8);
			for (int i = 0; i < sharedFellowshipTags.tagCount(); ++i) {
				UUID fsID = UUID.fromString(sharedFellowshipTags.getStringTagAt(i));
				cwp.sharedFellowshipIDs.add(fsID);
			}
		}
		cwp.validateFellowshipIDs(pd);
		return cwp;
	}

	public static String validateCustomName(String name) {
		String name1 = name;
		if (!StringUtils.isBlank(name1 = StringUtils.trim(name1))) {
			return name1;
		}
		return null;
	}

	private static boolean isSafeBlock(IBlockAccess world, int i, int j, int k) {
		Block below = world.getBlock(i, j - 1, k);
		Block block = world.getBlock(i, j, k);
		Block above = world.getBlock(i, j + 1, k);
		return below.getMaterial().blocksMovement() && !block.isNormalCube(world, i, j, k) && !above.isNormalCube(world, i, j + 1, k) && !block.getMaterial().isLiquid() && block.getMaterial() != Material.fire && !above.getMaterial().isLiquid() && above.getMaterial() != Material.fire;
	}

	public void addSharedFellowship(UUID fsID) {
		if (!sharedFellowshipIDs.contains(fsID)) {
			sharedFellowshipIDs.add(fsID);
		}
	}

	public boolean canUnlockShared(EntityPlayer entityplayer) {
		if (yCoord >= 0) {
			double distSq = entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5);
			return distSq <= 1000000.0;
		}
		return false;
	}

	public TESCustomWaypoint createCopyOfShared(UUID sharer) {
		TESCustomWaypoint copy = new TESCustomWaypoint(customName, mapX, mapY, xCoord, yCoord, zCoord, ID);
		copy.setSharingPlayerID(sharer);
		copy.sharedFellowshipIDs = new ArrayList<>(sharedFellowshipIDs);
		return copy;
	}

	public TESPacketShareCWPClient getClientAddFellowshipPacket(UUID fsID) {
		return new TESPacketShareCWPClient(ID, fsID, true);
	}

	public TESPacketDeleteCWPClient getClientDeletePacket() {
		return new TESPacketDeleteCWPClient(ID);
	}

	public TESPacketDeleteCWPClient getClientDeletePacketShared() {
		return new TESPacketDeleteCWPClient(ID).setSharingPlayer(sharingPlayer);
	}

	public TESPacketCreateCWPClient getClientPacket() {
		return new TESPacketCreateCWPClient(mapX, mapY, xCoord, yCoord, zCoord, ID, customName, sharedFellowshipIDs);
	}

	public TESPacketCreateCWPClient getClientPacketShared() {
		return new TESPacketCreateCWPClient(mapX, mapY, xCoord, yCoord, zCoord, ID, customName, sharedFellowshipIDs).setSharingPlayer(sharingPlayer, sharingPlayerName, sharedUnlocked, sharedHidden);
	}

	public TESPacketShareCWPClient getClientRemoveFellowshipPacket(UUID fsID) {
		return new TESPacketShareCWPClient(ID, fsID, false);
	}

	public TESPacketRenameCWPClient getClientRenamePacket() {
		return new TESPacketRenameCWPClient(ID, customName);
	}

	public TESPacketRenameCWPClient getClientRenamePacketShared() {
		return new TESPacketRenameCWPClient(ID, customName).setSharingPlayer(sharingPlayer);
	}

	public TESPacketCWPSharedHideClient getClientSharedHidePacket(boolean hide) {
		return new TESPacketCWPSharedHideClient(ID, sharingPlayer, hide);
	}

	public TESPacketCWPSharedUnlockClient getClientSharedUnlockPacket() {
		return new TESPacketCWPSharedUnlockClient(ID, sharingPlayer);
	}

	@Override
	public String getCodeName() {
		return customName;
	}

	@Override
	public String getDisplayName() {
		if (isShared()) {
			return StatCollector.translateToLocalFormatted("tes.waypoint.shared", customName);
		}
		return StatCollector.translateToLocalFormatted("tes.wp.custom", customName);
	}

	@Override
	public int getID() {
		return ID;
	}

	@Override
	public TESWaypoint getInstance() {
		return null;
	}

	@Override
	public TESAbstractWaypoint.WaypointLockState getLockState(EntityPlayer entityplayer) {
		boolean unlocked = hasPlayerUnlocked(entityplayer);
		if (isShared()) {
			return unlocked ? TESAbstractWaypoint.WaypointLockState.SHARED_CUSTOM_UNLOCKED : TESAbstractWaypoint.WaypointLockState.SHARED_CUSTOM_LOCKED;
		}
		return unlocked ? TESAbstractWaypoint.WaypointLockState.CUSTOM_UNLOCKED : TESAbstractWaypoint.WaypointLockState.CUSTOM_LOCKED;
	}

	@Override
	public String getLoreText(EntityPlayer entityplayer) {
		boolean ownShared = !isShared() && !sharedFellowshipIDs.isEmpty();
		boolean shared = isShared() && sharingPlayerName != null;
		if (ownShared || shared) {
			int numShared = sharedFellowshipIDs.size();
			int numShown = 0;
			Collection<String> fsNames = new ArrayList<>();
			for (int i = 0; i < 3 && i < sharedFellowshipIDs.size(); ++i) {
				UUID fsID = sharedFellowshipIDs.get(i);
				TESFellowshipClient fs = TESLevelData.getData(entityplayer).getClientFellowshipByID(fsID);
				if (fs == null) {
					continue;
				}
				fsNames.add(fs.getName());
				++numShown;
			}
			StringBuilder sharedFsNames = new StringBuilder();
			for (String s : fsNames) {
				sharedFsNames.append('\n').append(s);
			}
			if (numShared > numShown) {
				int numMore = numShared - numShown;
				sharedFsNames.append('\n').append(StatCollector.translateToLocalFormatted("tes.wp.custom.andMore", numMore));
			}
			if (ownShared) {
				return StatCollector.translateToLocalFormatted("tes.wp.custom.info", sharedFsNames.toString());
			}
			return StatCollector.translateToLocalFormatted("tes.waypoint.shared.info", sharingPlayerName, sharedFsNames.toString());
		}
		return null;
	}

	public List<UUID> getPlayersInAllSharedFellowships() {
		List<UUID> allPlayers = new ArrayList<>();
		for (UUID fsID : sharedFellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null) {
				List<UUID> fsPlayers = fs.getAllPlayerUUIDs();
				for (UUID player : fsPlayers) {
					if (player.equals(sharingPlayer) || allPlayers.contains(player)) {
						continue;
					}
					allPlayers.add(player);
				}
			}
		}
		return allPlayers;
	}

	@Override
	public int getRotation() {
		return 0;
	}

	public List<UUID> getSharedFellowshipIDs() {
		return sharedFellowshipIDs;
	}

	public void setSharedFellowshipIDs(List<UUID> fsIDs) {
		sharedFellowshipIDs = fsIDs;
	}

	public UUID getSharingPlayerID() {
		return sharingPlayer;
	}

	public void setSharingPlayerID(UUID id) {
		UUID prev = sharingPlayer;
		sharingPlayer = id;
		if (MinecraftServer.getServer() != null && (prev == null || !prev.equals(sharingPlayer))) {
			sharingPlayerName = TESPacketFellowship.getPlayerProfileWithUsername(sharingPlayer).getName();
		}
	}

	public String getSharingPlayerName() {
		return sharingPlayerName;
	}

	public void setSharingPlayerName(String s) {
		sharingPlayerName = s;
	}

	@Override
	public double getShiftX() {
		return 0;
	}

	@Override
	public double getShiftY() {
		return 0;
	}

	@Override
	public double getImgX() {
		return mapX;
	}

	@Override
	public int getCoordX() {
		return xCoord;
	}

	@Override
	public double getImgY() {
		return mapY;
	}

	@Override
	public int getCoordY(World world, int i, int k) {
		int j = yCoord;
		if (j < 0) {
			yCoord = TES.getTrueTopBlock(world, i, k);
		} else if (!isSafeBlock(world, i, j, k)) {
			int j1;
			Block below = world.getBlock(i, j - 1, k);
			Block block = world.getBlock(i, j, k);
			Block above = world.getBlock(i, j + 1, k);
			boolean belowSafe = below.getMaterial().blocksMovement();
			boolean blockSafe = !block.isNormalCube(world, i, j, k);
			boolean aboveSafe = !above.isNormalCube(world, i, j + 1, k);
			boolean foundSafe = false;
			if (!belowSafe) {
				for (j1 = j - 1; j1 >= 1; --j1) {
					if (!isSafeBlock(world, i, j1, k)) {
						continue;
					}
					yCoord = j1;
					foundSafe = true;
					break;
				}
			}
			if (!foundSafe && (!blockSafe || !aboveSafe)) {
				for (j1 = j + (aboveSafe ? 1 : 2); j1 <= world.getHeight() - 1; ++j1) {
					if (!isSafeBlock(world, i, j1, k)) {
						continue;
					}
					yCoord = j1;
					foundSafe = true;
					break;
				}
			}
			if (!foundSafe) {
				yCoord = TES.getTrueTopBlock(world, i, k);
			}
		}
		return yCoord;
	}

	@Override
	public int getCoordYSaved() {
		return yCoord;
	}

	@Override
	public int getCoordZ() {
		return zCoord;
	}

	@Override
	public boolean hasPlayerUnlocked(EntityPlayer entityplayer) {
		return !isShared() || sharedUnlocked;
	}

	public boolean hasSharedFellowship(TESFellowship fs) {
		return hasSharedFellowship(fs.getFellowshipID());
	}

	public boolean hasSharedFellowship(UUID fsID) {
		return sharedFellowshipIDs.contains(fsID);
	}

	@Override
	public boolean isHidden() {
		return false;
	}

	public boolean isShared() {
		return sharingPlayer != null;
	}

	public boolean isSharedHidden() {
		return sharedHidden;
	}

	public void setSharedHidden(boolean flag) {
		sharedHidden = flag;
	}

	public boolean isSharedUnlocked() {
		return sharedUnlocked;
	}

	public void removeSharedFellowship(UUID fsID) {
		sharedFellowshipIDs.remove(fsID);
	}

	public void rename(String newName) {
		customName = newName;
	}

	public void setSharedUnlocked() {
		sharedUnlocked = true;
	}

	private void validateFellowshipIDs(TESPlayerData ownerData) {
		UUID ownerUUID = ownerData.getPlayerUUID();
		Collection<UUID> removeIDs = new HashSet<>();
		for (UUID fsID : sharedFellowshipIDs) {
			TESFellowship fs = TESFellowshipData.getActiveFellowship(fsID);
			if (fs != null && fs.containsPlayer(ownerUUID)) {
				continue;
			}
			removeIDs.add(fsID);
		}
		sharedFellowshipIDs.removeAll(removeIDs);
	}

	public void writeToNBT(NBTTagCompound nbt, TESPlayerData pd) {
		nbt.setString("Name", customName);
		nbt.setInteger("X", mapX);
		nbt.setInteger("Y", mapY);
		nbt.setInteger("XCoord", xCoord);
		nbt.setInteger("YCoord", yCoord);
		nbt.setInteger("ZCoord", zCoord);
		nbt.setInteger("ID", ID);
		validateFellowshipIDs(pd);
		if (!sharedFellowshipIDs.isEmpty()) {
			NBTTagList sharedFellowshipTags = new NBTTagList();
			for (UUID fsID : sharedFellowshipIDs) {
				NBTTagString tag = new NBTTagString(fsID.toString());
				sharedFellowshipTags.appendTag(tag);
			}
			nbt.setTag("SharedFellowships", sharedFellowshipTags);
		}
	}
}