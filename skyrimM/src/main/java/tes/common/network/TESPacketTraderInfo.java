package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.inventory.TESContainerTrade;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class TESPacketTraderInfo implements IMessage {
	private NBTTagCompound traderData;

	@SuppressWarnings("unused")
	public TESPacketTraderInfo() {
	}

	public TESPacketTraderInfo(NBTTagCompound nbt) {
		traderData = nbt;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		try {
			traderData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error reading trader data");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(traderData);
		} catch (IOException e) {
			FMLLog.severe("Error writing trader data");
			e.printStackTrace();
		}
	}

	public NBTTagCompound getTraderData() {
		return traderData;
	}

	public static class Handler implements IMessageHandler<TESPacketTraderInfo, IMessage> {
		@Override
		public IMessage onMessage(TESPacketTraderInfo packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerTrade) {
				TESContainerTrade containerTrade = (TESContainerTrade) container;
				containerTrade.getTheTraderNPC().getTraderInfo().receiveClientPacket(packet);
			}
			return null;
		}
	}
}