package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.client.effect.TESEntitySwordCommandMarker;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

public class TESPacketLocationFX implements IMessage {
	private Type type;
	private double posX;
	private double posY;
	private double posZ;

	@SuppressWarnings("unused")
	public TESPacketLocationFX() {
	}

	public TESPacketLocationFX(Type t, double x, double y, double z) {
		type = t;
		posX = x;
		posY = y;
		posZ = z;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		int typeID = data.readByte();
		type = Type.values()[typeID];
		posX = data.readDouble();
		posY = data.readDouble();
		posZ = data.readDouble();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(type.ordinal());
		data.writeDouble(posX);
		data.writeDouble(posY);
		data.writeDouble(posZ);
	}

	public enum Type {
		SWORD_COMMAND
	}

	public static class Handler implements IMessageHandler<TESPacketLocationFX, IMessage> {
		@Override
		public IMessage onMessage(TESPacketLocationFX packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			double x = packet.posX;
			double y = packet.posY;
			double z = packet.posZ;
			if (packet.type == TESPacketLocationFX.Type.SWORD_COMMAND) {
				TESEntitySwordCommandMarker marker = new TESEntitySwordCommandMarker(world, x, y + 6.0D, z);
				world.spawnEntityInWorld(marker);
			}
			return null;
		}
	}
}