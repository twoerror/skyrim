package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.inanimate.TESEntityInvasionSpawner;
import io.netty.buffer.ByteBuf;

public class TESPacketInvasionWatch implements IMessage {
	private int invasionEntityID;
	private boolean overrideAlreadyWatched;

	@SuppressWarnings("unused")
	public TESPacketInvasionWatch() {
	}

	public TESPacketInvasionWatch(TESEntityInvasionSpawner invasion, boolean override) {
		invasionEntityID = invasion.getEntityId();
		overrideAlreadyWatched = override;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		invasionEntityID = data.readInt();
		overrideAlreadyWatched = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(invasionEntityID);
		data.writeBoolean(overrideAlreadyWatched);
	}

	public static class Handler implements IMessageHandler<TESPacketInvasionWatch, IMessage> {
		@Override
		public IMessage onMessage(TESPacketInvasionWatch packet, MessageContext context) {
			TES.proxy.handleInvasionWatch(packet.invasionEntityID, packet.overrideAlreadyWatched);
			return null;
		}
	}
}