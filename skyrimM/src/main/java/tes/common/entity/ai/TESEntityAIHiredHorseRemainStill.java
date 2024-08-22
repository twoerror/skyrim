package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIBase;

public class TESEntityAIHiredHorseRemainStill extends EntityAIBase {
	private final TESNPCMount theHorse;
	private final EntityCreature livingHorse;

	public TESEntityAIHiredHorseRemainStill(TESNPCMount entity) {
		theHorse = entity;
		livingHorse = (EntityCreature) theHorse;
		setMutexBits(5);
	}

	@Override
	public boolean shouldExecute() {
		if (!theHorse.getBelongsToNPC()) {
			return false;
		}
		Entity rider = livingHorse.riddenByEntity;
		if (rider == null || !rider.isEntityAlive() || !(rider instanceof TESEntityNPC)) {
			return false;
		}
		TESEntityNPC ridingNPC = (TESEntityNPC) rider;
		return ridingNPC.getHireableInfo().isActive() && !livingHorse.isInWater() && livingHorse.onGround && ridingNPC.getHireableInfo().isHalted() && (ridingNPC.getAttackTarget() == null || !ridingNPC.getAttackTarget().isEntityAlive());
	}

	@Override
	public void startExecuting() {
		livingHorse.getNavigator().clearPathEntity();
	}
}