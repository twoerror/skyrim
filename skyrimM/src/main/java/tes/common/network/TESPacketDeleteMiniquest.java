package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.quest.TESMiniQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class TESPacketDeleteMiniquest implements IMessage {
	private UUID questUUID;
	private boolean completed;

	@SuppressWarnings("unused")
	public TESPacketDeleteMiniquest() {
	}

	public TESPacketDeleteMiniquest(TESMiniQuest quest) {
		questUUID = quest.getQuestUUID();
		completed = quest.isCompleted();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		questUUID = new UUID(data.readLong(), data.readLong());
		completed = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(questUUID.getMostSignificantBits());
		data.writeLong(questUUID.getLeastSignificantBits());
		data.writeBoolean(completed);
	}

	public static class Handler implements IMessageHandler<TESPacketDeleteMiniquest, IMessage> {
		@Override
		public IMessage onMessage(TESPacketDeleteMiniquest packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESMiniQuest removeQuest = pd.getMiniQuestForID(packet.questUUID, packet.completed);
			if (removeQuest != null) {
				pd.removeMiniQuest(removeQuest, packet.completed);
			} else {
				FMLLog.warning("Tried to remove a TES miniquest that doesn't exist");
			}
			return null;
		}
	}
}