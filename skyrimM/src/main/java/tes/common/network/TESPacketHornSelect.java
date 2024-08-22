package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.database.TESItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

public class TESPacketHornSelect implements IMessage {
	private int hornType;

	@SuppressWarnings("unused")
	public TESPacketHornSelect() {
	}

	public TESPacketHornSelect(int h) {
		hornType = h;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		hornType = data.readByte();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(hornType);
	}

	public static class Handler implements IMessageHandler<TESPacketHornSelect, IMessage> {
		@Override
		public IMessage onMessage(TESPacketHornSelect packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			ItemStack itemstack = entityplayer.inventory.getCurrentItem();
			if (itemstack != null && itemstack.getItem() == TESItems.commandHorn && itemstack.getItemDamage() == 0) {
				itemstack.setItemDamage(packet.hornType);
			}
			return null;
		}
	}
}