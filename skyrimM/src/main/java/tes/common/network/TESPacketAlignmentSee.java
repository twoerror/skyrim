package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;

import java.util.EnumMap;
import java.util.Map;

public class TESPacketAlignmentSee implements IMessage {
	private final Map<TESFaction, Float> alignmentMap = new EnumMap<>(TESFaction.class);

	private String username;

	@SuppressWarnings("unused")
	public TESPacketAlignmentSee() {
	}

	public TESPacketAlignmentSee(String name, TESPlayerData pd) {
		username = name;
		for (TESFaction f : TESFaction.getPlayableAlignmentFactions()) {
			float al = pd.getAlignment(f);
			alignmentMap.put(f, al);
		}
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte length = data.readByte();
		ByteBuf nameBytes = data.readBytes(length);
		username = nameBytes.toString(Charsets.UTF_8);
		byte factionID;
		while ((factionID = data.readByte()) >= 0) {
			TESFaction f = TESFaction.forID(factionID);
			float alignment = data.readFloat();
			alignmentMap.put(f, alignment);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		byte[] nameBytes = username.getBytes(Charsets.UTF_8);
		data.writeByte(nameBytes.length);
		data.writeBytes(nameBytes);
		for (Map.Entry<TESFaction, Float> entry : alignmentMap.entrySet()) {
			TESFaction f = entry.getKey();
			float alignment = entry.getValue();
			data.writeByte(f.ordinal());
			data.writeFloat(alignment);
		}
		data.writeByte(-1);
	}

	public static class Handler implements IMessageHandler<TESPacketAlignmentSee, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAlignmentSee packet, MessageContext context) {
			TES.proxy.displayAlignmentSee(packet.username, packet.alignmentMap);
			return null;
		}
	}
}