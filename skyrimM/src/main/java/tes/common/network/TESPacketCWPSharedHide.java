package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class TESPacketCWPSharedHide implements IMessage {
	private int cwpID;
	private UUID sharingPlayer;
	private boolean hideCWP;

	@SuppressWarnings("unused")
	public TESPacketCWPSharedHide() {
	}

	public TESPacketCWPSharedHide(TESCustomWaypoint cwp, boolean hide) {
		cwpID = cwp.getID();
		sharingPlayer = cwp.getSharingPlayerID();
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

	public static class Handler implements IMessageHandler<TESPacketCWPSharedHide, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCWPSharedHide packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESCustomWaypoint cwp = pd.getSharedCustomWaypointByID(packet.sharingPlayer, packet.cwpID);
			if (cwp != null) {
				pd.hideOrUnhideSharedCustomWaypoint(cwp, packet.hideCWP);
			}
			return null;
		}
	}
}