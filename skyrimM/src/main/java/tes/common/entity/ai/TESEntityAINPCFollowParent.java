package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.ai.EntityAIBase;

public class TESEntityAINPCFollowParent extends EntityAIBase {
	private final TESEntityNPC theNPC;
	private final double moveSpeed;

	private TESEntityNPC parentNPC;
	private int followTick;

	public TESEntityAINPCFollowParent(TESEntityNPC npc, double d) {
		theNPC = npc;
		moveSpeed = d;
	}

	@Override
	public boolean continueExecuting() {
		if (!parentNPC.isEntityAlive()) {
			return false;
		}
		double d = theNPC.getDistanceSqToEntity(parentNPC);
		return d >= 9.0 && d <= 256.0;
	}

	@Override
	public void resetTask() {
		parentNPC = null;
	}

	@Override
	public boolean shouldExecute() {
		if (theNPC.getFamilyInfo().getAge() >= 0) {
			return false;
		}
		TESEntityNPC parent = theNPC.getFamilyInfo().getParentToFollow();
		if (parent == null || theNPC.getDistanceSqToEntity(parent) < 9.0 || theNPC.getDistanceSqToEntity(parent) >= 256.0) {
			return false;
		}
		parentNPC = parent;
		return true;
	}

	@Override
	public void startExecuting() {
		followTick = 0;
	}

	@Override
	public void updateTask() {
		if (followTick <= 0) {
			followTick = 10;
			theNPC.getNavigator().tryMoveToEntityLiving(parentNPC, moveSpeed);
		}
		followTick--;
	}
}