package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;

public class TESPacketFTBounceClient implements IMessage {
	@Override
	public void fromBytes(ByteBuf data) {
	}

	@Override
	public void toBytes(ByteBuf data) {
	}

	public static class Handler implements IMessageHandler<TESPacketFTBounceClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFTBounceClient packet, MessageContext context) {
			TES.proxy.getClientPlayer();
			IMessage packetResponse = new TESPacketFTBounceServer();
			TESPacketHandler.NETWORK_WRAPPER.sendToServer(packetResponse);
			return null;
		}
	}
}