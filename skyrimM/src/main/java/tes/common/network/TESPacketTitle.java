package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESTitle;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;

public class TESPacketTitle implements IMessage {
	private TESTitle.PlayerTitle playerTitle;

	@SuppressWarnings("unused")
	public TESPacketTitle() {
	}

	public TESPacketTitle(TESTitle.PlayerTitle t) {
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

	public static class Handler implements IMessageHandler<TESPacketTitle, IMessage> {
		@Override
		public IMessage onMessage(TESPacketTitle packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESTitle.PlayerTitle title = packet.playerTitle;
			pd.setPlayerTitle(title);
			return null;
		}
	}
}