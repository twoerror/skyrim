package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.entity.other.inanimate.TESEntityNPCRespawner;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.io.IOException;

public class TESPacketNPCRespawner implements IMessage {
	private int spawnerID;
	private NBTTagCompound spawnerData;

	@SuppressWarnings("unused")
	public TESPacketNPCRespawner() {
	}

	public TESPacketNPCRespawner(TESEntityNPCRespawner spawner) {
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
	}

	public static class Handler implements IMessageHandler<TESPacketNPCRespawner, IMessage> {
		@Override
		public IMessage onMessage(TESPacketNPCRespawner packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			Entity entity = world.getEntityByID(packet.spawnerID);
			if (entity instanceof TESEntityNPCRespawner) {
				TESEntityNPCRespawner spawner = (TESEntityNPCRespawner) entity;
				spawner.readSpawnerDataFromNBT(packet.spawnerData);
				entityplayer.openGui(TES.instance, TESGuiId.NPC_RESPAWNER.ordinal(), world, entity.getEntityId(), 0, 0);
			}
			return null;
		}
	}
}