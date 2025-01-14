package tes.common.network;

import com.google.common.base.Charsets;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESSquadrons;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

public class TESPacketNPCSquadron implements IMessage {
	private int npcID;
	private String squadron;

	@SuppressWarnings("unused")
	public TESPacketNPCSquadron() {
	}

	public TESPacketNPCSquadron(TESEntityNPC npc, String s) {
		npcID = npc.getEntityId();
		squadron = s;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		npcID = data.readInt();
		int length = data.readInt();
		if (length > -1) {
			squadron = data.readBytes(length).toString(Charsets.UTF_8);
		}
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(npcID);
		if (StringUtils.isNullOrEmpty(squadron)) {
			data.writeInt(-1);
		} else {
			byte[] sqBytes = squadron.getBytes(Charsets.UTF_8);
			data.writeInt(sqBytes.length);
			data.writeBytes(sqBytes);
		}
	}

	public static class Handler implements IMessageHandler<TESPacketNPCSquadron, IMessage> {
		@Override
		public IMessage onMessage(TESPacketNPCSquadron packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			Entity npc = world.getEntityByID(packet.npcID);
			if (npc instanceof TESEntityNPC) {
				TESEntityNPC hiredNPC = (TESEntityNPC) npc;
				if (hiredNPC.getHireableInfo().isActive() && hiredNPC.getHireableInfo().getHiringPlayer() == entityplayer) {
					String squadron = packet.squadron;
					if (StringUtils.isNullOrEmpty(squadron)) {
						hiredNPC.getHireableInfo().setHiredSquadron("");
					} else {
						squadron = TESSquadrons.checkAcceptableLength(squadron);
						hiredNPC.getHireableInfo().setHiredSquadron(squadron);
					}
				}
			}
			return null;
		}
	}
}