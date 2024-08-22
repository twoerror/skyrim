package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.utils.TESUnitTradeEntry;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TESPacketHiredGui implements IMessage {
	private TESUnitTradeEntry.PledgeType pledgeType;
	private boolean teleportAutomatically;
	private boolean isActive;
	private boolean canMove;
	private boolean openGui;
	private boolean inCombat;
	private boolean guardMode;
	private float alignmentRequired;
	private int guardRange;
	private int entityID;
	private int mobKills;
	private int xp;

	@SuppressWarnings("unused")
	public TESPacketHiredGui() {
	}

	public TESPacketHiredGui(int i, boolean flag) {
		entityID = i;
		openGui = flag;
	}

	@Override
	public void fromBytes(ByteBuf data) {
		entityID = data.readInt();
		openGui = data.readBoolean();
		isActive = data.readBoolean();
		canMove = data.readBoolean();
		teleportAutomatically = data.readBoolean();
		mobKills = data.readInt();
		xp = data.readInt();
		alignmentRequired = data.readFloat();
		pledgeType = TESUnitTradeEntry.PledgeType.forID(data.readByte());
		inCombat = data.readBoolean();
		guardMode = data.readBoolean();
		guardRange = data.readInt();
	}

	@Override
	public void toBytes(ByteBuf data) {
		data.writeInt(entityID);
		data.writeBoolean(openGui);
		data.writeBoolean(isActive);
		data.writeBoolean(canMove);
		data.writeBoolean(teleportAutomatically);
		data.writeInt(mobKills);
		data.writeInt(xp);
		data.writeFloat(alignmentRequired);
		data.writeByte(pledgeType.getTypeID());
		data.writeBoolean(inCombat);
		data.writeBoolean(guardMode);
		data.writeInt(guardRange);
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean active) {
		isActive = active;
	}

	public boolean isCanMove() {
		return canMove;
	}

	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public boolean isTeleportAutomatically() {
		return teleportAutomatically;
	}

	public void setTeleportAutomatically(boolean teleportAutomatically) {
		this.teleportAutomatically = teleportAutomatically;
	}

	public int getMobKills() {
		return mobKills;
	}

	public void setMobKills(int mobKills) {
		this.mobKills = mobKills;
	}

	public int getXp() {
		return xp;
	}

	public void setXp(int xp) {
		this.xp = xp;
	}

	public float getAlignmentRequired() {
		return alignmentRequired;
	}

	public void setAlignmentRequired(float alignmentRequired) {
		this.alignmentRequired = alignmentRequired;
	}

	public TESUnitTradeEntry.PledgeType getPledgeType() {
		return pledgeType;
	}

	public void setPledgeType(TESUnitTradeEntry.PledgeType pledgeType) {
		this.pledgeType = pledgeType;
	}

	public boolean isInCombat() {
		return inCombat;
	}

	public void setInCombat(boolean inCombat) {
		this.inCombat = inCombat;
	}

	public boolean isGuardMode() {
		return guardMode;
	}

	public void setGuardMode(boolean guardMode) {
		this.guardMode = guardMode;
	}

	public int getGuardRange() {
		return guardRange;
	}

	public void setGuardRange(int guardRange) {
		this.guardRange = guardRange;
	}

	public static class Handler implements IMessageHandler<TESPacketHiredGui, IMessage> {
		@Override
		public IMessage onMessage(TESPacketHiredGui packet, MessageContext context) {
			EntityPlayer entityplayer = TES.proxy.getClientPlayer();
			World world = TES.proxy.getClientWorld();
			Entity entity = world.getEntityByID(packet.entityID);
			if (entity instanceof TESEntityNPC) {
				TESEntityNPC npc = (TESEntityNPC) entity;
				if (npc.getHireableInfo().getHiringPlayer() == entityplayer) {
					npc.getHireableInfo().receiveClientPacket(packet);
					if (packet.openGui) {
						TES.proxy.openHiredNPCGui(npc);
					}
				}
			}
			return null;
		}
	}
}