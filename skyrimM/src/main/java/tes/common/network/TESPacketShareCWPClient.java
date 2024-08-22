package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowshipClient;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class TESPacketShareCWPClient implements IMessage {
	private int cwpID;
	private UUID fellowshipID;
	private boolean adding;

	@SuppressWarnings("unused")
	public TESPacketShareCWPClient() {
	}

	public TESPacketShareCWPClient(int id, UUID fsID, boolean add) {
		cwpID = id;
		fellowshipID = fsID;
		adding = add;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cwpID = data.readInt();
		fellowshipID = new UUID(data.readLong(), data.readLong());
		adding = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cwpID);
		data.writeLong(fellowshipID.getMostSignificantBits());
		data.writeLong(fellowshipID.getLeastSignificantBits());
		data.writeBoolean(adding);
	}

	public static class Handler implements IMessageHandler<TESPacketShareCWPClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketShareCWPClient packet, MessageContext context) {
			TESCustomWaypoint cwp;
			TESPlayerData pd;
			if (!TES.proxy.isSingleplayer() && (cwp = (pd = TESLevelData.getData(TES.proxy.getClientPlayer())).getCustomWaypointByID(packet.cwpID)) != null) {
				if (packet.adding) {
					TESFellowshipClient fsClient = pd.getClientFellowshipByID(packet.fellowshipID);
					if (fsClient != null) {
						TESPlayerData.customWaypointAddSharedFellowshipClient(cwp, fsClient);
					}
				} else {
					TESPlayerData.customWaypointRemoveSharedFellowshipClient(cwp, packet.fellowshipID);
				}
			}
			return null;
		}
	}
}