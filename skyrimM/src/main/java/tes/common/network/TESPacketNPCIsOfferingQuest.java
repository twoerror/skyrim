package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TESPacketNPCIsOfferingQuest implements IMessage {
	private boolean offering;
	private int offerColor;
	private int entityID;

	@SuppressWarnings("unused")
	public TESPacketNPCIsOfferingQuest() {
	}

	public TESPacketNPCIsOfferingQuest(int id, boolean flag, int color) {
		entityID = id;
		offering = flag;
		offerColor = color;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		offering = data.readBoolean();
		offerColor = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeBoolean(offering);
		data.writeInt(offerColor);
	}

	public int getOfferColor() {
		return offerColor;
	}

	public boolean isOffering() {
		return offering;
	}

	public static class Handler implements IMessageHandler<TESPacketNPCIsOfferingQuest, IMessage> {
		@Override
		public IMessage onMessage(TESPacketNPCIsOfferingQuest packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				((TESEntityNPC) entity).getQuestInfo().receiveData(packet);
			}
			return null;
		}
	}
}