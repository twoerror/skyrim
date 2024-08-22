package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TESPacketDeleteCWPClient implements IMessage {
	private int cwpID;
	private UUID sharingPlayer;

	@SuppressWarnings("unused")
	public TESPacketDeleteCWPClient() {
	}

	public TESPacketDeleteCWPClient(int id) {
		cwpID = id;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cwpID = data.readInt();
		boolean shared = data.readBoolean();
		if (shared) {
			sharingPlayer = new UUID(data.readLong(), data.readLong());
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cwpID);
		boolean shared = sharingPlayer != null;
		data.writeBoolean(shared);
		if (shared) {
			data.writeLong(sharingPlayer.getMostSignificantBits());
			data.writeLong(sharingPlayer.getLeastSignificantBits());
		}
	}

	public TESPacketDeleteCWPClient setSharingPlayer(UUID player) {
		sharingPlayer = player;
		return this;
	}

	public static class Handler implements IMessageHandler<TESPacketDeleteCWPClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketDeleteCWPClient packet, MessageContext context) {
			TESCustomWaypoint cwp;
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (packet.sharingPlayer != null) {
				TESCustomWaypoint cwp2;
				if (!TES.proxy.isSingleplayer() && (cwp2 = pd.getSharedCustomWaypointByID(packet.sharingPlayer, packet.cwpID)) != null) {
					pd.removeSharedCustomWaypoint(cwp2);
				}
			} else if (!TES.proxy.isSingleplayer() && (cwp = pd.getCustomWaypointByID(packet.cwpID)) != null) {
				pd.removeCustomWaypointClient(cwp);
			}
			return null;
		}
	}
}