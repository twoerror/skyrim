package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.world.map.TESAbstractWaypoint;
import tes.common.world.map.TESCustomWaypoint;
import tes.common.world.map.TESWaypoint;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentTranslation;

import java.util.UUID;

public class TESPacketFastTravel implements IMessage {
	private boolean isCustom;
	private int wpID;
	private UUID sharingPlayer;

	@SuppressWarnings("unused")
	public TESPacketFastTravel() {
	}

	public TESPacketFastTravel(TESAbstractWaypoint wp) {
		isCustom = wp instanceof TESCustomWaypoint;
		wpID = wp.getID();
		if (wp instanceof TESCustomWaypoint) {
			sharingPlayer = ((TESCustomWaypoint) wp).getSharingPlayerID();
		}
	}

	@Override
	public void fromBytes(ByteBuf data) {
		isCustom = data.readBoolean();
		wpID = data.readInt();
		boolean shared = data.readBoolean();
		if (shared) {
			sharingPlayer = new UUID(data.readLong(), data.readLong());
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeBoolean(isCustom);
		data.writeInt(wpID);
		boolean shared = sharingPlayer != null;
		data.writeBoolean(shared);
		if (shared) {
			data.writeLong(sharingPlayer.getMostSignificantBits());
			data.writeLong(sharingPlayer.getLeastSignificantBits());
		}
	}

	public static class Handler implements IMessageHandler<TESPacketFastTravel, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFastTravel packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			if (TESConfig.enableFastTravel) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				boolean isCustom = packet.isCustom;
				int waypointID = packet.wpID;
				TESAbstractWaypoint waypoint = null;
				if (isCustom) {
					UUID sharingPlayer = packet.sharingPlayer;
					waypoint = sharingPlayer != null ? playerData.getSharedCustomWaypointByID(sharingPlayer, waypointID) : playerData.getCustomWaypointByID(waypointID);
				} else if (waypointID >= 0 && waypointID < TESWaypoint.values().length) {
					waypoint = TESWaypoint.values()[waypointID];
				}
				if (waypoint != null && waypoint.hasPlayerUnlocked(entityplayer)) {
					if (playerData.getTimeSinceFT() < playerData.getWaypointFTTime(waypoint, entityplayer)) {
						entityplayer.closeScreen();
						entityplayer.addChatMessage(new ChatComponentTranslation("tes.fastTravel.moreTime", waypoint.getDisplayName()));
					} else {
						boolean canTravel = playerData.canFastTravel();
						if (!canTravel) {
							entityplayer.closeScreen();
							entityplayer.addChatMessage(new ChatComponentTranslation("tes.fastTravel.underAttack"));
						} else if (entityplayer.isPlayerSleeping()) {
							entityplayer.closeScreen();
							entityplayer.addChatMessage(new ChatComponentTranslation("tes.fastTravel.inBed"));
						} else {
							playerData.setTargetFTWaypoint(waypoint);
						}
					}
				}
			} else {
				entityplayer.addChatMessage(new ChatComponentTranslation("tes.chat.ftDisabled"));
			}
			return null;
		}
	}
}