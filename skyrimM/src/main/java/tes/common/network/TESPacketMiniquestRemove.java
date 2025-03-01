package tes.common.network;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.quest.TESMiniQuest;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TESPacketMiniquestRemove implements IMessage {
	private UUID questUUID;
	private boolean wasCompleted;
	private boolean addToCompleted;

	@SuppressWarnings("unused")
	public TESPacketMiniquestRemove() {
	}

	public TESPacketMiniquestRemove(TESMiniQuest quest, boolean wc, boolean atc) {
		questUUID = quest.getQuestUUID();
		wasCompleted = wc;
		addToCompleted = atc;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		questUUID = new UUID(data.readLong(), data.readLong());
		wasCompleted = data.readBoolean();
		addToCompleted = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(questUUID.getMostSignificantBits());
		data.writeLong(questUUID.getLeastSignificantBits());
		data.writeBoolean(wasCompleted);
		data.writeBoolean(addToCompleted);
	}

	public static class Handler implements IMessageHandler<TESPacketMiniquestRemove, IMessage> {
		@Override
		public IMessage onMessage(TESPacketMiniquestRemove packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				EntityPlayer entityplayer = TES.proxy.getClientPlayer();
				TESPlayerData pd = TESLevelData.getData(entityplayer);
				TESMiniQuest removeQuest = pd.getMiniQuestForID(packet.questUUID, packet.wasCompleted);
				if (removeQuest != null) {
					if (packet.addToCompleted) {
						pd.completeMiniQuest(removeQuest);
					} else {
						pd.removeMiniQuest(removeQuest, packet.wasCompleted);
					}
				} else {
					FMLLog.warning("Tried to remove a TES miniquest that doesn't exist");
				}
			}
			return null;
		}
	}
}