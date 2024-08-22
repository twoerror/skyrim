package tes.common.entity.ai;

import tes.TES;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import net.minecraft.entity.player.EntityPlayer;

public class TESEntityAIHiringPlayerHurtByTarget extends EntityAITarget {
	private final TESEntityNPC theNPC;

	private EntityLivingBase theTarget;
	private int playerRevengeTimer;

	public TESEntityAIHiringPlayerHurtByTarget(TESEntityNPC entity) {
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
		theTarget = entityplayer.getAITarget();
		int i = entityplayer.func_142015_aE();
		return i != playerRevengeTimer && TES.canNPCAttackEntity(theNPC, theTarget, true) && isSuitableTarget(theTarget, false);
	}

	@Override
	public void startExecuting() {
		theNPC.setAttackTarget(theTarget);
		EntityPlayer entityplayer = theNPC.getHireableInfo().getHiringPlayer();
		if (entityplayer != null) {
			playerRevengeTimer = entityplayer.func_142015_aE();
		}
		super.startExecuting();
	}
}