package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;

public class TESPacketEnvironmentOverlay implements IMessage {
	private Overlay overlay;

	@SuppressWarnings("unused")
	public TESPacketEnvironmentOverlay() {
	}

	public TESPacketEnvironmentOverlay(Overlay o) {
		overlay = o;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte overlayID = data.readByte();
		overlay = Overlay.values()[overlayID];
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(overlay.ordinal());
	}

	public enum Overlay {
		FROST, BURN
	}

	public static class Handler implements IMessageHandler<TESPacketEnvironmentOverlay, IMessage> {
		@Override
		public IMessage onMessage(TESPacketEnvironmentOverlay packet, MessageContext context) {
			if (packet.overlay == Overlay.FROST) {
				TES.proxy.showFrostOverlay();
			} else if (packet.overlay == Overlay.BURN) {
				TES.proxy.showBurnOverlay();
			}
			return null;
		}
	}
}