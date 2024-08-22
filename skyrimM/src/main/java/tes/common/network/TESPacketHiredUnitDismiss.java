package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.entity.other.TESEntityNPC;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

public class TESPacketHiredUnitDismiss implements IMessage {
	private int entityID;
	private int action;

	@SuppressWarnings("unused")
	public TESPacketHiredUnitDismiss() {
	}

	public TESPacketHiredUnitDismiss(int id, int a) {
		entityID = id;
		action = a;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		action = data.readByte();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeByte(action);
	}

	public static class Handler implements IMessageHandler<TESPacketHiredUnitDismiss, IMessage> {
		@Override
		public IMessage onMessage(TESPacketHiredUnitDismiss packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			World world = entityplayer.worldObj;
			Entity npc = world.getEntityByID(packet.entityID);
			if (npc instanceof TESEntityNPC) {
				TESEntityNPC hiredNPC = (TESEntityNPC) npc;
				if (hiredNPC.getHireableInfo().isActive() && hiredNPC.getHireableInfo().getHiringPlayer() == entityplayer) {
					int action = packet.action;
					boolean closeScreen = false;
					if (action == 0) {
						hiredNPC.getHireableInfo().dismissUnit(false);
						Entity mount = hiredNPC.ridingEntity;
						Entity rider = hiredNPC.riddenByEntity;
						if (mount instanceof TESEntityNPC) {
							TESEntityNPC mountNPC = (TESEntityNPC) mount;
							if (mountNPC.getHireableInfo().isActive() && mountNPC.getHireableInfo().getHiringPlayer() == entityplayer) {
								mountNPC.getHireableInfo().dismissUnit(false);
							}
						}
						if (rider instanceof TESEntityNPC) {
							TESEntityNPC riderNPC = (TESEntityNPC) rider;
							if (riderNPC.getHireableInfo().isActive() && riderNPC.getHireableInfo().getHiringPlayer() == entityplayer) {
								riderNPC.getHireableInfo().dismissUnit(false);
							}
						}
						closeScreen = true;
					}
					if (closeScreen) {
						entityplayer.closeScreen();
					}
				}
			}
			return null;
		}
	}
}