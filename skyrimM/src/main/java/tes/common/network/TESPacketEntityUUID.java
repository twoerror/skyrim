package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

import java.util.UUID;

public class TESPacketEntityUUID implements IMessage {
	private int entityID;
	private UUID entityUUID;

	@SuppressWarnings("unused")
	public TESPacketEntityUUID() {
	}

	public TESPacketEntityUUID(int id, UUID uuid) {
		entityID = id;
		entityUUID = uuid;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		entityUUID = new UUID(data.readLong(), data.readLong());
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeLong(entityUUID.getMostSignificantBits());
		data.writeLong(entityUUID.getLeastSignificantBits());
	}

	public static class Handler implements IMessageHandler<TESPacketEntityUUID, IMessage> {
		@Override
		public IMessage onMessage(TESPacketEntityUUID packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESRandomSkinEntity) {
				TESRandomSkinEntity npc = (TESRandomSkinEntity) entity;
				npc.setUniqueID(packet.entityUUID);
			}
			return null;
		}
	}
}