package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.entity.other.inanimate.TESEntityCargocart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

public class TESPacketCargocart implements IMessage {
	private int load;
	private int cartId;

	@SuppressWarnings("unused")
	public TESPacketCargocart() {
	}

	public TESPacketCargocart(int loadIn, int cartIn) {
		load = loadIn;
		cartId = cartIn;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		load = buf.readInt();
		cartId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(load);
		buf.writeInt(cartId);
	}

	public static class Handler implements IMessageHandler<TESPacketCargocart, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCargocart message, MessageContext ctx) {
			TESEntityCargocart cart = (TESEntityCargocart) Minecraft.getMinecraft().theWorld.getEntityByID(message.cartId);
			cart.setLoad(message.load);
			return null;
		}
	}
}