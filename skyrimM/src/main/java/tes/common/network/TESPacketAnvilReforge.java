package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.inventory.TESContainerAnvil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public class TESPacketAnvilReforge implements IMessage {
	@Override
	public void fromBytes(ByteBuf data) {
	}

	@Override
	public void toBytes(ByteBuf data) {
	}

	public static class Handler implements IMessageHandler<TESPacketAnvilReforge, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAnvilReforge packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerAnvil) {
				TESContainerAnvil anvil = (TESContainerAnvil) container;
				anvil.reforgeItem();
			}
			return null;
		}
	}
}