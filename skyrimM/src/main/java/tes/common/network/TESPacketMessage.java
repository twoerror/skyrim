package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESGuiMessageTypes;
import io.netty.buffer.ByteBuf;

public class TESPacketMessage implements IMessage {
	private TESGuiMessageTypes message;

	@SuppressWarnings("unused")
	public TESPacketMessage() {
	}

	public TESPacketMessage(TESGuiMessageTypes m) {
		message = m;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte messageID = data.readByte();
		if (messageID < 0 || messageID >= TESGuiMessageTypes.values().length) {
			FMLLog.severe("Failed to display TES message: There is no message with ID " + messageID);
			message = null;
		} else {
			message = TESGuiMessageTypes.values()[messageID];
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(message.ordinal());
	}

	public static class Handler implements IMessageHandler<TESPacketMessage, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMessage packet, MessageContext context) {
			if (packet.message != null) {
				TES.proxy.displayMessage(packet.message);
			}
			return null;
		}
	}
}