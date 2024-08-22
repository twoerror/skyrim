package tes.common.entity.ai;

import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

public class TESEntityAIHiringPlayerHurtTarget extends EntityAITarget {
	private final TESEntityNPC theNPC;

	private EntityLivingBase theTarget;
	private int playerLastAttackerTime;

	public TESEntityAIHiringPlayerHurtTarget(TESEntityNPC entity) {
		super(entity, false);
		theNPC = entity;
		setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if (!theNPC.getHireableInfo().isActive() || theNPC.getHireableInfo().isHalted()) {
			return false;
		}
		EntityPlayer entityplayer = theNPC.getHireableInfo().getHiringPlayer();
		if (entityplayer == null) {
			return false;
		}
		theTarget = entityplayer.getLastAttacker();
		int i = entityplayer.getLastAttackerTime();
		return i != playerLastAttackerTime && TES.canNPCAttackEntity(theNPC, theTarget, true) && isSuitableTarget(theTarget, false);
	}

	@Override
	public void startExecuting() {
		theNPC.setAttackTarget(theTarget);
		theNPC.getHireableInfo().setWasAttackCommanded(true);
		EntityPlayer entityplayer = theNPC.getHireableInfo().getHiringPlayer();
		if (entityplayer != null) {
			playerLastAttackerTime = entityplayer.getLastAttackerTime();
		}
		super.startExecuting();
	}
}