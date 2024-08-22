package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESDimension;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.faction.TESFaction;
import tes.common.quest.TESMiniQuestEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.EnumMap;
import java.util.Map;

public class TESPacketClientInfo implements IMessage {
	private TESFaction viewingFaction;
	private Map<TESDimension.DimensionRegion, TESFaction> changedRegionMap;
	private boolean showWP;
	private boolean showCWP;
	private boolean showHiddenSWP;

	@SuppressWarnings("unused")
	public TESPacketClientInfo() {
	}

	public TESPacketClientInfo(TESFaction f, Map<TESDimension.DimensionRegion, TESFaction> crMap, boolean w, boolean cw, boolean h) {
		viewingFaction = f;
		changedRegionMap = crMap;
		showWP = w;
		showCWP = cw;
		showHiddenSWP = h;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte factionID = data.readByte();
		if (factionID >= 0) {
			viewingFaction = TESFaction.forID(factionID);
		}
		int changedRegionsSize = data.readByte();
		if (changedRegionsSize > 0) {
			changedRegionMap = new EnumMap<>(TESDimension.DimensionRegion.class);
			for (int l = 0; l < changedRegionsSize; ++l) {
				TESDimension.DimensionRegion reg = TESDimension.DimensionRegion.forID(data.readByte());
				TESFaction fac = TESFaction.forID(data.readByte());
				changedRegionMap.put(reg, fac);
			}
		}
		showWP = data.readBoolean();
		showCWP = data.readBoolean();
		showHiddenSWP = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		if (viewingFaction == null) {
			data.writeByte(-1);
		} else {
			data.writeByte(viewingFaction.ordinal());
		}
		int changedRegionsSize = changedRegionMap != null ? changedRegionMap.size() : 0;
		data.writeByte(changedRegionsSize);
		if (changedRegionsSize > 0) {
			for (Map.Entry<TESDimension.DimensionRegion, TESFaction> e : changedRegionMap.entrySet()) {
				TESDimension.DimensionRegion reg = e.getKey();
				TESFaction fac = e.getValue();
				data.writeByte(reg.ordinal());
				data.writeByte(fac.ordinal());
			}
		}
		data.writeBoolean(showWP);
		data.writeBoolean(showCWP);
		data.writeBoolean(showHiddenSWP);
	}

	public static class Handler implements IMessageHandler<TESPacketClientInfo, IMessage> {
		@Override
		public IMessage onMessage(TESPacketClientInfo packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (packet.viewingFaction != null) {
				TESFaction prevFac = pd.getViewingFaction();
				TESFaction newFac = packet.viewingFaction;
				pd.setViewingFaction(newFac);
				if (prevFac != newFac && prevFac.getFactionRegion() == newFac.getFactionRegion()) {
					pd.distributeMQEvent(new TESMiniQuestEvent.CycleAlignment());
				}
				if (prevFac.getFactionRegion() != newFac.getFactionRegion()) {
					pd.distributeMQEvent(new TESMiniQuestEvent.CycleAlignmentRegion());
				}
			}
			Map<TESDimension.DimensionRegion, TESFaction> changedRegionMap = packet.changedRegionMap;
			if (changedRegionMap != null) {
				for (Map.Entry<TESDimension.DimensionRegion, TESFaction> entry : changedRegionMap.entrySet()) {
					TESFaction fac = entry.getValue();
					pd.setRegionLastViewedFaction(entry.getKey(), fac);
				}
			}
			pd.setShowWaypoints(packet.showWP);
			pd.setShowCustomWaypoints(packet.showCWP);
			pd.setShowHiddenSharedWaypoints(packet.showHiddenSWP);
			return null;
		}
	}
}