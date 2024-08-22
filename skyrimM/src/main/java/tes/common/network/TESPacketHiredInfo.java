package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.info.TESHireableInfo;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import java.util.UUID;

public class TESPacketHiredInfo implements IMessage {
	private TESHireableInfo.Task task;
	private String squadron;
	private UUID hiringPlayer;

	private boolean isHired;
	private int xpLvl;
	private int entityID;

	@SuppressWarnings("unused")
	public TESPacketHiredInfo() {
	}

	public TESPacketHiredInfo(int i, UUID player, TESHireableInfo.Task t, String sq, int lvl) {
		entityID = i;
		hiringPlayer = player;
		isHired = hiringPlayer != null;
		task = t;
		squadron = sq;
		xpLvl = lvl;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		isHired = data.readBoolean();
		hiringPlayer = isHired ? new UUID(data.readLong(), data.readLong()) : null;
		task = TESHireableInfo.Task.forID(data.readByte());
		short sqLength = data.readShort();
		if (sqLength > -1) {
			squadron = data.readBytes(sqLength).toString(Charsets.UTF_8);
		}
		xpLvl = data.readShort();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeBoolean(isHired);
		if (isHired) {
			data.writeLong(hiringPlayer.getMostSignificantBits());
			data.writeLong(hiringPlayer.getLeastSignificantBits());
		}
		data.writeByte(task.ordinal());
		if (StringUtils.isNullOrEmpty(squadron)) {
			data.writeShort(-1);
		} else {
			byte[] sqBytes = squadron.getBytes(Charsets.UTF_8);
			data.writeShort(sqBytes.length);
			data.writeBytes(sqBytes);
		}
		data.writeShort(xpLvl);
	}

	public UUID getHiringPlayer() {
		return hiringPlayer;
	}

	public TESHireableInfo.Task getTask() {
		return task;
	}

	public String getSquadron() {
		return squadron;
	}

	public int getXpLvl() {
		return xpLvl;
	}

	public static class Handler implements IMessageHandler<TESPacketHiredInfo, IMessage> {
		@Override
		public IMessage onMessage(TESPacketHiredInfo packet, MessageContext context) {
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				((TESEntityNPC) entity).getHireableInfo().receiveBasicData(packet);
			}
			return null;
		}
	}
}