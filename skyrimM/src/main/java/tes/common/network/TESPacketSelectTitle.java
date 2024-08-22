package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESTitle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumChatFormatting;

public class TESPacketSelectTitle implements IMessage {
	private TESTitle.PlayerTitle playerTitle;

	@SuppressWarnings("unused")
	public TESPacketSelectTitle() {
	}

	public TESPacketSelectTitle(TESTitle.PlayerTitle t) {
		playerTitle = t;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		short titleID = data.readShort();
		TESTitle title = TESTitle.forID(titleID);
		byte colorID = data.readByte();
		EnumChatFormatting color = TESTitle.PlayerTitle.colorForID(colorID);
		if (title != null && color != null) {
			playerTitle = new TESTitle.PlayerTitle(title, color);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		if (playerTitle == null) {
			data.writeShort(-1);
			data.writeByte(-1);
		} else {
			data.writeShort(playerTitle.getTitle().getTitleID());
			data.writeByte(playerTitle.getColor().getFormattingCode());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketSelectTitle, IMessage> {
		@Override
		public IMessage onMessage(TESPacketSelectTitle packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESTitle.PlayerTitle title = packet.playerTitle;
			if (title == null) {
				pd.setPlayerTitle(null);
			} else if (title.getTitle().canPlayerUse(entityplayer)) {
				pd.setPlayerTitle(title);
			}
			return null;
		}
	}
}