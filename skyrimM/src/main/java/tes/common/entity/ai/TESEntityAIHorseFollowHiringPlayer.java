package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESNPCMount;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class TESEntityAIHorseFollowHiringPlayer extends EntityAIBase {
	private final TESNPCMount theHorse;
	private final EntityCreature livingHorse;
	private final float maxNearDist;
	private final float minFollowDist;

	private EntityPlayer theHiringPlayer;
	private boolean avoidsWater;
	private boolean initSpeed;
	private double moveSpeed;
	private int followTick;

	public TESEntityAIHorseFollowHiringPlayer(TESNPCMount entity) {
		theHorse = entity;
		livingHorse = (EntityCreature) theHorse;
		minFollowDist = 8.0f;
		maxNearDist = 6.0f;
		setMutexBits(3);
	}

	@Override
	public boolean continueExecuting() {
		if (livingHorse.riddenByEntity == null || !livingHorse.riddenByEntity.isEntityAlive() || !(livingHorse.riddenByEntity instanceof TESEntityNPC)) {
			return false;
		}
		TESEntityNPC ridingNPC = (TESEntityNPC) livingHorse.riddenByEntity;
		return ridingNPC.getHireableInfo().isActive() && ridingNPC.getHireableInfo().getHiringPlayer() != null && ridingNPC.getHireableInfo().shouldFollowPlayer() && !livingHorse.getNavigator().noPath() && livingHorse.getDistanceSqToEntity(theHiringPlayer) > maxNearDist * maxNearDist;
	}

	@Override
	public void resetTask() {
		theHiringPlayer = null;
		livingHorse.getNavigator().clearPathEntity();
		livingHorse.getNavigator().setAvoidsWater(avoidsWater);
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
		if (!ridingNPC.getHireableInfo().isActive()) {
			return false;
		}
		EntityPlayer entityplayer = ridingNPC.getHireableInfo().getHiringPlayer();
		if (entityplayer == null || !ridingNPC.getHireableInfo().shouldFollowPlayer() || livingHorse.getDistanceSqToEntity(entityplayer) < minFollowDist * minFollowDist) {
			return false;
		}
		theHiringPlayer = entityplayer;
		return true;
	}

	@Override
	public void startExecuting() {
		followTick = 0;
		avoidsWater = livingHorse.getNavigator().getAvoidsWater();
		livingHorse.getNavigator().setAvoidsWater(false);
		if (!initSpeed) {
			double d = livingHorse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
			moveSpeed = 1.0 / d * 0.37;
			initSpeed = true;
		}
	}

	@Override
	public void updateTask() {
		TESEntityNPC ridingNPC = (TESEntityNPC) livingHorse.riddenByEntity;
		livingHorse.getLookHelper().setLookPositionWithEntity(theHiringPlayer, 10.0f, livingHorse.getVerticalFaceSpeed());
		ridingNPC.rotationYaw = livingHorse.rotationYaw;
		ridingNPC.rotationYawHead = livingHorse.rotationYawHead;
		if (ridingNPC.getHireableInfo().shouldFollowPlayer()) {
			--followTick;
			if (followTick <= 0) {
				followTick = 10;
				if (!livingHorse.getNavigator().tryMoveToEntityLiving(theHiringPlayer, moveSpeed) && ridingNPC.getHireableInfo().isTeleportAutomatically()) {
					double d = ridingNPC.getEntityAttribute(SharedMonsterAttributes.followRange).getAttributeValue();
					d = Math.max(d, 24.0);
					if (ridingNPC.getDistanceSqToEntity(theHiringPlayer) > d * d) {
						ridingNPC.getHireableInfo().tryTeleportToHiringPlayer(false);
					}
				}
			}
		}
	}
}