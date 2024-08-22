package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketFellowshipDisband extends TESPacketFellowshipDo {
	@SuppressWarnings("unused")
	public TESPacketFellowshipDisband() {
	}

	public TESPacketFellowshipDisband(TESFellowshipClient fs) {
		super(fs);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipDisband, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipDisband packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESFellowship fellowship = packet.getActiveFellowship();
			if (fellowship != null) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				playerData.disbandFellowship(fellowship, entityplayer.getCommandSenderName());
			}
			return null;
		}
	}
}