package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;

public class TESPacketConquestNotification implements IMessage {
	private TESFaction conqFac;
	private float conqVal;
	private boolean isCleansing;

	@SuppressWarnings("unused")
	public TESPacketConquestNotification() {
	}

	public TESPacketConquestNotification(TESFaction fac, float f, boolean clean) {
		conqFac = fac;
		conqVal = f;
		isCleansing = clean;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte facID = data.readByte();
		conqFac = TESFaction.forID(facID);
		conqVal = data.readFloat();
		isCleansing = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(conqFac.ordinal());
		data.writeFloat(conqVal);
		data.writeBoolean(isCleansing);
	}

	public static class Handler implements IMessageHandler<TESPacketConquestNotification, IMessage> {
		@Override
		public IMessage onMessage(TESPacketConquestNotification packet, MessageContext context) {
			if (packet.conqFac != null) {
				TES.proxy.queueConquestNotification(packet.conqFac, packet.conqVal, packet.isCleansing);
			}
			return null;
		}
	}
}