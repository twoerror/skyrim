package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketPledgeSet implements IMessage {
	private TESFaction pledgeFac;

	@SuppressWarnings("unused")
	public TESPacketPledgeSet() {
	}

	public TESPacketPledgeSet(TESFaction f) {
		pledgeFac = f;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte facID = data.readByte();
		pledgeFac = facID == -1 ? null : TESFaction.forID(facID);
	}

	@Override
	public void toBytes(ByteBuf data) {
		int facID = pledgeFac == null ? -1 : pledgeFac.ordinal();
		data.writeByte(facID);
	}

	public static class Handler implements IMessageHandler<TESPacketPledgeSet, IMessage> {
		@Override
		public IMessage onMessage(TESPacketPledgeSet packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESFaction fac = packet.pledgeFac;
			if (fac == null) {
				pd.revokePledgeFaction(entityplayer, true);
			} else if (pd.canPledgeTo(fac) && pd.canMakeNewPledge()) {
				pd.setPledgeFaction(fac);
			}
			return null;
		}
	}
}