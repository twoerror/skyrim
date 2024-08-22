package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.TESPlayerData;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.UUID;

public class TESPacketFellowshipDoPlayer extends TESPacketFellowshipDo {
	private UUID subjectUuid;
	private PlayerFunction function;

	@SuppressWarnings("unused")
	public TESPacketFellowshipDoPlayer() {
	}

	public TESPacketFellowshipDoPlayer(TESFellowshipClient fs, UUID subject, PlayerFunction f) {
		super(fs);
		subjectUuid = subject;
		function = f;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		super.fromBytes(data);
		subjectUuid = new UUID(data.readLong(), data.readLong());
		function = PlayerFunction.values()[data.readByte()];
	}

	@Override
	public void toBytes(ByteBuf data) {
		super.toBytes(data);
		data.writeLong(subjectUuid.getMostSignificantBits());
		data.writeLong(subjectUuid.getLeastSignificantBits());
		data.writeByte(function.ordinal());
	}

	public enum PlayerFunction {
		REMOVE, TRANSFER, OP, DEOP
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipDoPlayer, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipDoPlayer packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			String playerName = entityplayer.getCommandSenderName();
			TESFellowship fellowship = packet.getActiveFellowship();
			UUID subjectPlayer = packet.subjectUuid;
			if (fellowship != null && subjectPlayer != null) {
				TESPlayerData playerData = TESLevelData.getData(entityplayer);
				switch (packet.function) {
					case DEOP:
						playerData.setFellowshipAdmin(fellowship, subjectPlayer, false, playerName);
						break;
					case OP:
						playerData.setFellowshipAdmin(fellowship, subjectPlayer, true, playerName);
						break;
					case REMOVE:
						playerData.removePlayerFromFellowship(fellowship, subjectPlayer, playerName);
						break;
					case TRANSFER:
						playerData.transferFellowship(fellowship, subjectPlayer, playerName);
						break;
				}
			}
			return null;
		}
	}
}