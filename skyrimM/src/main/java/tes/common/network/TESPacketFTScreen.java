package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESCustomWaypoint;
import tes.common.world.map.TESWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TESPacketFTScreen implements IMessage {
	private boolean isCustom;
	private int wpID;
	private UUID sharingPlayer;
	private int startX;
	private int startZ;

	@SuppressWarnings("unused")
	public TESPacketFTScreen() {
	}

	public TESPacketFTScreen(TESAbstractWaypoint wp, int x, int z) {
		isCustom = wp instanceof TESCustomWaypoint;
		wpID = wp.getID();
		if (wp instanceof TESCustomWaypoint) {
			sharingPlayer = ((TESCustomWaypoint) wp).getSharingPlayerID();
		}
		startX = x;
		startZ = z;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		isCustom = data.readBoolean();
		wpID = data.readInt();
		boolean shared = data.readBoolean();
		if (shared) {
			sharingPlayer = new UUID(data.readLong(), data.readLong());
		}
		startX = data.readInt();
		startZ = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeBoolean(isCustom);
		data.writeInt(wpID);
		boolean shared = sharingPlayer != null;
		data.writeBoolean(shared);
		if (shared) {
			data.writeLong(sharingPlayer.getMostSignificantBits());
			data.writeLong(sharingPlayer.getLeastSignificantBits());
		}
		data.writeInt(startX);
		data.writeInt(startZ);
	}

	public static class Handler implements IMessageHandler<TESPacketFTScreen, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFTScreen packet, MessageContext context) {
			boolean custom = packet.isCustom;
			int wpID = packet.wpID;
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData playerData = TESLevelData.getData(entityplayer);
			TESAbstractWaypoint waypoint = null;
			if (custom) {
				UUID sharingPlayerID = packet.sharingPlayer;
				waypoint = sharingPlayerID != null ? playerData.getSharedCustomWaypointByID(sharingPlayerID, wpID) : playerData.getCustomWaypointByID(wpID);
			} else if (wpID >= 0 && wpID < TESWaypoint.values().length) {
				waypoint = TESWaypoint.values()[wpID];
			}
			if (waypoint != null) {
				TES.proxy.displayFTScreen(waypoint, packet.startX, packet.startZ);
			}
			return null;
		}
	}
}