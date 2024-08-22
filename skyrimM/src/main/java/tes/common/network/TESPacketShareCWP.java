package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketShareCWP implements IMessage {
	private int wpID;
	private String fsName;
	private boolean adding;

	@SuppressWarnings("unused")
	public TESPacketShareCWP() {
	}

	public TESPacketShareCWP(TESAbstractWaypoint wp, String s, boolean add) {
		wpID = wp.getID();
		fsName = s;
		adding = add;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		wpID = data.readInt();
		short length = data.readShort();
		fsName = data.readBytes(length).toString(Charsets.UTF_8);
		adding = data.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(wpID);
		byte[] nameBytes = fsName.getBytes(Charsets.UTF_8);
		data.writeShort(nameBytes.length);
		data.writeBytes(nameBytes);
		data.writeBoolean(adding);
	}

	public static class Handler implements IMessageHandler<TESPacketShareCWP, IMessage> {
		@Override
		public IMessage onMessage(TESPacketShareCWP packet, MessageContext context) {
			TESFellowship fellowship;
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESCustomWaypoint cwp = pd.getCustomWaypointByID(packet.wpID);
			if (cwp != null && (fellowship = pd.getFellowshipByName(packet.fsName)) != null) {
				if (packet.adding) {
					pd.customWaypointAddSharedFellowship(cwp, fellowship);
				} else {
					pd.customWaypointRemoveSharedFellowship(cwp, fellowship);
				}
			}
			return null;
		}
	}
}