package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class TESPacketFactionData implements IMessage {
	private TESFaction faction;
	private NBTTagCompound factionNBT;

	@SuppressWarnings("unused")
	public TESPacketFactionData() {
	}

	public TESPacketFactionData(TESFaction f, NBTTagCompound nbt) {
		faction = f;
		factionNBT = nbt;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte factionID = data.readByte();
		faction = TESFaction.forID(factionID);
		try {
			factionNBT = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error reading faction data");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(faction.ordinal());
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(factionNBT);
		} catch (IOException e) {
			FMLLog.severe("Error writing faction data");
			e.printStackTrace();
		}
	}

	public static class Handler implements IMessageHandler<TESPacketFactionData, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFactionData packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				TESPlayerData pd = TESLevelData.getData(entityplayer);
				TESFaction faction = packet.faction;
				if (faction != null) {
					TESFactionData factionData = pd.getFactionData(faction);
					factionData.load(packet.factionNBT);
				}
			}
			return null;
		}
	}
}