package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESDate;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class TESPacketDate implements IMessage {
	private NBTTagCompound dateData;
	private boolean doUpdate;

	@SuppressWarnings("unused")
	public TESPacketDate() {
	}

	public TESPacketDate(NBTTagCompound nbt, boolean flag) {
		dateData = nbt;
		doUpdate = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		try {
			dateData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error reading TES date");
			e.printStackTrace();
		}
		doUpdate = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(dateData);
		} catch (IOException e) {
			FMLLog.severe("Error writing TES date");
			e.printStackTrace();
		}
		data.writeBoolean(doUpdate);
	}

	public static class Handler implements IMessageHandler<TESPacketDate, IMessage> {
		@Override
		public IMessage onMessage(TESPacketDate packet, MessageContext context) {
			TESDate.loadDates(packet.dateData);
			if (packet.doUpdate) {
				TES.proxy.displayNewDate();
			}
			return null;
		}
	}
}