package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESSquadrons;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StringUtils;

public class TESPacketItemSquadron implements IMessage {
	private String squadron;

	@SuppressWarnings("unused")
	public TESPacketItemSquadron() {
	}

	public TESPacketItemSquadron(String s) {
		squadron = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		int length = data.readInt();
		if (length > -1) {
			squadron = data.readBytes(length).toString(Charsets.UTF_8);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		if (StringUtils.isNullOrEmpty(squadron)) {
			data.writeInt(-1);
		} else {
			byte[] sqBytes = squadron.getBytes(Charsets.UTF_8);
			data.writeInt(sqBytes.length);
			data.writeBytes(sqBytes);
		}
	}

	public static class Handler implements IMessageHandler<TESPacketItemSquadron, IMessage> {
		@Override
		public IMessage onMessage(TESPacketItemSquadron packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			ItemStack itemstack = entityplayer.getCurrentEquippedItem();
			if (itemstack != null && itemstack.getItem() instanceof TESSquadrons.SquadronItem) {
				String squadron = packet.squadron;
				if (StringUtils.isNullOrEmpty(squadron)) {
					TESSquadrons.setSquadron(itemstack, "");
				} else {
					squadron = TESSquadrons.checkAcceptableLength(squadron);
					TESSquadrons.setSquadron(itemstack, squadron);
				}
			}
			return null;
		}
	}
}