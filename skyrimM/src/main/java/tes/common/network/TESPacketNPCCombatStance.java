package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class TESPacketNPCCombatStance implements IMessage {
	private int entityID;
	private boolean combatStance;

	@SuppressWarnings("unused")
	public TESPacketNPCCombatStance() {
	}

	public TESPacketNPCCombatStance(int id, boolean flag) {
		entityID = id;
		combatStance = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		combatStance = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeBoolean(combatStance);
	}

	public static class Handler implements IMessageHandler<TESPacketNPCCombatStance, IMessage> {
		@Override
		public IMessage onMessage(TESPacketNPCCombatStance packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				((TESEntityNPC) entity).setClientCombatStance(packet.combatStance);
			}
			return null;
		}
	}
}