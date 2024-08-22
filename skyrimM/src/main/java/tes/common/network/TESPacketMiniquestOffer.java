package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.entity.other.TESEntityNPC;
import tes.common.quest.TESMiniQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

import java.io.IOException;

public class TESPacketMiniquestOffer implements IMessage {
	private int entityID;
	private NBTTagCompound miniquestData;

	@SuppressWarnings("unused")
	public TESPacketMiniquestOffer() {
	}

	public TESPacketMiniquestOffer(int id, NBTTagCompound nbt) {
		entityID = id;
		miniquestData = nbt;
	}

	public static void sendClosePacket(EntityPlayer entityplayer, TESEntityNPC npc, boolean accept) {
		if (entityplayer == null) {
			FMLLog.warning("TES Warning: Tried to send miniquest offer close packet, but player == null");
			return;
		}
		IMessage packet = new TESPacketMiniquestOfferClose(npc.getEntityId(), accept);
		TESPacketHandler.NETWORK_WRAPPER.sendToServer(packet);
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		try {
			miniquestData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error reading miniquest data");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(miniquestData);
		} catch (IOException e) {
			FMLLog.severe("Error writing miniquest data");
			e.printStackTrace();
		}
	}

	public static class Handler implements IMessageHandler<TESPacketMiniquestOffer, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMiniquestOffer packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entity;
				TESMiniQuest quest = TESMiniQuest.loadQuestFromNBT(packet.miniquestData, pd);
				if (quest != null) {
					TES.proxy.displayMiniquestOffer(quest, npc);
				} else {
					sendClosePacket(entityplayer, npc, false);
				}
			}
			return null;
		}
	}
}