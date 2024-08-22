package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.quest.TESMiniQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class TESPacketMiniquest implements IMessage {
	private NBTTagCompound miniquestData;
	private boolean completed;

	@SuppressWarnings("unused")
	public TESPacketMiniquest() {
	}

	public TESPacketMiniquest(NBTTagCompound nbt, boolean flag) {
		miniquestData = nbt;
		completed = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		try {
			miniquestData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Hummel009: Error reading miniquest data");
			e.printStackTrace();
		}
		completed = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(miniquestData);
		} catch (IOException e) {
			FMLLog.severe("Hummel009: Error writing miniquest data");
			e.printStackTrace();
		}
		data.writeBoolean(completed);
	}

	public static class Handler implements IMessageHandler<TESPacketMiniquest, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMiniquest packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				TESPlayerData pd = TESLevelData.getData(entityplayer);
				TESMiniQuest miniquest = TESMiniQuest.loadQuestFromNBT(packet.miniquestData, pd);
				if (miniquest != null) {
					TESMiniQuest existingQuest = pd.getMiniQuestForID(miniquest.getQuestUUID(), packet.completed);
					if (existingQuest == null) {
						if (packet.completed) {
							pd.addMiniQuestCompleted(miniquest);
						} else {
							pd.addMiniQuest(miniquest);
						}
					} else {
						existingQuest.readFromNBT(packet.miniquestData);
					}
				}
			}
			return null;
		}
	}
}