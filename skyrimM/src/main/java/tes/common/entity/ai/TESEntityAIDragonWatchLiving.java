package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.AxisAlignedBB;

public class TESEntityAIDragonWatchLiving extends EntityAIBase {
	private final TESEntityDragon dragon;
	private final float maxDist;
	private final float watchChance;

	private Entity watchedEntity;
	private int watchTicks;

	public TESEntityAIDragonWatchLiving(TESEntityDragon dragon, float maxDist, float watchChance) {
		this.dragon = dragon;
		this.maxDist = maxDist;
		this.watchChance = watchChance;
		setMutexBits(2);
	}

	@Override
	public boolean continueExecuting() {
		return watchedEntity.isEntityAlive() && !(dragon.getDistanceSqToEntity(watchedEntity) > maxDist * maxDist) && watchTicks > 0;
	}

	@Override
	public void resetTask() {
		dragon.renderYawOffset = 0;
		watchedEntity = null;
	}

	@Override
	public boolean shouldExecute() {
		if (dragon.getRNG().nextFloat() >= watchChance) {
			return false;
		}
		AxisAlignedBB aabb = dragon.boundingBox.expand(maxDist, dragon.height, maxDist);
		watchedEntity = dragon.worldObj.findNearestEntityWithinAABB(EntityLiving.class, aabb, dragon);
		if (watchedEntity != null) {
			if (watchedEntity == dragon.getRidingPlayer()) {
				watchedEntity = null;
			}
			if (watchedEntity == dragon.getOwner()) {
				watchTicks *= 3;
			}
		}
		return watchedEntity != null;
	}

	@Override
	public void startExecuting() {
		watchTicks = 40 + dragon.getRNG().nextInt(40);
	}

	@Override
	public void updateTask() {
		double lx = watchedEntity.posX;
		double ly = watchedEntity.posY + watchedEntity.getEyeHeight();
		double lz = watchedEntity.posZ;
		dragon.getLookHelper().setLookPosition(lx, ly, lz, 10, dragon.getVerticalFaceSpeed());
		watchTicks--;
	}
}