package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.IChatComponent;

public class TESPacketFellowshipNotification implements IMessage {
	private IChatComponent message;

	@SuppressWarnings("unused")
	public TESPacketFellowshipNotification() {
	}

	public TESPacketFellowshipNotification(IChatComponent c) {
		message = c;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		int length = data.readInt();
		ByteBuf srlBytes = data.readBytes(length);
		String serialised = srlBytes.toString(Charsets.UTF_8);
		message = IChatComponent.Serializer.func_150699_a(serialised);
	}

	@Override
	public void toBytes(ByteBuf data) {
		String serialised = IChatComponent.Serializer.func_150696_a(message);
		byte[] srlBytes = serialised.getBytes(Charsets.UTF_8);
		data.writeInt(srlBytes.length);
		data.writeBytes(srlBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipNotification, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipNotification packet, MessageContext context) {
			TES.proxy.queueFellowshipNotification(packet.message);
			return null;
		}
	}
}