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
import tes.common.util.TESLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.UUID;

public abstract class TESPacketFellowshipPartialUpdate implements IMessage {
	private UUID fellowshipID;

	protected TESPacketFellowshipPartialUpdate() {
	}

	protected TESPacketFellowshipPartialUpdate(TESFellowship fs) {
		fellowshipID = fs.getFellowshipID();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		fellowshipID = new UUID(data.readLong(), data.readLong());
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(fellowshipID.getMostSignificantBits());
		data.writeLong(fellowshipID.getLeastSignificantBits());
	}

	protected abstract void updateClient(TESFellowshipClient var1);

	protected UUID getFellowshipID() {
		return fellowshipID;
	}

	public static class AddMember extends OnePlayerUpdate {
		private TESTitle.PlayerTitle playerTitle;

		@SuppressWarnings("unused")
		public AddMember() {
		}

		public AddMember(TESFellowship fs, UUID member) {
			super(fs, member);
			playerTitle = TESLevelData.getData(member).getPlayerTitle();
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			playerTitle = TESTitle.PlayerTitle.readNullableTitle(data);
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			TESTitle.PlayerTitle.writeNullableTitle(data, playerTitle);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.addMember(playerProfile, playerTitle);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<AddMember> {
		}
	}

	public static class ChangeIcon extends TESPacketFellowshipPartialUpdate {
		private ItemStack fellowshipIcon;

		@SuppressWarnings("unused")
		public ChangeIcon() {
		}

		public ChangeIcon(TESFellowship fs) {
			super(fs);
			fellowshipIcon = fs.getIcon();
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			NBTTagCompound iconData = new NBTTagCompound();
			try {
				iconData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
			} catch (IOException e) {
				FMLLog.severe("Hummel009: Error reading fellowship icon data");
				e.printStackTrace();
			}
			fellowshipIcon = ItemStack.loadItemStackFromNBT(iconData);
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
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
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setIcon(fellowshipIcon);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<ChangeIcon> {
		}
	}

	private abstract static class Handler<P extends TESPacketFellowshipPartialUpdate> implements IMessageHandler<P, IMessage> {
		@Override
		public IMessage onMessage(P packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESFellowshipClient fellowship = pd.getClientFellowshipByID(packet.getFellowshipID());
			if (fellowship != null) {
				packet.updateClient(fellowship);
			} else {
				TESLog.getLogger().warn("Client couldn't find fellowship for ID {}", packet.getFellowshipID());
			}
			return null;
		}
	}

	private abstract static class OnePlayerUpdate extends TESPacketFellowshipPartialUpdate {
		protected GameProfile playerProfile;

		protected OnePlayerUpdate() {
		}

		protected OnePlayerUpdate(TESFellowship fs, UUID player) {
			super(fs);
			playerProfile = TESPacketFellowship.getPlayerProfileWithUsername(player);
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			playerProfile = TESPacketFellowship.readPlayerUuidAndUsername(data);
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			TESPacketFellowship.writePlayerUuidAndUsername(data, playerProfile);
		}
	}

	public static class RemoveAdmin extends OnePlayerUpdate {
		private boolean isAdminned;

		@SuppressWarnings("unused")
		public RemoveAdmin() {
		}

		public RemoveAdmin(TESFellowship fs, UUID admin, boolean adminned) {
			super(fs, admin);
			isAdminned = adminned;
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			isAdminned = data.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			data.writeBoolean(isAdminned);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.removeAdmin(playerProfile.getId(), isAdminned);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<RemoveAdmin> {
		}
	}

	public static class RemoveMember extends OnePlayerUpdate {
		@SuppressWarnings("unused")
		public RemoveMember() {
		}

		public RemoveMember(TESFellowship fs, UUID member) {
			super(fs, member);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.removeMember(playerProfile);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<RemoveMember> {
		}
	}

	public static class Rename extends TESPacketFellowshipPartialUpdate {
		private String fellowshipName;

		public Rename() {
		}

		public Rename(TESFellowship fs) {
			super(fs);
			fellowshipName = fs.getName();
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			byte fsNameLength = data.readByte();
			ByteBuf fsNameBytes = data.readBytes(fsNameLength);
			fellowshipName = fsNameBytes.toString(Charsets.UTF_8);
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			byte[] fsNameBytes = fellowshipName.getBytes(Charsets.UTF_8);
			data.writeByte(fsNameBytes.length);
			data.writeBytes(fsNameBytes);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setName(fellowshipName);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<Rename> {
		}
	}

	public static class SetAdmin extends OnePlayerUpdate {
		private boolean isAdminned;

		@SuppressWarnings("unused")
		public SetAdmin() {
		}

		public SetAdmin(TESFellowship fs, UUID admin, boolean adminned) {
			super(fs, admin);
			isAdminned = adminned;
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			isAdminned = data.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			data.writeBoolean(isAdminned);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setAdmin(playerProfile.getId(), isAdminned);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<SetAdmin> {
		}
	}

	public static class SetOwner extends OnePlayerUpdate {
		private boolean isOwned;

		@SuppressWarnings("unused")
		public SetOwner() {
		}

		public SetOwner(TESFellowship fs, UUID owner, boolean owned) {
			super(fs, owner);
			isOwned = owned;
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			isOwned = data.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			data.writeBoolean(isOwned);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setOwner(playerProfile, isOwned);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<SetOwner> {
		}
	}

	public static class ToggleHiredFriendlyFire extends TESPacketFellowshipPartialUpdate {
		private boolean preventHiredFF;

		@SuppressWarnings("unused")
		public ToggleHiredFriendlyFire() {
		}

		public ToggleHiredFriendlyFire(TESFellowship fs) {
			super(fs);
			preventHiredFF = fs.getPreventHiredFriendlyFire();
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			preventHiredFF = data.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			data.writeBoolean(preventHiredFF);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setPreventHiredFriendlyFire(preventHiredFF);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<ToggleHiredFriendlyFire> {
		}
	}

	public static class TogglePvp extends TESPacketFellowshipPartialUpdate {
		private boolean preventPVP;

		@SuppressWarnings("unused")
		public TogglePvp() {
		}

		public TogglePvp(TESFellowship fs) {
			super(fs);
			preventPVP = fs.getPreventPVP();
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			preventPVP = data.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			data.writeBoolean(preventPVP);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setPreventPVP(preventPVP);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<TogglePvp> {
		}
	}

	public static class ToggleShowMap extends TESPacketFellowshipPartialUpdate {
		private boolean showMapLocations;

		@SuppressWarnings("unused")
		public ToggleShowMap() {
		}

		public ToggleShowMap(TESFellowship fs) {
			super(fs);
			showMapLocations = fs.getShowMapLocations();
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			showMapLocations = data.readBoolean();
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			data.writeBoolean(showMapLocations);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.setShowMapLocations(showMapLocations);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<ToggleShowMap> {
		}
	}

	public static class UpdatePlayerTitle extends OnePlayerUpdate {
		private TESTitle.PlayerTitle playerTitle;

		@SuppressWarnings("unused")
		public UpdatePlayerTitle() {
		}

		public UpdatePlayerTitle(TESFellowship fs, UUID player, TESTitle.PlayerTitle title) {
			super(fs, player);
			playerTitle = title;
		}

		@Override
		public void fromBytes(ByteBuf data) {
			super.fromBytes(data);
			playerTitle = TESTitle.PlayerTitle.readNullableTitle(data);
		}

		@Override
		public void toBytes(ByteBuf data) {
			super.toBytes(data);
			TESTitle.PlayerTitle.writeNullableTitle(data, playerTitle);
		}

		@Override
		public void updateClient(TESFellowshipClient fellowship) {
			fellowship.updatePlayerTitle(playerProfile.getId(), playerTitle);
		}

		public static class Handler extends TESPacketFellowshipPartialUpdate.Handler<UpdatePlayerTitle> {
		}
	}
}