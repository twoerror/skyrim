package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;

public class TESPacketLoginPlayerData implements IMessage {
	private NBTTagCompound playerData;

	@SuppressWarnings("unused")
	public TESPacketLoginPlayerData() {
	}

	public TESPacketLoginPlayerData(NBTTagCompound nbt) {
		playerData = nbt;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		try {
			playerData = new PacketBuffer(data).readNBTTagCompoundFromBuffer();
		} catch (IOException e) {
			FMLLog.severe("Error reading TES login player data");
			e.printStackTrace();
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		try {
			new PacketBuffer(data).writeNBTTagCompoundToBuffer(playerData);
		} catch (IOException e) {
			FMLLog.severe("Error writing TES login player data");
			e.printStackTrace();
		}
	}

	public static class Handler implements IMessageHandler<TESPacketLoginPlayerData, IMessage> {
		@Override
		public IMessage onMessage(TESPacketLoginPlayerData packet, MessageContext context) {
			NBTTagCompound nbt = packet.playerData;
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (!TES.proxy.isSingleplayer()) {
				pd.load(nbt);
			}
			TES.proxy.setWaypointModes(pd.showWaypoints(), pd.showCustomWaypoints(), pd.showHiddenSharedWaypoints());
			return null;
		}
	}
}