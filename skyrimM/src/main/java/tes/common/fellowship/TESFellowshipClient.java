package tes.common.fellowship;

import com.mojang.authlib.GameProfile;
import tes.common.database.TESTitle;
import net.minecraft.item.ItemStack;

import java.util.*;

public class TESFellowshipClient {
	private final UUID fellowshipID;

	private List<UUID> memberUUIDs = new ArrayList<>();
	private Map<UUID, String> usernameMap = new HashMap<>();
	private Map<UUID, TESTitle.PlayerTitle> titleMap = new HashMap<>();
	private Set<UUID> adminUUIDs = new HashSet<>();

	private UUID ownerUUID;
	private String fellowshipName;
	private ItemStack fellowshipIcon;

	private boolean isOwned;
	private boolean isAdminned;
	private boolean preventPVP;
	private boolean preventHiredFF;
	private boolean showMapLocations;

	public TESFellowshipClient(UUID id, String name, boolean owned, boolean admin, GameProfile owner, Iterable<GameProfile> members) {
		fellowshipID = id;
		fellowshipName = name;
		isOwned = owned;
		isAdminned = admin;
		ownerUUID = owner.getId();
		usernameMap.put(ownerUUID, owner.getName());
		for (GameProfile member : members) {
			memberUUIDs.add(member.getId());
			usernameMap.put(member.getId(), member.getName());
		}
	}

	public void addMember(GameProfile member, TESTitle.PlayerTitle title) {
		UUID memberUuid = member.getId();
		if (!memberUUIDs.contains(memberUuid)) {
			memberUUIDs.add(memberUuid);
			usernameMap.put(memberUuid, member.getName());
			titleMap.put(memberUuid, title);
		}
	}

	public boolean containsPlayer(UUID playerUuid) {
		return ownerUUID.equals(playerUuid) || memberUUIDs.contains(playerUuid);
	}

	public boolean containsPlayerUsername(String username) {
		return usernameMap.containsValue(username);
	}

	public List<GameProfile> getAllPlayerProfiles() {
		return getProfilesFor(getAllPlayerUuids());
	}

	public List<UUID> getAllPlayerUuids() {
		List<UUID> allPlayers = new ArrayList<>();
		allPlayers.add(ownerUUID);
		allPlayers.addAll(memberUUIDs);
		return allPlayers;
	}

	public UUID getFellowshipID() {
		return fellowshipID;
	}

	public ItemStack getIcon() {
		return fellowshipIcon;
	}

	public void setIcon(ItemStack itemstack) {
		fellowshipIcon = itemstack;
	}

	public List<GameProfile> getMemberProfiles() {
		return getProfilesFor(memberUUIDs);
	}

	public List<UUID> getMemberUuids() {
		return memberUUIDs;
	}

	public String getName() {
		return fellowshipName;
	}

	public void setName(String name) {
		fellowshipName = name;
	}

	public GameProfile getOwnerProfile() {
		return getProfileFor(ownerUUID);
	}

	public UUID getOwnerUuid() {
		return ownerUUID;
	}

	public int getPlayerCount() {
		return memberUUIDs.size() + 1;
	}

	public boolean getPreventHiredFriendlyFire() {
		return preventHiredFF;
	}

	public void setPreventHiredFriendlyFire(boolean flag) {
		preventHiredFF = flag;
	}

	public boolean getPreventPVP() {
		return preventPVP;
	}

	public void setPreventPVP(boolean flag) {
		preventPVP = flag;
	}

	private GameProfile getProfileFor(UUID playerUuid) {
		return new GameProfile(playerUuid, getUsernameFor(playerUuid));
	}

	private List<GameProfile> getProfilesFor(Iterable<UUID> playerUuids) {
		List<GameProfile> list = new ArrayList<>();
		for (UUID playerUuid : playerUuids) {
			list.add(getProfileFor(playerUuid));
		}
		return list;
	}

	public boolean getShowMapLocations() {
		return showMapLocations;
	}

	public void setShowMapLocations(boolean flag) {
		showMapLocations = flag;
	}

	public TESTitle.PlayerTitle getTitleFor(UUID playerUuid) {
		return titleMap.get(playerUuid);
	}

	public String getUsernameFor(UUID playerUuid) {
		return usernameMap.get(playerUuid);
	}

	public boolean isAdmin(UUID playerUuid) {
		return adminUUIDs.contains(playerUuid);
	}

	public boolean isAdminned() {
		return isAdminned;
	}

	public boolean isOwned() {
		return isOwned;
	}

	public void removeAdmin(UUID playerUuid, boolean adminned) {
		if (adminUUIDs.contains(playerUuid)) {
			adminUUIDs.remove(playerUuid);
			isAdminned = adminned;
		}
	}

	public void removeMember(GameProfile member) {
		UUID memberUuid = member.getId();
		if (memberUUIDs.contains(memberUuid)) {
			memberUUIDs.remove(memberUuid);
			usernameMap.remove(memberUuid);
			adminUUIDs.remove(memberUuid);
			titleMap.remove(memberUuid);
		}
	}

	public void setAdmin(UUID playerUuid, boolean adminned) {
		if (!adminUUIDs.contains(playerUuid)) {
			adminUUIDs.add(playerUuid);
			isAdminned = adminned;
		}
	}

	public void setAdmins(Set<UUID> admins) {
		adminUUIDs = admins;
	}

	public void setOwner(GameProfile newOwner, boolean owned) {
		UUID prevOwnerUuid = ownerUUID;
		UUID newOwnerUuid = newOwner.getId();
		if (!prevOwnerUuid.equals(newOwnerUuid)) {
			if (!memberUUIDs.contains(prevOwnerUuid)) {
				memberUUIDs.add(0, prevOwnerUuid);
			}
			ownerUUID = newOwnerUuid;
			usernameMap.put(ownerUUID, newOwner.getName());
			memberUUIDs.remove(newOwnerUuid);
			adminUUIDs.remove(newOwnerUuid);
			isOwned = owned;
			if (isOwned) {
				isAdminned = false;
			}
		}
	}

	public void setTitles(Map<UUID, TESTitle.PlayerTitle> titles) {
		titleMap = titles;
	}

	public void updateDataFrom(TESFellowshipClient other) {
		fellowshipName = other.fellowshipName;
		fellowshipIcon = other.fellowshipIcon;
		isOwned = other.isOwned;
		isAdminned = other.isAdminned;
		ownerUUID = other.ownerUUID;
		memberUUIDs = other.memberUUIDs;
		usernameMap = other.usernameMap;
		titleMap = other.titleMap;
		adminUUIDs = other.adminUUIDs;
		preventPVP = other.preventPVP;
		preventHiredFF = other.preventHiredFF;
		showMapLocations = other.showMapLocations;
	}

	public void updatePlayerTitle(UUID playerUuid, TESTitle.PlayerTitle title) {
		if (title == null) {
			titleMap.remove(playerUuid);
		} else {
			titleMap.put(playerUuid, title);
		}
	}
}