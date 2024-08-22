package tes.common.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESConfig;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import tes.common.util.TESLog;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.UUID;

public class TESPacketFellowshipInvitePlayer extends TESPacketFellowshipDo {
	private String invitedUsername;

	@SuppressWarnings("unused")
	public TESPacketFellowshipInvitePlayer() {
	}

	public TESPacketFellowshipInvitePlayer(TESFellowshipClient fs, String username) {
		super(fs);
		invitedUsername = username;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		super.fromBytes(data);
		byte nameLength = data.readByte();
		ByteBuf nameBytes = data.readBytes(nameLength);
		invitedUsername = nameBytes.toString(Charsets.UTF_8);
	}

	@Override
	public void toBytes(ByteBuf data) {
		super.toBytes(data);
		byte[] nameBytes = invitedUsername.getBytes(Charsets.UTF_8);
		data.writeByte(nameBytes.length);
		data.writeBytes(nameBytes);
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipInvitePlayer, IMessage> {
		private static UUID findInvitedPlayerUUID(String invitedUsername) {
			GameProfile profile = MinecraftServer.getServer().func_152358_ax().func_152655_a(invitedUsername);
			if (profile != null && profile.getId() != null) {
				return profile.getId();
			}
			return null;
		}

		@Override
		public IMessage onMessage(TESPacketFellowshipInvitePlayer packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			TESFellowship fellowship = packet.getActiveFellowship();
			if (fellowship != null) {
				int limit = TESConfig.getFellowshipMaxSize(entityplayer.worldObj);
				if (limit >= 0 && fellowship.getPlayerCount() >= limit) {
					TESLog.getLogger().warn(String.format("Player %s tried to invite a player with username %s to fellowship %s, but fellowship size %d is already >= the maximum of %d", entityplayer.getCommandSenderName(), packet.invitedUsername, fellowship.getName(), fellowship.getPlayerCount(), limit));
				} else {
					UUID invitedPlayer = findInvitedPlayerUUID(packet.invitedUsername);
					if (invitedPlayer != null) {
						TESPlayerData playerData = TESLevelData.getData(entityplayer);
						playerData.invitePlayerToFellowship(fellowship, invitedPlayer, entityplayer.getCommandSenderName());
					} else {
						TESLog.getLogger().warn(String.format("Player %s tried to invite a player with username %s to fellowship %s, but couldn't find the invited player's UUID", entityplayer.getCommandSenderName(), packet.invitedUsername, fellowship.getName()));
					}
				}
			}
			return null;
		}
	}
}