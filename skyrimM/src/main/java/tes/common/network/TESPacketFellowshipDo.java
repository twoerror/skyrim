package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import tes.common.fellowship.TESFellowship;
import tes.common.fellowship.TESFellowshipClient;
import tes.common.fellowship.TESFellowshipData;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public abstract class TESPacketFellowshipDo implements IMessage {
	private UUID fellowshipID;

	protected TESPacketFellowshipDo() {
	}

	protected TESPacketFellowshipDo(TESFellowshipClient fsClient) {
		fellowshipID = fsClient.getFellowshipID();
	}

	@Override
	public void fromBytes(ByteBuf data) {
		fellowshipID = new UUID(data.readLong(), data.readLong());
	}

	protected TESFellowship getActiveFellowship() {
		return TESFellowshipData.getActiveFellowship(fellowshipID);
	}

	protected TESFellowship getActiveOrDisbandedFellowship() {
		return TESFellowshipData.getFellowship(fellowshipID);
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeLong(fellowshipID.getMostSignificantBits());
		data.writeLong(fellowshipID.getLeastSignificantBits());
	}
}