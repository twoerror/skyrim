package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketAchievement implements IMessage {
	private TESAchievement achievement;
	private boolean display;

	@SuppressWarnings("unused")
	public TESPacketAchievement() {
	}

	public TESPacketAchievement(TESAchievement ach, boolean disp) {
		achievement = ach;
		display = disp;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte catID = data.readByte();
		short achID = data.readShort();
		TESAchievement.Category cat = TESAchievement.Category.values()[catID];
		achievement = TESAchievement.achievementForCategoryAndID(cat, achID);
		display = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(achievement.getCategory().ordinal());
		data.writeShort(achievement.getId());
		data.writeBoolean(display);
	}

	public static class Handler implements IMessageHandler<TESPacketAchievement, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAchievement packet, MessageContext context) {
			TESAchievement achievement = packet.achievement;
			if (achievement != null) {
				if (!TES.proxy.isSingleplayer()) {
					EntityPlayer entityplayer = TES.proxy.getClientPlayer();
					TESLevelData.getData(entityplayer).addAchievement(achievement);
				}
				if (packet.display) {
					TES.proxy.queueAchievement(achievement);
				}
			}
			return null;
		}
	}
}