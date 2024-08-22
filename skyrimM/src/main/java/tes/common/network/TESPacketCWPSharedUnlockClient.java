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

public class TESPacketCWPSharedUnlockClient implements IMessage {
	private int cwpID;
	private UUID sharingPlayer;

	@SuppressWarnings("unused")
	public TESPacketCWPSharedUnlockClient() {
	}

	public TESPacketCWPSharedUnlockClient(int id, UUID player) {
		cwpID = id;
		sharingPlayer = player;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cwpID = data.readInt();
		sharingPlayer = new UUID(data.readLong(), data.readLong());
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cwpID);
		data.writeLong(sharingPlayer.getMostSignificantBits());
		data.writeLong(sharingPlayer.getLeastSignificantBits());
	}

	public static class Handler implements IMessageHandler<TESPacketCWPSharedUnlockClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCWPSharedUnlockClient packet, MessageContext context) {
			TESCustomWaypoint cwp;
			TESPlayerData pd;
			if (!TES.proxy.isSingleplayer() && (cwp = (pd = TESLevelData.getData(TES.proxy.getClientPlayer())).getSharedCustomWaypointByID(packet.sharingPlayer, packet.cwpID)) != null) {
				pd.unlockSharedCustomWaypoint(cwp);
			}
			return null;
		}
	}
}