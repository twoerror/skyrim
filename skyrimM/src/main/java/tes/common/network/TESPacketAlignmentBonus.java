package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.faction.TESAlignmentBonusMap;
import tes.common.faction.TESAlignmentValues;
import tes.common.faction.TESFaction;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.StatCollector;

import java.util.Map;

public class TESPacketAlignmentBonus implements IMessage {
	private TESAlignmentBonusMap factionBonusMap = new TESAlignmentBonusMap();
	private TESFaction mainFaction;
	private String name;

	private float prevMainAlignment;
	private float conquestBonus;
	private double posX;
	private double posY;
	private double posZ;
	private boolean needsTranslation;
	private boolean isKill;
	private boolean isHiredKill;

	@SuppressWarnings("unused")
	public TESPacketAlignmentBonus() {
	}

	public TESPacketAlignmentBonus(TESFaction f, float pre, TESAlignmentBonusMap fMap, float conqBonus, double x, double y, double z, TESAlignmentValues.AlignmentBonus source) {
		mainFaction = f;
		prevMainAlignment = pre;
		factionBonusMap = fMap;
		conquestBonus = conqBonus;
		posX = x;
		posY = y;
		posZ = z;
		name = source.getName();
		needsTranslation = source.isNeedsTranslation();
		isKill = source.isKill();
		isHiredKill = source.isKillByHiredUnit();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		mainFaction = TESFaction.forID(data.readByte());
		prevMainAlignment = data.readFloat();
		byte factionID;
		while ((factionID = data.readByte()) >= 0) {
			TESFaction faction = TESFaction.forID(factionID);
			float bonus = data.readFloat();
			factionBonusMap.put(faction, bonus);
		}
		conquestBonus = data.readFloat();
		posX = data.readDouble();
		posY = data.readDouble();
		posZ = data.readDouble();
		short length = data.readShort();
		name = data.readBytes(length).toString(Charsets.UTF_8);
		needsTranslation = data.readBoolean();
		isKill = data.readBoolean();
		isHiredKill = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(mainFaction.ordinal());
		data.writeFloat(prevMainAlignment);
		if (!factionBonusMap.isEmpty()) {
			for (Map.Entry<TESFaction, Float> e : factionBonusMap.entrySet()) {
				TESFaction faction = e.getKey();
				float bonus = e.getValue();
				data.writeByte(faction.ordinal());
				data.writeFloat(bonus);
			}
		}
		data.writeByte(-1);
		data.writeFloat(conquestBonus);
		data.writeDouble(posX);
		data.writeDouble(posY);
		data.writeDouble(posZ);
		byte[] nameData = name.getBytes(Charsets.UTF_8);
		data.writeShort(nameData.length);
		data.writeBytes(nameData);
		data.writeBoolean(needsTranslation);
		data.writeBoolean(isKill);
		data.writeBoolean(isHiredKill);
	}

	public static class Handler implements IMessageHandler<TESPacketAlignmentBonus, IMessage> {
		@Override
		public IMessage onMessage(TESPacketAlignmentBonus packet, MessageContext context) {
			String name = packet.name;
			if (packet.needsTranslation) {
				name = StatCollector.translateToLocal(name);
			}
			TES.proxy.spawnAlignmentBonus(packet.mainFaction, packet.prevMainAlignment, packet.factionBonusMap, name, packet.conquestBonus, packet.posX, packet.posY, packet.posZ);
			return null;
		}
	}
}