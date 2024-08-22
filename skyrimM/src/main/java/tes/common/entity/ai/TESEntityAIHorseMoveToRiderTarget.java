package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.Vec3;

public class TESEntityAIHorseMoveToRiderTarget extends EntityAIBase {
	private final TESNPCMount theHorse;
	private final EntityCreature livingHorse;

	private PathEntity entityPathEntity;
	private double speed;
	private int pathCheckTimer;

	public TESEntityAIHorseMoveToRiderTarget(TESNPCMount horse) {
		theHorse = horse;
		livingHorse = (EntityCreature) theHorse;
		setMutexBits(3);
	}

	@Override
	public boolean continueExecuting() {
		if (livingHorse.riddenByEntity == null || !livingHorse.riddenByEntity.isEntityAlive() || !(livingHorse.riddenByEntity instanceof TESEntityNPC)) {
			return false;
		}
		TESEntityNPC rider = (TESEntityNPC) livingHorse.riddenByEntity;
		EntityLivingBase riderTarget = rider.getAttackTarget();
		return riderTarget != null && riderTarget.isEntityAlive() && !livingHorse.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
		livingHorse.getNavigator().clearPathEntity();
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
		EntityLivingBase riderTarget = ((EntityLiving) rider).getAttackTarget();
		if (riderTarget == null || !riderTarget.isEntityAlive()) {
			return false;
		}
		entityPathEntity = livingHorse.getNavigator().getPathToEntityLiving(riderTarget);
		return entityPathEntity != null;
	}

	@Override
	public void startExecuting() {
		speed = ((EntityLivingBase) livingHorse.riddenByEntity).getEntityAttribute(TESEntityNPC.HORSE_ATTACK_SPEED).getAttributeValue();
		livingHorse.getNavigator().setPath(entityPathEntity, speed);
		pathCheckTimer = 0;
	}

	@Override
	public void updateTask() {
		TESEntityNPC rider = (TESEntityNPC) livingHorse.riddenByEntity;
		EntityLivingBase riderTarget = rider.getAttackTarget();
		boolean aimingBow = rider.isAimingRanged() && livingHorse.getEntitySenses().canSee(riderTarget);
		if (!aimingBow) {
			livingHorse.getLookHelper().setLookPositionWithEntity(riderTarget, 30.0f, 30.0f);
			rider.rotationYaw = livingHorse.rotationYaw;
			rider.rotationYawHead = livingHorse.rotationYawHead;
		}
		--pathCheckTimer;
		if (pathCheckTimer <= 0) {
			pathCheckTimer = 4 + livingHorse.getRNG().nextInt(7);
			livingHorse.getNavigator().tryMoveToEntityLiving(riderTarget, speed);
		}
		if (aimingBow) {
			if (rider.getDistanceSqToEntity(riderTarget) < 25.0) {
				Vec3 vec = TESEntityAIRangedAttack.findPositionAwayFrom(rider, riderTarget, 8, 16);
				if (vec != null) {
					livingHorse.getNavigator().tryMoveToXYZ(vec.xCoord, vec.yCoord, vec.zCoord, speed);
				}
			} else {
				livingHorse.getNavigator().clearPathEntity();
			}
		}
	}
}