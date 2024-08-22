package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketCheckMenuPrompt implements IMessage {
	private TESPacketMenuPrompt.Type type;

	@SuppressWarnings("unused")
	public TESPacketCheckMenuPrompt() {
	}

	public TESPacketCheckMenuPrompt(TESPacketMenuPrompt.Type t) {
		type = t;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte typeID = data.readByte();
		type = TESPacketMenuPrompt.Type.values()[typeID];
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(type.ordinal());
	}

	public static class Handler implements IMessageHandler<TESPacketCheckMenuPrompt, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCheckMenuPrompt packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (packet.type == TESPacketMenuPrompt.Type.MENU) {
				pd.setCheckedMenu(true);
			}
			return null;
		}
	}
}