package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketFellowshipLeave extends TESPacketFellowshipDo {
	@SuppressWarnings("unused")
	public TESPacketFellowshipLeave() {
	}

	public TESPacketFellowshipLeave(TESFellowshipClient fs) {
		super(fs);
	}

	@Override
	public void fromBytes(ByteBuf data) {
		super.fromBytes(data);
	}

	@Override
	public void toBytes(ByteBuf data) {
		super.toBytes(data);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipLeave, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipLeave packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESFellowship fellowship = packet.getActiveFellowship();
			if (fellowship != null) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				playerData.leaveFellowship(fellowship);
			}
			return null;
		}
	}
}