package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;

public class TESPacketCancelItemHighlight implements IMessage {
	@Override
	public void fromBytes(ByteBuf data) {
	}

	@Override
	public void toBytes(ByteBuf data) {
	}

	public static class Handler implements IMessageHandler<TESPacketCancelItemHighlight, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCancelItemHighlight packet, MessageContext context) {
			TES.proxy.cancelItemHighlight();
			return null;
		}
	}
}