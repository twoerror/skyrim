package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESCapes;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class TESPacketCape implements IMessage {
	private UUID player;
	private TESCapes cape;

	@SuppressWarnings("unused")
	public TESPacketCape() {
	}

	public TESPacketCape(UUID uuid) {
		player = uuid;
		TESPlayerData pd = TESLevelData.getData(player);
		cape = pd.getCape();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		player = new UUID(data.readLong(), data.readLong());
		boolean hasCape = data.readBoolean();
		if (hasCape) {
			byte capeID = data.readByte();
			byte capeTypeID = data.readByte();
			if (capeTypeID < 0 || capeTypeID >= TESCapes.CapeType.values().length) {
				FMLLog.severe("Failed to update TES cape on client side: There is no capetype with ID " + capeTypeID);
			} else {
				TESCapes.CapeType capeType = TESCapes.CapeType.values()[capeTypeID];
				if (capeID < 0 || capeID >= capeType.getCapes().size()) {
					FMLLog.severe("Failed to update TES cape on client side: There is no cape with ID " + capeID + " for capetype " + capeTypeID);
				} else {
					cape = capeType.getCapes().get(capeID);
				}
			}
		} else {
			cape = null;
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(player.getMostSignificantBits());
		data.writeLong(player.getLeastSignificantBits());
		boolean hasCape = cape != null;
		data.writeBoolean(hasCape);
		if (hasCape) {
			data.writeByte(cape.getCapeID());
			data.writeByte(cape.getCapeType().ordinal());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketCape, IMessage> {
		@Override
		public IMessage onMessage(TESPacketCape packet, MessageContext context) {
			TESPlayerData pd = TESLevelData.getData(packet.player);
			pd.setCape(packet.cape);
			return null;
		}
	}
}