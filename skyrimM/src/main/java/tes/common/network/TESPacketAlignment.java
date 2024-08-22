package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class TESPacketAlignment implements IMessage {
	private final Map<TESFaction, Float> alignmentMap = new EnumMap<>(TESFaction.class);

	private UUID player;
	private boolean hideAlignment;

	@SuppressWarnings("unused")
	public TESPacketAlignment() {
	}

	public TESPacketAlignment(UUID uuid) {
		player = uuid;
		TESPlayerData pd = TESLevelData.getData(player);
		for (TESFaction f : TESFaction.values()) {
			float al = pd.getAlignment(f);
			alignmentMap.put(f, al);
		}
		hideAlignment = pd.getHideAlignment();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		player = new UUID(data.readLong(), data.readLong());
		byte factionID;
		while ((factionID = data.readByte()) >= 0) {
			TESFaction f = TESFaction.forID(factionID);
			float alignment = data.readFloat();
			alignmentMap.put(f, alignment);
		}
		hideAlignment = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(player.getMostSignificantBits());
		data.writeLong(player.getLeastSignificantBits());
		for (Map.Entry<TESFaction, Float> entry : alignmentMap.entrySet()) {
			TESFaction f = entry.getKey();
			float alignment = entry.getValue();
			data.writeByte(f.ordinal());
			data.writeFloat(alignment);
		}
		data.writeByte(-1);
		data.writeBoolean(hideAlignment);
	}

	public static class Handler implements IMessageHandler<TESPacketAlignment, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAlignment packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				TESPlayerData pd = TESLevelData.getData(packet.player);
				for (Map.Entry<TESFaction, Float> entry : packet.alignmentMap.entrySet()) {
					TESFaction f = entry.getKey();
					float alignment = entry.getValue();
					pd.setAlignment(f, alignment);
				}
				pd.setHideAlignment(packet.hideAlignment);
			}
			return null;
		}
	}
}