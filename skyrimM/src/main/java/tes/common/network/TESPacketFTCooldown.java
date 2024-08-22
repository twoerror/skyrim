package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import io.netty.buffer.ByteBuf;

public class TESPacketFTCooldown implements IMessage {
	private int cooldownMax;
	private int cooldownMin;

	@SuppressWarnings("unused")
	public TESPacketFTCooldown() {
	}

	public TESPacketFTCooldown(int max, int min) {
		cooldownMax = max;
		cooldownMin = min;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cooldownMax = data.readInt();
		cooldownMin = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cooldownMax);
		data.writeInt(cooldownMin);
	}

	public static class Handler implements IMessageHandler<TESPacketFTCooldown, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFTCooldown packet, MessageContext context) {
			TESLevelData.setWaypointCooldown(packet.cooldownMax, packet.cooldownMin);
			return null;
		}
	}
}