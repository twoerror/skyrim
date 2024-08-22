package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.fellowship.TESFellowship;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TESPacketFellowshipRemove implements IMessage {
	private UUID fellowshipID;
	private boolean isInvite;

	@SuppressWarnings("unused")
	public TESPacketFellowshipRemove() {
	}

	public TESPacketFellowshipRemove(TESFellowship fs, boolean invite) {
		fellowshipID = fs.getFellowshipID();
		isInvite = invite;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		fellowshipID = new UUID(data.readLong(), data.readLong());
		isInvite = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(fellowshipID.getMostSignificantBits());
		data.writeLong(fellowshipID.getLeastSignificantBits());
		data.writeBoolean(isInvite);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipRemove, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipRemove packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			if (packet.isInvite) {
				TESLevelData.getData(entityplayer).removeClientFellowshipInvite(packet.fellowshipID);
			} else {
				TESLevelData.getData(entityplayer).removeClientFellowship(packet.fellowshipID);
			}
			return null;
		}
	}
}