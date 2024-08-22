package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.entity.other.inanimate.TESEntityNPCRespawner;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.io.IOException;

public class TESPacketEditNPCRespawner implements IMessage {
	private boolean destroy;
	private int spawnerID;
	private NBTTagCompound spawnerData;

	@SuppressWarnings("unused")
	public TESPacketEditNPCRespawner() {
	}

	public TESPacketEditNPCRespawner(TESEntityNPCRespawner spawner) {
		spawnerID = spawner.getEntityId();
		spawnerData = new NBTTagCompound();
		spawner.writeSpawnerDataToNBT(spawnerData);
	}

	@Override
	public void fromBytes(ByteBuf data) {
		spawnerID = data.readInt();
		try {
			spawnerData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error reading spawner data");
			e.printStackTrace();
		}
		destroy = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(spawnerID);
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(spawnerData);
		} catch (IOException e) {
			FMLLog.severe("Error writing spawner data");
			e.printStackTrace();
		}
		data.writeBoolean(destroy);
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	public static class Handler implements IMessageHandler<TESPacketEditNPCRespawner, IMessage> {
		@Override
		public IMessage onMessage(TESPacketEditNPCRespawner packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			Entity sEntity = world.getEntityByID(packet.spawnerID);
			if (sEntity instanceof TESEntityNPCRespawner) {
				TESEntityNPCRespawner spawner = (TESEntityNPCRespawner) sEntity;
				if (entityplayer.capabilities.isCreativeMode) {
					spawner.readSpawnerDataFromNBT(packet.spawnerData);
				}
				if (packet.destroy) {
					spawner.onBreak();
				}
			}
			return null;
		}
	}
}