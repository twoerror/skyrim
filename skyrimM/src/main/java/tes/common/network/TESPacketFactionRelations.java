package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.faction.TESFaction;
import tes.common.faction.TESFactionRelations;
import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

public class TESPacketFactionRelations implements IMessage {
	private Type packetType;
	private Map<TESFactionRelations.FactionPair, TESFactionRelations.Relation> fullMap;
	private TESFactionRelations.FactionPair singleKey;
	private TESFactionRelations.Relation singleRelation;

	public static TESPacketFactionRelations fullMap(Map<TESFactionRelations.FactionPair, TESFactionRelations.Relation> map) {
		TESPacketFactionRelations pkt = new TESPacketFactionRelations();
		pkt.packetType = Type.FULL_MAP;
		pkt.fullMap = map;
		return pkt;
	}

	public static TESPacketFactionRelations oneEntry(TESFactionRelations.FactionPair pair, TESFactionRelations.Relation rel) {
		TESPacketFactionRelations pkt = new TESPacketFactionRelations();
		pkt.packetType = Type.ONE_ENTRY;
		pkt.singleKey = pair;
		pkt.singleRelation = rel;
		return pkt;
	}

	public static TESPacketFactionRelations reset() {
		TESPacketFactionRelations pkt = new TESPacketFactionRelations();
		pkt.packetType = Type.RESET;
		return pkt;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte typeID = data.readByte();
		packetType = Type.forID(typeID);
		if (packetType == Type.FULL_MAP) {
			fullMap = new HashMap<>();
			byte fac1ID;
			while ((fac1ID = data.readByte()) >= 0) {
				byte fac2ID = data.readByte();
				byte relID = data.readByte();
				TESFaction f1 = TESFaction.forID(fac1ID);
				TESFaction f2 = TESFaction.forID(fac2ID);
				TESFactionRelations.FactionPair key = new TESFactionRelations.FactionPair(f1, f2);
				TESFactionRelations.Relation rel = TESFactionRelations.Relation.forID(relID);
				fullMap.put(key, rel);
			}
		} else if (packetType != Type.RESET && packetType == Type.ONE_ENTRY) {
			byte fac1ID = data.readByte();
			byte fac2ID = data.readByte();
			byte relID = data.readByte();
			TESFaction f1 = TESFaction.forID(fac1ID);
			TESFaction f2 = TESFaction.forID(fac2ID);
			singleKey = new TESFactionRelations.FactionPair(f1, f2);
			singleRelation = TESFactionRelations.Relation.forID(relID);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(packetType.ordinal());
		if (packetType == Type.FULL_MAP) {
			for (Map.Entry<TESFactionRelations.FactionPair, TESFactionRelations.Relation> e : fullMap.entrySet()) {
				TESFactionRelations.FactionPair key = e.getKey();
				TESFactionRelations.Relation rel = e.getValue();
				data.writeByte(key.getLeft().ordinal());
				data.writeByte(key.getRight().ordinal());
				data.writeByte(rel.ordinal());
			}
			data.writeByte(-1);
		} else if (packetType != Type.RESET && packetType == Type.ONE_ENTRY) {
			data.writeByte(singleKey.getLeft().ordinal());
			data.writeByte(singleKey.getRight().ordinal());
			data.writeByte(singleRelation.ordinal());
		}
	}

	public enum Type {
		FULL_MAP, RESET, ONE_ENTRY;

		private static Type forID(int id) {
			for (Type t : values()) {
				if (t.ordinal() != id) {
					continue;
				}
				return t;
			}
			return null;
		}
	}

	public static class Handler implements IMessageHandler<TESPacketFactionRelations, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFactionRelations packet, MessageContext context) {
			if (!TES.proxy.isSingleplayer()) {
				Type t = packet.packetType;
				if (t == Type.FULL_MAP) {
					TESFactionRelations.resetAllRelations();
					for (Map.Entry<TESFactionRelations.FactionPair, TESFactionRelations.Relation> e : packet.fullMap.entrySet()) {
						TESFactionRelations.FactionPair key = e.getKey();
						TESFactionRelations.Relation rel = e.getValue();
						TESFactionRelations.overrideRelations(key.getLeft(), key.getRight(), rel);
					}
				} else if (t == Type.RESET) {
					TESFactionRelations.resetAllRelations();
				} else if (t == Type.ONE_ENTRY) {
					TESFactionRelations.FactionPair key = packet.singleKey;
					TESFactionRelations.Relation rel = packet.singleRelation;
					TESFactionRelations.overrideRelations(key.getLeft(), key.getRight(), rel);
				}
			}
			return null;
		}
	}
}