package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.database.TESCapes;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketSelectCape implements IMessage {
	private TESCapes cape;

	@SuppressWarnings("unused")
	public TESPacketSelectCape() {
	}

	public TESPacketSelectCape(TESCapes s) {
		cape = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte capeID = data.readByte();
		if (capeID == -1) {
			cape = null;
		} else {
			byte capeTypeID = data.readByte();
			if (capeTypeID < 0 || capeTypeID >= TESCapes.CapeType.values().length) {
				FMLLog.severe("Failed to update TES cape on server side: There is no capetype with ID " + capeTypeID);
			} else {
				TESCapes.CapeType capeType = TESCapes.CapeType.values()[capeTypeID];
				if (capeID < 0 || capeID >= capeType.getCapes().size()) {
					FMLLog.severe("Failed to update TES cape on server side: There is no cape with ID " + capeID + " for capetype " + capeTypeID);
				} else {
					cape = capeType.getCapes().get(capeID);
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		if (cape == null) {
			data.writeByte(-1);
		} else {
			data.writeByte(cape.getCapeID());
			data.writeByte(cape.getCapeType().ordinal());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketSelectCape, IMessage> {
		@Override
		public IMessage onMessage(TESPacketSelectCape packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESCapes cape = packet.cape;
			if (cape == null || cape.canPlayerWear(entityplayer)) {
				TESLevelData.getData(entityplayer).setCape(cape);
				TESLevelData.sendCapeToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
			} else {
				FMLLog.severe("Failed to update TES cape on server side: Player " + entityplayer.getCommandSenderName() + " cannot wear cape " + cape.name());
			}
			return null;
		}
	}
}