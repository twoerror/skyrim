package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketFellowshipRename extends TESPacketFellowshipDo {
	private String newName;

	@SuppressWarnings("unused")
	public TESPacketFellowshipRename() {
	}

	public TESPacketFellowshipRename(TESFellowshipClient fs, String name) {
		super(fs);
		newName = name;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		super.fromBytes(data);
		byte nameLength = data.readByte();
		ByteBuf nameBytes = data.readBytes(nameLength);
		newName = nameBytes.toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		super.toBytes(data);
		byte[] nameBytes = newName.getBytes(Charsets.UTF_8);
		data.writeByte(nameBytes.length);
		data.writeBytes(nameBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipRename, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipRename packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESFellowship fellowship = packet.getActiveFellowship();
			if (fellowship != null) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				playerData.renameFellowship(fellowship, packet.newName);
			}
			return null;
		}
	}
}