package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.database.TESShields;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class TESPacketShield implements IMessage {
	private UUID player;
	private TESShields shield;

	@SuppressWarnings("unused")
	public TESPacketShield() {
	}

	public TESPacketShield(UUID uuid) {
		player = uuid;
		TESPlayerData pd = TESLevelData.getData(player);
		shield = pd.getShield();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		player = new UUID(data.readLong(), data.readLong());
		boolean hasShield = data.readBoolean();
		if (hasShield) {
			byte shieldID = data.readByte();
			byte shieldTypeID = data.readByte();
			if (shieldTypeID < 0 || shieldTypeID >= TESShields.ShieldType.values().length) {
				FMLLog.severe("Failed to update TES shield on client side: There is no shieldtype with ID " + shieldTypeID);
			} else {
				TESShields.ShieldType shieldType = TESShields.ShieldType.values()[shieldTypeID];
				if (shieldID < 0 || shieldID >= shieldType.getShields().size()) {
					FMLLog.severe("Failed to update TES shield on client side: There is no shield with ID " + shieldID + " for shieldtype " + shieldTypeID);
				} else {
					shield = shieldType.getShields().get(shieldID);
				}
			}
		} else {
			shield = null;
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(player.getMostSignificantBits());
		data.writeLong(player.getLeastSignificantBits());
		boolean hasShield = shield != null;
		data.writeBoolean(hasShield);
		if (hasShield) {
			data.writeByte(shield.getShieldID());
			data.writeByte(shield.getShieldType().ordinal());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketShield, IMessage> {
		@Override
		public IMessage onMessage(TESPacketShield packet, MessageContext context) {
			TESPlayerData pd = TESLevelData.getData(packet.player);
			pd.setShield(packet.shield);
			return null;
		}
	}
}