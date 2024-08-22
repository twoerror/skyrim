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

public class TESPacketWaypointUseCount implements IMessage {
	private boolean isCustom;
	private int wpID;
	private int useCount;
	private UUID sharingPlayer;

	@SuppressWarnings("unused")
	public TESPacketWaypointUseCount() {
	}

	public TESPacketWaypointUseCount(TESAbstractWaypoint wp, int count) {
		isCustom = wp instanceof TESCustomWaypoint;
		wpID = wp.getID();
		useCount = count;
		if (wp instanceof TESCustomWaypoint) {
			sharingPlayer = ((TESCustomWaypoint) wp).getSharingPlayerID();
		}
	}

	@Override
	public void fromBytes(ByteBuf data) {
		isCustom = data.readBoolean();
		wpID = data.readInt();
		useCount = data.readInt();
		boolean shared = data.readBoolean();
		if (shared) {
			sharingPlayer = new UUID(data.readLong(), data.readLong());
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeBoolean(isCustom);
		data.writeInt(wpID);
		data.writeInt(useCount);
		boolean shared = sharingPlayer != null;
		data.writeBoolean(shared);
		if (shared) {
			data.writeLong(sharingPlayer.getMostSignificantBits());
			data.writeLong(sharingPlayer.getLeastSignificantBits());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketWaypointUseCount, IMessage> {
		@Override
		public IMessage onMessage(TESPacketWaypointUseCount packet, MessageContext context) {
			boolean custom = packet.isCustom;
			int wpID = packet.wpID;
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESAbstractWaypoint waypoint = null;
			if (custom) {
				UUID sharingPlayerID = packet.sharingPlayer;
				waypoint = sharingPlayerID != null ? pd.getSharedCustomWaypointByID(sharingPlayerID, wpID) : pd.getCustomWaypointByID(wpID);
			} else if (wpID >= 0 && wpID < TESWaypoint.values().length) {
				waypoint = TESWaypoint.values()[wpID];
			}
			if (waypoint != null) {
				pd.setWPUseCount(waypoint, packet.useCount);
			}
			return null;
		}
	}
}