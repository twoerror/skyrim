package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TESPacketNPCIsEating implements IMessage {
	private int entityID;
	private boolean isEating;

	@SuppressWarnings("unused")
	public TESPacketNPCIsEating() {
	}

	public TESPacketNPCIsEating(int id, boolean flag) {
		entityID = id;
		isEating = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		isEating = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeBoolean(isEating);
	}

	public static class Handler implements IMessageHandler<TESPacketNPCIsEating, IMessage> {
		@Override
		public IMessage onMessage(TESPacketNPCIsEating packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				((TESEntityNPC) entity).setClientIsEating(packet.isEating);
			}
			return null;
		}
	}
}