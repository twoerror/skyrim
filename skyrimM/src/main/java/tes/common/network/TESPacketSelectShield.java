package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.database.TESShields;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketSelectShield implements IMessage {
	private TESShields shield;

	@SuppressWarnings("unused")
	public TESPacketSelectShield() {
	}

	public TESPacketSelectShield(TESShields s) {
		shield = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte shieldID = data.readByte();
		if (shieldID == -1) {
			shield = null;
		} else {
			byte shieldTypeID = data.readByte();
			if (shieldTypeID < 0 || shieldTypeID >= TESShields.ShieldType.values().length) {
				FMLLog.severe("Failed to update TES shield on server side: There is no shieldtype with ID " + shieldTypeID);
			} else {
				TESShields.ShieldType shieldType = TESShields.ShieldType.values()[shieldTypeID];
				if (shieldID < 0 || shieldID >= shieldType.getShields().size()) {
					FMLLog.severe("Failed to update TES shield on server side: There is no shield with ID " + shieldID + " for shieldtype " + shieldTypeID);
				} else {
					shield = shieldType.getShields().get(shieldID);
				}
			}
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		if (shield == null) {
			data.writeByte(-1);
		} else {
			data.writeByte(shield.getShieldID());
			data.writeByte(shield.getShieldType().ordinal());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketSelectShield, IMessage> {
		@Override
		public IMessage onMessage(TESPacketSelectShield packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESShields shield = packet.shield;
			if (shield == null || shield.canPlayerWear(entityplayer)) {
				TESLevelData.getData(entityplayer).setShield(shield);
				TESLevelData.sendShieldToAllPlayersInWorld(entityplayer, entityplayer.worldObj);
			} else {
				FMLLog.severe("Failed to update TES shield on server side: Player " + entityplayer.getCommandSenderName() + " cannot wear shield " + shield.name());
			}
			return null;
		}
	}
}