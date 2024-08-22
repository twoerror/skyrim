package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketUpdateViewingFaction implements IMessage {
	private TESFaction viewingFaction;

	@SuppressWarnings("unused")
	public TESPacketUpdateViewingFaction() {
	}

	public TESPacketUpdateViewingFaction(TESFaction f) {
		viewingFaction = f;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte facID = data.readByte();
		viewingFaction = TESFaction.forID(facID);
	}

	@Override
	public void toBytes(ByteBuf data) {
		int facID = viewingFaction.ordinal();
		data.writeByte(facID);
	}

	public static class Handler implements IMessageHandler<TESPacketUpdateViewingFaction, IMessage> {
		@Override
		public IMessage onMessage(TESPacketUpdateViewingFaction packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			pd.setViewingFaction(packet.viewingFaction);
			return null;
		}
	}
}