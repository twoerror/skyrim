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

public class TESPacketFellowshipRespondInvite extends TESPacketFellowshipDo {
	private boolean accept;

	@SuppressWarnings("unused")
	public TESPacketFellowshipRespondInvite() {
	}

	public TESPacketFellowshipRespondInvite(TESFellowshipClient fs, boolean accepts) {
		super(fs);
		accept = accepts;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		super.fromBytes(data);
		accept = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		super.toBytes(data);
		data.writeBoolean(accept);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipRespondInvite, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipRespondInvite packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData playerData = TESLevelData.getData(entityplayer);
			TESFellowship fellowship = packet.getActiveOrDisbandedFellowship();
			if (fellowship != null) {
				if (packet.accept) {
					playerData.acceptFellowshipInvite(fellowship, true);
				} else {
					playerData.rejectFellowshipInvite(fellowship);
				}
			} else {
				IMessage resultPacket = new TESPacketFellowshipAcceptInviteResult(TESPacketFellowshipAcceptInviteResult.AcceptInviteResult.NONEXISTENT);
				TESPacketHandler.NETWORK_WRAPPER.sendTo(resultPacket, entityplayer);
			}
			return null;
		}
	}
}