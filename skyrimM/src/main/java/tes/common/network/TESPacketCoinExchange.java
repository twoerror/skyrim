package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.inventory.TESContainerCoinExchange;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public class TESPacketCoinExchange implements IMessage {
	private int button;

	@SuppressWarnings("unused")
	public TESPacketCoinExchange() {
	}

	public TESPacketCoinExchange(int i) {
		button = i;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		button = data.readByte();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(button);
	}

	public static class Handler implements IMessageHandler<TESPacketCoinExchange, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCoinExchange packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerCoinExchange) {
				TESContainerCoinExchange coinExchange = (TESContainerCoinExchange) container;
				coinExchange.handleExchangePacket(packet.button);
			}
			return null;
		}
	}
}