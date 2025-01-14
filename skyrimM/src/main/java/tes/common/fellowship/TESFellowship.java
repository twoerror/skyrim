package tes.common.fellowship;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.TESLevelData;
import tes.common.network.TESPacketFellowshipNotification;
import tes.common.network.TESPacketHandler;
import tes.common.util.TESLog;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class TESFellowship {
	private final List<UUID> memberUUIDs = new ArrayList<>();
	private final Collection<UUID> adminUUIDs = new HashSet<>();
	private final Set<UUID> waypointSharerUUIDs = new HashSet<>();

	private UUID ownerUUID;
	private UUID fellowshipUUID;
	private String fellowshipName;
	private ItemStack fellowshipIcon;

	private boolean needsSave;
	private boolean disbanded;
	private boolean preventPVP = true;
	private boolean preventHiredFF = true;
	private boolean showMapLocations = true;
	private boolean doneRetroactiveWaypointSharerCheck = true;

	public TESFellowship(UUID fsID) {
		fellowshipUUID = fsID;
	}

	public TESFellowship(UUID owner, String name) {
		this();
		ownerUUID = owner;
		fellowshipName = name;
	}

	private TESFellowship() {
		fellowshipUUID = UUID.randomUUID();
	}

	public static void sendNotification(ICommandSender entityplayer, String key, Object... args) {
		ChatComponentTranslation message = new ChatComponentTranslation(key, args);
		message.getChatStyle().setColor(EnumChatFormatting.YELLOW);
		entityplayer.addChatMessage(message);
		IMessage packet = new TESPacketFellowshipNotification(message);
		TESPacketHandler.NETWORK_WRAPPER.sendTo(packet, (EntityPlayerMP) entityplayer);
	}

	public void addMember(UUID player) {
		if (!isOwner(player) && !memberUUIDs.contains(player)) {
			memberUUIDs.add(player);
			TESLevelData.getData(player).addFellowship(this);
			updateForAllMembers(new FellowshipUpdateType.AddMember(player));
			markDirty();
		}
	}

	public boolean containsPlayer(UUID player) {
		return isOwner(player) || hasMember(player);
	}

	public void createAndRegister() {
		TESFellowshipData.addFellowship(this);
		TESLevelData.getData(ownerUUID).addFellowship(this);
		updateForAllMembers(new FellowshipUpdateType.Full());
		markDirty();
	}

	public void doRetroactiveWaypointSharerCheckIfNeeded() {
		if (!doneRetroactiveWaypointSharerCheck) {
			waypointSharerUUIDs.clear();
			if (!disbanded) {
				List<UUID> allPlayersSafe = getAllPlayerUUIDs();
				for (UUID player : allPlayersSafe) {
					if (!TESLevelData.getData(player).hasAnyWaypointsSharedToFellowship(this)) {
						continue;
					}
					waypointSharerUUIDs.add(player);
				}
				TESLog.getLogger().info("Fellowship {} did retroactive waypoint sharer check and found {} out of {} players", fellowshipName, waypointSharerUUIDs.size(), allPlayersSafe.size());
			}
			doneRetroactiveWaypointSharerCheck = true;
			markDirty();
		}
	}

	public List<UUID> getAllPlayerUUIDs() {
		List<UUID> list = new ArrayList<>();
		list.add(ownerUUID);
		list.addAll(memberUUIDs);
		return list;
	}

	public UUID getFellowshipID() {
		return fellowshipUUID;
	}

	public ItemStack getIcon() {
		return fellowshipIcon;
	}

	public void setIcon(ItemStack itemstack) {
		fellowshipIcon = itemstack;
		updateForAllMembers(new FellowshipUpdateType.ChangeIcon());
		markDirty();
	}

	public List<UUID> getMemberUUIDs() {
		return memberUUIDs;
	}

	public String getName() {
		return fellowshipName;
	}

	public void setName(String name) {
		fellowshipName = name;
		updateForAllMembers(new FellowshipUpdateType.Rename());
		markDirty();
	}

	public UUID getOwner() {
		return ownerUUID;
	}

	public void setOwner(UUID owner) {
		UUID prevOwner = ownerUUID;
		if (prevOwner != null && !memberUUIDs.contains(prevOwner)) {
			memberUUIDs.add(0, prevOwner);
		}
		ownerUUID = owner;
		memberUUIDs.remove(owner);
		adminUUIDs.remove(owner);
		TESLevelData.getData(ownerUUID).addFellowship(this);
		updateForAllMembers(new FellowshipUpdateType.SetOwner(ownerUUID));
		markDirty();
	}

	public int getPlayerCount() {
		return memberUUIDs.size() + 1;
	}

	public boolean getPreventHiredFriendlyFire() {
		return preventHiredFF;
	}

	public void setPreventHiredFriendlyFire(boolean flag) {
		preventHiredFF = flag;
		updateForAllMembers(new FellowshipUpdateType.ToggleHiredFriendlyFire());
		markDirty();
	}

	public boolean getPreventPVP() {
		return preventPVP;
	}

	public void setPreventPVP(boolean flag) {
		preventPVP = flag;
		updateForAllMembers(new FellowshipUpdateType.TogglePvp());
		markDirty();
	}

	public boolean getShowMapLocations() {
		return showMapLocations;
	}

	public void setShowMapLocations(boolean flag) {
		showMapLocations = flag;
		updateForAllMembers(new FellowshipUpdateType.ToggleShowMapLocations());
		markDirty();
	}

	public Set<UUID> getWaypointSharerUUIDs() {
		return waypointSharerUUIDs;
	}

	public boolean hasMember(UUID player) {
		return memberUUIDs.contains(player);
	}

	public boolean isAdmin(UUID player) {
		return hasMember(player) && adminUUIDs.contains(player);
	}

	public boolean isDisbanded() {
		return disbanded;
	}

	public boolean isOwner(UUID player) {
		return ownerUUID.equals(player);
	}

	public void load(NBTTagCompound fsData) {
		disbanded = fsData.getBoolean("Disbanded");
		if (fsData.hasKey("Owner")) {
			ownerUUID = UUID.fromString(fsData.getString("Owner"));
		}
		memberUUIDs.clear();
		adminUUIDs.clear();
		NBTTagList memberTags = fsData.getTagList("Members", 10);
		for (int i = 0; i < memberTags.tagCount(); ++i) {
			NBTTagCompound nbt = memberTags.getCompoundTagAt(i);
			UUID member = UUID.fromString(nbt.getString("Member"));
			memberUUIDs.add(member);
			if (!nbt.hasKey("Admin") || !nbt.getBoolean("Admin")) {
				continue;
			}
			adminUUIDs.add(member);
		}
		waypointSharerUUIDs.clear();
		NBTTagList waypointSharerTags = fsData.getTagList("WaypointSharers", 8);
		for (int i = 0; i < waypointSharerTags.tagCount(); ++i) {
			UUID waypointSharer = UUID.fromString(waypointSharerTags.getStringTagAt(i));
			if (containsPlayer(waypointSharer)) {
				waypointSharerUUIDs.add(waypointSharer);
			}
		}
		if (fsData.hasKey("Name")) {
			fellowshipName = fsData.getString("Name");
		}
		if (fsData.hasKey("Icon")) {
			NBTTagCompound itemData = fsData.getCompoundTag("Icon");
			fellowshipIcon = ItemStack.loadItemStackFromNBT(itemData);
		}
		if (fsData.hasKey("PreventPVP")) {
			preventPVP = fsData.getBoolean("PreventPVP");
		}
		if (fsData.hasKey("PreventPVP")) {
			preventHiredFF = fsData.getBoolean("PreventHiredFF");
		}
		if (fsData.hasKey("ShowMap")) {
			showMapLocations = fsData.getBoolean("ShowMap");
		}
		validate();
		doneRetroactiveWaypointSharerCheck = fsData.getBoolean("DoneRetroactiveWaypointSharerCheck");
	}

	private void markDirty() {
		needsSave = true;
	}

	public void markIsWaypointSharer(UUID player, boolean flag) {
		if (containsPlayer(player)) {
			if (flag && !waypointSharerUUIDs.contains(player)) {
				waypointSharerUUIDs.add(player);
				markDirty();
			} else if (!flag && waypointSharerUUIDs.contains(player)) {
				waypointSharerUUIDs.remove(player);
				markDirty();
			}
		}
	}

	public boolean needsSave() {
		return needsSave;
	}

	public void removeMember(UUID player) {
		if (memberUUIDs.contains(player)) {
			memberUUIDs.remove(player);
			adminUUIDs.remove(player);
			waypointSharerUUIDs.remove(player);
			TESLevelData.getData(player).removeFellowship(this);
			updateForAllMembers(new FellowshipUpdateType.RemoveMember(player));
			markDirty();
		}
	}

	public void save(NBTTagCompound fsData) {
		fsData.setBoolean("Disbanded", disbanded);
		if (ownerUUID != null) {
			fsData.setString("Owner", ownerUUID.toString());
		}
		NBTTagList memberTags = new NBTTagList();
		for (UUID member : memberUUIDs) {
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setString("Member", member.toString());
			if (adminUUIDs.contains(member)) {
				nbt.setBoolean("Admin", true);
			}
			memberTags.appendTag(nbt);
		}
		fsData.setTag("Members", memberTags);
		NBTTagList waypointSharerTags = new NBTTagList();
		for (UUID waypointSharer : waypointSharerUUIDs) {
			waypointSharerTags.appendTag(new NBTTagString(waypointSharer.toString()));
		}
		fsData.setTag("WaypointSharers", waypointSharerTags);
		if (fellowshipName != null) {
			fsData.setString("Name", fellowshipName);
		}
		if (fellowshipIcon != null) {
			NBTTagCompound itemData = new NBTTagCompound();
			fellowshipIcon.writeToNBT(itemData);
			fsData.setTag("Icon", itemData);
		}
		fsData.setBoolean("PreventPVP", preventPVP);
		fsData.setBoolean("PreventHiredFF", preventHiredFF);
		fsData.setBoolean("ShowMap", showMapLocations);
		fsData.setBoolean("DoneRetroactiveWaypointSharerCheck", doneRetroactiveWaypointSharerCheck);
		needsSave = false;
	}

	public void sendFellowshipMessage(EntityPlayerMP sender, String message) {
		String message1 = message;
		if (sender.func_147096_v() == EntityPlayer.EnumChatVisibility.HIDDEN) {
			ChatComponentTranslation msgCannotSend = new ChatComponentTranslation("chat.cannotSend");
			msgCannotSend.getChatStyle().setColor(EnumChatFormatting.RED);
			sender.playerNetServerHandler.sendPacket(new S02PacketChat(msgCannotSend));
		} else {
			sender.func_143004_u();
			message1 = StringUtils.normalizeSpace(message1);
			if (StringUtils.isBlank(message1)) {
				return;
			}
			for (int i = 0; i < message1.length(); ++i) {
				if (ChatAllowedCharacters.isAllowedCharacter(message1.charAt(i))) {
					continue;
				}
				sender.playerNetServerHandler.kickPlayerFromServer("Illegal characters in chat");
				return;
			}
			ClickEvent fMsgClickEvent = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/fmsg \"" + fellowshipName + "\" ");
			IChatComponent msgComponent = ForgeHooks.newChatWithLinks(message1);
			msgComponent.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			IChatComponent senderComponent = sender.func_145748_c_();
			senderComponent.getChatStyle().setChatClickEvent(fMsgClickEvent);
			ChatComponentTranslation chatComponent = new ChatComponentTranslation("chat.type.text", senderComponent, "");
			chatComponent = ForgeHooks.onServerChatEvent(sender.playerNetServerHandler, message1, chatComponent);
			if (chatComponent == null) {
				return;
			}
			chatComponent.appendSibling(msgComponent);
			ChatComponentTranslation fsComponent = new ChatComponentTranslation("tes.command.fmsg.fsPrefix", fellowshipName);
			fsComponent.getChatStyle().setColor(EnumChatFormatting.YELLOW);
			fsComponent.getChatStyle().setChatClickEvent(fMsgClickEvent);
			ChatComponentTranslation fullChatComponent = new ChatComponentTranslation("%s %s", fsComponent, chatComponent);
			MinecraftServer server = MinecraftServer.getServer();
			server.addChatMessage(fullChatComponent);
			S02PacketChat packetChat = new S02PacketChat(fullChatComponent, false);
			for (EntityPlayerMP entityplayer : (List<EntityPlayerMP>) server.getConfigurationManager().playerEntityList) {
				if (!containsPlayer(entityplayer.getUniqueID())) {
					continue;
				}
				entityplayer.playerNetServerHandler.sendPacket(packetChat);
			}
		}
	}

	public void setAdmin(UUID player, boolean flag) {
		if (memberUUIDs.contains(player)) {
			if (flag && !adminUUIDs.contains(player)) {
				adminUUIDs.add(player);
				updateForAllMembers(new FellowshipUpdateType.SetAdmin(player));
				markDirty();
			} else if (!flag && adminUUIDs.contains(player)) {
				adminUUIDs.remove(player);
				updateForAllMembers(new FellowshipUpdateType.RemoveAdmin(player));
				markDirty();
			}
		}
	}

	public void setDisbandedAndRemoveAllMembers() {
		disbanded = true;
		markDirty();
		Iterable<UUID> copyMemberIDs = new ArrayList<>(memberUUIDs);
		for (UUID player : copyMemberIDs) {
			removeMember(player);
		}
	}

	public void updateForAllMembers(FellowshipUpdateType updateType) {
		for (UUID player : getAllPlayerUUIDs()) {
			TESLevelData.getData(player).updateFellowship(this, updateType);
		}
	}

	private void validate() {
		if (fellowshipUUID == null) {
			fellowshipUUID = UUID.randomUUID();
		}
		if (ownerUUID == null) {
			ownerUUID = UUID.randomUUID();
		}
	}
}