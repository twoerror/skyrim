package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;

public class TESPacketMenuPrompt implements IMessage {
	private Type type;

	@SuppressWarnings("unused")
	public TESPacketMenuPrompt() {
	}

	public TESPacketMenuPrompt(Type t) {
		type = t;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte typeID = data.readByte();
		type = Type.values()[typeID];
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(type.ordinal());
	}

	public enum Type {
		MENU
	}

	public static class Handler implements IMessageHandler<TESPacketMenuPrompt, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMenuPrompt packet, MessageContext context) {
			TES.proxy.displayMenuPrompt(packet.type);
			return null;
		}
	}
}