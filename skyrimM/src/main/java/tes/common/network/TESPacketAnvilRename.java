package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.inventory.TESContainerAnvil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import org.apache.commons.lang3.StringUtils;

public class TESPacketAnvilRename implements IMessage {
	private String rename;

	@SuppressWarnings("unused")
	public TESPacketAnvilRename() {
	}

	public TESPacketAnvilRename(String s) {
		rename = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		short nameLength = data.readShort();
		ByteBuf nameBytes = data.readBytes(nameLength);
		rename = nameBytes.toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		byte[] nameBytes = rename.getBytes(Charsets.UTF_8);
		data.writeShort(nameBytes.length);
		data.writeBytes(nameBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketAnvilRename, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAnvilRename packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerAnvil) {
				TESContainerAnvil anvil = (TESContainerAnvil) container;
				String rename = packet.rename;
				if (rename != null && !StringUtils.isBlank(rename)) {
					if (rename.length() <= 30) {
						anvil.updateItemName(rename);
					}
				} else {
					anvil.updateItemName("");
				}
			}
			return null;
		}
	}
}