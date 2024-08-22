package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketAchievementRemove implements IMessage {
	private TESAchievement achievement;

	@SuppressWarnings("unused")
	public TESPacketAchievementRemove() {
	}

	public TESPacketAchievementRemove(TESAchievement ach) {
		achievement = ach;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte catID = data.readByte();
		short achID = data.readShort();
		TESAchievement.Category cat = TESAchievement.Category.values()[catID];
		achievement = TESAchievement.achievementForCategoryAndID(cat, achID);
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(achievement.getCategory().ordinal());
		data.writeShort(achievement.getId());
	}

	public static class Handler implements IMessageHandler<TESPacketAchievementRemove, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAchievementRemove packet, MessageContext context) {
			TESAchievement achievement = packet.achievement;
			if (achievement != null && !TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				TESLevelData.getData(entityplayer).removeAchievement(achievement);
			}
			return null;
		}
	}
}