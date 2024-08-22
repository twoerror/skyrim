package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.faction.TESFaction;
import tes.common.world.map.TESConquestGrid;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketConquestGridRequest implements IMessage {
	private TESFaction conqFac;

	@SuppressWarnings("unused")
	public TESPacketConquestGridRequest() {
	}

	public TESPacketConquestGridRequest(TESFaction fac) {
		conqFac = fac;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte facID = data.readByte();
		conqFac = TESFaction.forID(facID);
	}

	@Override
	public void toBytes(ByteBuf data) {
		int facID = conqFac.ordinal();
		data.writeByte(facID);
	}

	public static class Handler implements IMessageHandler<TESPacketConquestGridRequest, IMessage> {
		@Override
		public IMessage onMessage(TESPacketConquestGridRequest packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESFaction fac = packet.conqFac;
			if (fac != null) {
				TESConquestGrid.ConquestViewableQuery query = TESConquestGrid.canPlayerViewConquest(entityplayer, fac);
				if (query.getResult() == TESConquestGrid.ConquestViewable.CAN_VIEW) {
					TESConquestGrid.sendConquestGridTo(entityplayer, fac);
				}
			}
			return null;
		}
	}
}