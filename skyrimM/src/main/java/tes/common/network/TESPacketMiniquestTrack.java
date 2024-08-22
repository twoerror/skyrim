package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.quest.TESMiniQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class TESPacketMiniquestTrack implements IMessage {
	private UUID questID;

	@SuppressWarnings("unused")
	public TESPacketMiniquestTrack() {
	}

	public TESPacketMiniquestTrack(TESMiniQuest quest) {
		questID = quest == null ? null : quest.getQuestUUID();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		boolean hasQuest = data.readBoolean();
		questID = hasQuest ? new UUID(data.readLong(), data.readLong()) : null;
	}

	@Override
	public void toBytes(ByteBuf data) {
		boolean hasQuest = questID != null;
		data.writeBoolean(hasQuest);
		if (hasQuest) {
			data.writeLong(questID.getMostSignificantBits());
			data.writeLong(questID.getLeastSignificantBits());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketMiniquestTrack, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMiniquestTrack packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (packet.questID == null) {
				pd.setTrackingMiniQuestID(null);
			} else {
				pd.setTrackingMiniQuestID(packet.questID);
			}
			return null;
		}
	}
}