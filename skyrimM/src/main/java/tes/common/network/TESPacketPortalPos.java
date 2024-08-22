package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import io.netty.buffer.ByteBuf;

public class TESPacketPortalPos implements IMessage {
	private int portalX;
	private int portalY;
	private int portalZ;

	@SuppressWarnings("unused")
	public TESPacketPortalPos() {
	}

	public TESPacketPortalPos(int i, int j, int k) {
		portalX = i;
		portalY = j;
		portalZ = k;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		portalX = data.readInt();
		portalY = data.readInt();
		portalZ = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(portalX);
		data.writeInt(portalY);
		data.writeInt(portalZ);
	}

	public static class Handler implements IMessageHandler<TESPacketPortalPos, IMessage> {
		@Override
		public IMessage onMessage(TESPacketPortalPos packet, MessageContext context) {
			TESLevelData.setGameOfThronesPortalX(packet.portalX);
			TESLevelData.setGameOfThronesPortalY(packet.portalY);
			TESLevelData.setGameOfThronesPortalZ(packet.portalZ);
			return null;
		}
	}
}