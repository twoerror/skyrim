package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class TESPacketRenameCWP implements IMessage {
	private int wpID;
	private String name;

	@SuppressWarnings("unused")
	public TESPacketRenameCWP() {
	}

	public TESPacketRenameCWP(TESAbstractWaypoint wp, String s) {
		wpID = wp.getID();
		name = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		wpID = data.readInt();
		short length = data.readShort();
		name = data.readBytes(length).toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(wpID);
		byte[] nameBytes = name.getBytes(Charsets.UTF_8);
		data.writeShort(nameBytes.length);
		data.writeBytes(nameBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketRenameCWP, IMessage> {
		@Override
		public IMessage onMessage(TESPacketRenameCWP packet, MessageContext context) {
			String wpName;
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			TESCustomWaypoint cwp = pd.getCustomWaypointByID(packet.wpID);
			if (cwp != null && (wpName = TESCustomWaypoint.validateCustomName(packet.name)) != null) {
				pd.renameCustomWaypoint(cwp, wpName);
			}
			return null;
		}
	}
}