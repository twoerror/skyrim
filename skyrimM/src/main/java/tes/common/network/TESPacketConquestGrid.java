package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.faction.TESFaction;
import tes.common.world.map.TESConquestZone;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TESPacketConquestGrid implements IMessage {
	private TESFaction conqFac;
	private List<TESConquestZone> allZones;
	private long worldTime;

	@SuppressWarnings("unused")
	public TESPacketConquestGrid() {
	}

	public TESPacketConquestGrid(TESFaction fac, Collection<TESConquestZone> grid, World world) {
		conqFac = fac;
		allZones = new ArrayList<>(grid);
		worldTime = world.getTotalWorldTime();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		byte facID = data.readByte();
		conqFac = TESFaction.forID(facID);
		allZones = new ArrayList<>();
		short gridX;
		short gridZ;
		float str;
		while ((gridX = data.readShort()) != -15000) {
			gridZ = data.readShort();
			long time = data.readLong();
			str = data.readFloat();
			TESConquestZone zone = new TESConquestZone(gridX, gridZ);
			zone.setClientSide();
			zone.setLastChangeTime(time);
			zone.setConquestStrengthRaw(conqFac, str);
			allZones.add(zone);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		int facID = conqFac.ordinal();
		data.writeByte(facID);
		for (TESConquestZone zone : allZones) {
			float str = zone.getConquestStrength(conqFac, worldTime);
			if (str <= 0.0f) {
				continue;
			}
			float strRaw = zone.getConquestStrengthRaw(conqFac);
			data.writeShort(zone.getGridX());
			data.writeShort(zone.getGridZ());
			data.writeLong(zone.getLastChangeTime());
			data.writeFloat(strRaw);
		}
		data.writeShort(-15000);
	}

	public static class Handler implements IMessageHandler<TESPacketConquestGrid, IMessage> {
		@Override
		public IMessage onMessage(TESPacketConquestGrid packet, MessageContext context) {
			TES.proxy.receiveConquestGrid(packet.conqFac, packet.allZones);
			return null;
		}
	}
}