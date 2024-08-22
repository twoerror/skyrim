package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TESPacketMiniquestOfferClose implements IMessage {
	private int npcID;
	private boolean accept;

	@SuppressWarnings("unused")
	public TESPacketMiniquestOfferClose() {
	}

	public TESPacketMiniquestOfferClose(int id, boolean flag) {
		npcID = id;
		accept = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		npcID = data.readInt();
		accept = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(npcID);
		data.writeBoolean(accept);
	}

	public static class Handler implements IMessageHandler<TESPacketMiniquestOfferClose, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMiniquestOfferClose packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			Entity npcEntity = world.getEntityByID(packet.npcID);
			if (npcEntity instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) npcEntity;
				npc.getQuestInfo().receiveOfferResponse(entityplayer, packet.accept);
			}
			return null;
		}
	}
}