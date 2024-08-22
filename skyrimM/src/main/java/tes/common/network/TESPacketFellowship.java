package tes.common.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESTitle;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.UsernameCache;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;

public class TESPacketFellowship implements IMessage {
	private final List<GameProfile> members = new ArrayList<>();
	private final Map<UUID, TESTitle.PlayerTitle> titleMap = new HashMap<>();
	private final Set<UUID> adminUuids = new HashSet<>();

	private UUID fellowshipID;
	private String fellowshipName;
	private ItemStack fellowshipIcon;
	private GameProfile owner;

	private boolean isInvite;
	private boolean isOwned;
	private boolean isAdminned;
	private boolean preventPVP;
	private boolean preventHiredFF;
	private boolean showMapLocations;

	@SuppressWarnings("unused")
	public TESPacketFellowship() {
	}

	public TESPacketFellowship(TESPlayerData playerData, TESFellowship fs, boolean invite) {
		fellowshipID = fs.getFellowshipID();
		isInvite = invite;
		fellowshipName = fs.getName();
		fellowshipIcon = fs.getIcon();
		UUID thisPlayer = playerData.getPlayerUUID();
		isOwned = fs.isOwner(thisPlayer);
		isAdminned = fs.isAdmin(thisPlayer);
		List<UUID> playerIDs = fs.getAllPlayerUUIDs();
		for (UUID player : playerIDs) {
			GameProfile profile = getPlayerProfileWithUsername(player);
			if (fs.isOwner(player)) {
				owner = profile;
			} else {
				members.add(profile);
			}
			TESTitle.PlayerTitle title = TESLevelData.getPlayerTitleWithOfflineCache(player);
			if (title != null) {
				titleMap.put(player, title);
			}
			if (!fs.isAdmin(player)) {
				continue;
			}
			adminUuids.add(player);
		}
		preventPVP = fs.getPreventPVP();
		preventHiredFF = fs.getPreventHiredFriendlyFire();
		showMapLocations = fs.getShowMapLocations();
	}

	public static GameProfile getPlayerProfileWithUsername(UUID player) {
		GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152652_a(player);
		if (profile == null || StringUtils.isBlank(profile.getName())) {
			String name = UsernameCache.getLastKnownUsername(player);
			if (name != null) {
				return new GameProfile(player, name);
			}
			profile = new GameProfile(player, "");
			MinecraftServer.getServer().func_147130_as().fillProfileProperties(profile, true);
		}
		return profile;
	}

	public static GameProfile readPlayerUuidAndUsername(ByteBuf data) {
		UUID uuid = new UUID(data.readLong(), data.readLong());
		byte nameLength = data.readByte();
		if (nameLength >= 0) {
			ByteBuf nameBytes = data.readBytes(nameLength);
			String username = nameBytes.toString(Charsets.UTF_8);
			return new GameProfile(uuid, username);
		}
		return null;
	}

	public static void writePlayerUuidAndUsername(ByteBuf data, GameProfile profile) {
		UUID uuid = profile.getId();
		String username = profile.getName();
		data.writeLong(uuid.getMostSignificantBits());
		data.writeLong(uuid.getLeastSignificantBits());
		byte[] usernameBytes = username.getBytes(Charsets.UTF_8);
		data.writeByte(usernameBytes.length);
		data.writeBytes(usernameBytes);
	}

	@Override
	public void fromBytes(ByteBuf data) {
		fellowshipID = new UUID(data.readLong(), data.readLong());
		isInvite = data.readBoolean();
		byte fsNameLength = data.readByte();
		ByteBuf fsNameBytes = data.readBytes(fsNameLength);
		fellowshipName = fsNameBytes.toString(Charsets.UTF_8);
		NBTTagCompound iconData = new NBTTagCompound();
		try {
			iconData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Hummel009: Error reading fellowship icon data");
			e.printStackTrace();
		}
		fellowshipIcon = ItemStack.loadItemStackFromNBT(iconData);
		isOwned = data.readBoolean();
		isAdminned = data.readBoolean();
		owner = readPlayerUuidAndUsername(data);
		readTitleForPlayer(data, owner.getId());
		int numMembers = data.readInt();
		for (int i = 0; i < numMembers; ++i) {
			GameProfile member = readPlayerUuidAndUsername(data);
			if (member == null) {
				continue;
			}
			members.add(member);
			UUID memberUuid = member.getId();
			readTitleForPlayer(data, memberUuid);
			boolean admin = data.readBoolean();
			if (!admin) {
				continue;
			}
			adminUuids.add(memberUuid);
		}
		preventPVP = data.readBoolean();
		preventHiredFF = data.readBoolean();
		showMapLocations = data.readBoolean();
	}

	private void readTitleForPlayer(ByteBuf data, UUID playerUuid) {
		TESTitle.PlayerTitle playerTitle = TESTitle.PlayerTitle.readNullableTitle(data);
		if (playerTitle != null) {
			titleMap.put(playerUuid, playerTitle);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(fellowshipID.getMostSignificantBits());
		data.writeLong(fellowshipID.getLeastSignificantBits());
		data.writeBoolean(isInvite);
		byte[] fsNameBytes = fellowshipName.getBytes(Charsets.UTF_8);
		data.writeByte(fsNameBytes.length);
		data.writeBytes(fsNameBytes);
		NBTTagCompound iconData = new NBTTagCompound();
		if (fellowshipIcon != null) {
			fellowshipIcon.writeToNBT(iconData);
		}
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(iconData);
		} catch (IOException e) {
			FMLLog.severe("Hummel009: Error writing fellowship icon data");
			e.printStackTrace();
		}
		data.writeBoolean(isOwned);
		data.writeBoolean(isAdminned);
		writePlayerUuidAndUsername(data, owner);
		TESTitle.PlayerTitle.writeNullableTitle(data, titleMap.get(owner.getId()));
		data.writeInt(members.size());
		for (GameProfile member : members) {
			UUID memberUuid = member.getId();
			TESTitle.PlayerTitle title = titleMap.get(memberUuid);
			boolean admin = adminUuids.contains(memberUuid);
			writePlayerUuidAndUsername(data, member);
			TESTitle.PlayerTitle.writeNullableTitle(data, title);
			data.writeBoolean(admin);
		}
		data.writeBoolean(preventPVP);
		data.writeBoolean(preventHiredFF);
		data.writeBoolean(showMapLocations);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowship, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowship packet, MessageContext context) {
			TESFellowshipClient fellowship = new TESFellowshipClient(packet.fellowshipID, packet.fellowshipName, packet.isOwned, packet.isAdminned, packet.owner, packet.members);
			fellowship.setTitles(packet.titleMap);
			fellowship.setAdmins(packet.adminUuids);
			fellowship.setIcon(packet.fellowshipIcon);
			fellowship.setPreventPVP(packet.preventPVP);
			fellowship.setPreventHiredFriendlyFire(packet.preventHiredFF);
			fellowship.setShowMapLocations(packet.showMapLocations);
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			if (packet.isInvite) {
				TESLevelData.getData(entityplayer).addOrUpdateClientFellowshipInvite(fellowship);
			} else {
				TESLevelData.getData(entityplayer).addOrUpdateClientFellowship(fellowship);
			}
			return null;
		}
	}
}