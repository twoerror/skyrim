package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class TESPacketCWPSharedHideClient implements IMessage {
	private int cwpID;
	private UUID sharingPlayer;
	private boolean hideCWP;

	@SuppressWarnings("unused")
	public TESPacketCWPSharedHideClient() {
	}

	public TESPacketCWPSharedHideClient(int id, UUID player, boolean hide) {
		cwpID = id;
		sharingPlayer = player;
		hideCWP = hide;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cwpID = data.readInt();
		sharingPlayer = new UUID(data.readLong(), data.readLong());
		hideCWP = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cwpID);
		data.writeLong(sharingPlayer.getMostSignificantBits());
		data.writeLong(sharingPlayer.getLeastSignificantBits());
		data.writeBoolean(hideCWP);
	}

	public static class Handler implements IMessageHandler<TESPacketCWPSharedHideClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCWPSharedHideClient packet, MessageContext context) {
			TESCustomWaypoint cwp;
			TESPlayerData pd;
			if (!TES.proxy.isSingleplayer() && (cwp = (pd = TESLevelData.getData(TES.proxy.getClientPlayer())).getSharedCustomWaypointByID(packet.sharingPlayer, packet.cwpID)) != null) {
				pd.hideOrUnhideSharedCustomWaypoint(cwp, packet.hideCWP);
			}
			return null;
		}
	}
}