package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.quest.TESMiniQuestEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketClientMQEvent implements IMessage {
	private ClientMQEvent type;

	@SuppressWarnings("unused")
	public TESPacketClientMQEvent() {
	}

	public TESPacketClientMQEvent(ClientMQEvent t) {
		type = t;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte typeID = data.readByte();
		if (typeID >= 0 && typeID < ClientMQEvent.values().length) {
			type = ClientMQEvent.values()[typeID];
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(type.ordinal());
	}

	public enum ClientMQEvent {
		MAP, FACTIONS
	}

	public static class Handler implements IMessageHandler<TESPacketClientMQEvent, IMessage> {
		@Override
		public IMessage onMessage(TESPacketClientMQEvent packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (packet.type == ClientMQEvent.MAP) {
				pd.distributeMQEvent(new TESMiniQuestEvent.ViewMap());
			} else if (packet.type == ClientMQEvent.FACTIONS) {
				pd.distributeMQEvent(new TESMiniQuestEvent.ViewFactions());
			}
			return null;
		}
	}
}