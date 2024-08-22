package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESCustomWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

public class TESPacketRenameCWPClient implements IMessage {
	private int cwpID;
	private String name;
	private UUID sharingPlayer;

	@SuppressWarnings("unused")
	public TESPacketRenameCWPClient() {
	}

	public TESPacketRenameCWPClient(int id, String s) {
		cwpID = id;
		name = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		cwpID = data.readInt();
		short length = data.readShort();
		name = data.readBytes(length).toString(Charsets.UTF_8);
		boolean shared = data.readBoolean();
		if (shared) {
			sharingPlayer = new UUID(data.readLong(), data.readLong());
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(cwpID);
		byte[] nameBytes = name.getBytes(Charsets.UTF_8);
		data.writeShort(nameBytes.length);
		data.writeBytes(nameBytes);
		boolean shared = sharingPlayer != null;
		data.writeBoolean(shared);
		if (shared) {
			data.writeLong(sharingPlayer.getMostSignificantBits());
			data.writeLong(sharingPlayer.getLeastSignificantBits());
		}
	}

	public TESPacketRenameCWPClient setSharingPlayer(UUID player) {
		sharingPlayer = player;
		return this;
	}

	public static class Handler implements IMessageHandler<TESPacketRenameCWPClient, IMessage> {
		@Override
		public IMessage onMessage(TESPacketRenameCWPClient packet, MessageContext context) {
			TESCustomWaypoint cwp;
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			TESPlayerData pd = TESLevelData.getData(entityplayer);
			if (packet.sharingPlayer != null) {
				TESCustomWaypoint cwp2;
				if (!TES.proxy.isSingleplayer() && (cwp2 = pd.getSharedCustomWaypointByID(packet.sharingPlayer, packet.cwpID)) != null) {
					pd.renameSharedCustomWaypoint(cwp2, packet.name);
				}
			} else if (!TES.proxy.isSingleplayer() && (cwp = pd.getCustomWaypointByID(packet.cwpID)) != null) {
				TESPlayerData.renameCustomWaypointClient(cwp, packet.name);
			}
			return null;
		}
	}
}