package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketStopItemUse implements IMessage {
	@Override
	public void fromBytes(ByteBuf data) {
	}

	@Override
	public void toBytes(ByteBuf data) {
	}

	public static class Handler implements IMessageHandler<TESPacketStopItemUse, IMessage> {
		@Override
		public IMessage onMessage(TESPacketStopItemUse packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			entityplayer.clearItemInUse();
			return null;
		}
	}
}