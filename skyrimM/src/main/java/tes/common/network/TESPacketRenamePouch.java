package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.inventory.TESContainerPouch;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;

public class TESPacketRenamePouch implements IMessage {
	private String name;

	@SuppressWarnings("unused")
	public TESPacketRenamePouch() {
	}

	public TESPacketRenamePouch(String s) {
		name = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		short length = data.readShort();
		name = data.readBytes(length).toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		byte[] nameData = name.getBytes(Charsets.UTF_8);
		data.writeShort(nameData.length);
		data.writeBytes(nameData);
	}

	public static class Handler implements IMessageHandler<TESPacketRenamePouch, IMessage> {
		@Override
		public IMessage onMessage(TESPacketRenamePouch packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerPouch) {
				TESContainerPouch pouch = (TESContainerPouch) container;
				pouch.renamePouch(packet.name);
			}
			return null;
		}
	}
}