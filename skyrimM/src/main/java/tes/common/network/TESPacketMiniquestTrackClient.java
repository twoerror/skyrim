package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TESPacketMiniquestTrackClient implements IMessage {
	private UUID questID;

	@SuppressWarnings("unused")
	public TESPacketMiniquestTrackClient() {
	}

	public TESPacketMiniquestTrackClient(UUID uuid) {
		questID = uuid;
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

	public static class Handler implements IMessageHandler<TESPacketMiniquestTrackClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMiniquestTrackClient packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				TESPlayerData pd = TESLevelData.getData(entityplayer);
				pd.setTrackingMiniQuestID(packet.questID);
			}
			return null;
		}
	}
}