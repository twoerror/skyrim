package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.ai.EntityAIBase;

public class TESEntityAINPCFollowSpouse extends EntityAIBase {
	private final TESEntityNPC theNPC;
	private final double moveSpeed;

	private TESEntityNPC theSpouse;
	private int followTick;

	public TESEntityAINPCFollowSpouse(TESEntityNPC npc, double d) {
		theNPC = npc;
		moveSpeed = d;
	}

	@Override
	public boolean continueExecuting() {
		if (!theSpouse.isEntityAlive()) {
			return false;
		}
		double d = theNPC.getDistanceSqToEntity(theSpouse);
		return d >= 36.0 && d <= 256.0;
	}

	@Override
	public void resetTask() {
		theSpouse = null;
	}

	@Override
	public boolean shouldExecute() {
		TESEntityNPC spouse = theNPC.getFamilyInfo().getSpouse();
		if (spouse == null) {
			return false;
		}
		if (!spouse.isEntityAlive() || theNPC.getDistanceSqToEntity(spouse) < 36.0 || theNPC.getDistanceSqToEntity(spouse) >= 256.0) {
			return false;
		}
		theSpouse = spouse;
		return true;
	}

	@Override
	public void startExecuting() {
		followTick = 200;
	}

	@Override
	public void updateTask() {
		--followTick;
		if (theNPC.getDistanceSqToEntity(theSpouse) > 144.0 || followTick <= 0) {
			followTick = 200;
			theNPC.getNavigator().tryMoveToEntityLiving(theSpouse, moveSpeed);
		}
	}
}