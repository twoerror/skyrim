package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketPledge implements IMessage {
	private TESFaction pledgeFac;

	@SuppressWarnings("unused")
	public TESPacketPledge() {
	}

	public TESPacketPledge(TESFaction f) {
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

	public static class Handler implements IMessageHandler<TESPacketPledge, IMessage> {
		@Override
		public IMessage onMessage(TESPacketPledge packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				TESPlayerData pd = TESLevelData.getData(entityplayer);
				pd.setPledgeFaction(packet.pledgeFac);
			}
			return null;
		}
	}
}