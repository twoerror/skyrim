package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketFellowshipCreate implements IMessage {
	private String fellowshipName;

	@SuppressWarnings("unused")
	public TESPacketFellowshipCreate() {
	}

	public TESPacketFellowshipCreate(String name) {
		fellowshipName = name;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte nameLength = data.readByte();
		ByteBuf nameBytes = data.readBytes(nameLength);
		fellowshipName = nameBytes.toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		byte[] nameBytes = fellowshipName.getBytes(Charsets.UTF_8);
		data.writeByte(nameBytes.length);
		data.writeBytes(nameBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipCreate, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipCreate packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData playerData = TESLevelData.getData(entityplayer);
			playerData.createFellowship(packet.fellowshipName, true);
			return null;
		}
	}
}