package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;

public class TESPacketIsOpResponse implements IMessage {
	private boolean isOp;

	@SuppressWarnings("unused")
	public TESPacketIsOpResponse() {
	}

	public TESPacketIsOpResponse(boolean flag) {
		isOp = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		isOp = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeBoolean(isOp);
	}

	public static class Handler implements IMessageHandler<TESPacketIsOpResponse, IMessage> {
		@Override
		public IMessage onMessage(TESPacketIsOpResponse packet, MessageContext context) {
			TES.proxy.setMapIsOp(packet.isOp);
			return null;
		}
	}
}