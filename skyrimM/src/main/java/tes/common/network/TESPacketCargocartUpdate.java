package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.entity.other.inanimate.TESEntityCart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityHorse;

public class TESPacketCargocartUpdate implements IMessage {
	private int pullingId;
	private int cartId;

	@SuppressWarnings("unused")
	public TESPacketCargocartUpdate() {
	}

	public TESPacketCargocartUpdate(int horseIn, int cartIn) {
		pullingId = horseIn;
		cartId = cartIn;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pullingId = buf.readInt();
		cartId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pullingId);
		buf.writeInt(cartId);
	}

	public static class Handler implements IMessageHandler<TESPacketCargocartUpdate, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCargocartUpdate message, MessageContext ctx) {
			EntityHorse pulling = null;
			TESEntityCart cart = (TESEntityCart) Minecraft.getMinecraft().theWorld.getEntityByID(message.cartId);
			if (message.pullingId >= 0) {
				pulling = (EntityHorse) Minecraft.getMinecraft().theWorld.getEntityByID(message.pullingId);
			}
			cart.setPulling(pulling);
			return null;
		}
	}
}