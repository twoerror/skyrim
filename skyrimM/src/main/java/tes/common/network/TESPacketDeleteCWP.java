package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketDeleteCWP implements IMessage {
	private int wpID;

	@SuppressWarnings("unused")
	public TESPacketDeleteCWP() {
	}

	public TESPacketDeleteCWP(TESAbstractWaypoint wp) {
		wpID = wp.getID();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		wpID = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(wpID);
	}

	public static class Handler implements IMessageHandler<TESPacketDeleteCWP, IMessage> {
		@Override
		public IMessage onMessage(TESPacketDeleteCWP packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESCustomWaypoint cwp = pd.getCustomWaypointByID(packet.wpID);
			if (cwp != null) {
				pd.removeCustomWaypoint(cwp);
			}
			return null;
		}
	}
}