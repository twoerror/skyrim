package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import io.netty.buffer.ByteBuf;

public class TESPacketEnableAlignmentZones implements IMessage {
	private boolean enable;

	@SuppressWarnings("unused")
	public TESPacketEnableAlignmentZones() {
	}

	public TESPacketEnableAlignmentZones(boolean flag) {
		enable = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		enable = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeBoolean(enable);
	}

	public static class Handler implements IMessageHandler<TESPacketEnableAlignmentZones, IMessage> {
		@Override
		public IMessage onMessage(TESPacketEnableAlignmentZones packet, MessageContext context) {
			TESLevelData.setEnableAlignmentZones(packet.enable);
			return null;
		}
	}
}