package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.fellowship.TESFellowship;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class TESPacketFellowshipAcceptInviteResult implements IMessage {
	private UUID fellowshipID;
	private String fellowshipName;
	private AcceptInviteResult result;

	@SuppressWarnings("unused")
	public TESPacketFellowshipAcceptInviteResult() {
	}

	public TESPacketFellowshipAcceptInviteResult(AcceptInviteResult result) {
		fellowshipID = null;
		fellowshipName = null;
		this.result = result;
	}

	public TESPacketFellowshipAcceptInviteResult(TESFellowship fs, AcceptInviteResult result) {
		fellowshipID = fs.getFellowshipID();
		fellowshipName = fs.getName();
		this.result = result;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		boolean hasFellowship = data.readBoolean();
		if (hasFellowship) {
			fellowshipID = new UUID(data.readLong(), data.readLong());
			byte fsNameLength = data.readByte();
			ByteBuf fsNameBytes = data.readBytes(fsNameLength);
			fellowshipName = fsNameBytes.toString(Charsets.UTF_8);
		}
		result = AcceptInviteResult.values()[data.readByte()];
	}

	@Override
	public void toBytes(ByteBuf data) {
		boolean hasFellowship = fellowshipID != null && fellowshipName != null;
		data.writeBoolean(hasFellowship);
		if (hasFellowship) {
			data.writeLong(fellowshipID.getMostSignificantBits());
			data.writeLong(fellowshipID.getLeastSignificantBits());
			byte[] fsNameBytes = fellowshipName.getBytes(Charsets.UTF_8);
			data.writeByte(fsNameBytes.length);
			data.writeBytes(fsNameBytes);
		}
		data.writeByte(result.ordinal());
	}

	public enum AcceptInviteResult {
		JOINED, DISBANDED, TOO_LARGE, NONEXISTENT
	}

	public static class Handler implements IMessageHandler<TESPacketFellowshipAcceptInviteResult, IMessage> {
		@Override
		public IMessage onMessage(TESPacketFellowshipAcceptInviteResult packet, MessageContext context) {
			TES.proxy.displayFellowshipAcceptInvitationResult(packet.fellowshipID, packet.fellowshipName, packet.result);
			return null;
		}
	}
}