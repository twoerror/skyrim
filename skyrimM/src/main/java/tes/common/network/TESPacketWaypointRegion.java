package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class TESPacketWaypointRegion implements IMessage {
	private TESWaypoint.Region region;
	private boolean unlocking;

	@SuppressWarnings("unused")
	public TESPacketWaypointRegion() {
	}

	public TESPacketWaypointRegion(TESWaypoint.Region r, boolean flag) {
		region = r;
		unlocking = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		region = TESWaypoint.regionForID(data.readByte());
		unlocking = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeByte(region.ordinal());
		data.writeBoolean(unlocking);
	}

	public static class Handler implements IMessageHandler<TESPacketWaypointRegion, IMessage> {
		@Override
		public IMessage onMessage(TESPacketWaypointRegion packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESWaypoint.Region region = packet.region;
			if (region != null) {
				if (packet.unlocking) {
					pd.unlockFTRegion(region);
				} else {
					pd.lockFTRegion(region);
				}
			}
			return null;
		}
	}
}