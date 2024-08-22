package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;

import java.util.List;

public class TESEntityAINPCFollowNPC extends EntityAIBase {
	private final Class<? extends Entity> entityClassToFollow;
	private final TESEntityNPC entityFollowing;

	private TESEntityNPC entityToFollow;
	private int followDelay;

	public TESEntityAINPCFollowNPC(TESEntityNPC npc, Class<? extends Entity> target) {
		entityFollowing = npc;
		entityClassToFollow = target;
	}

	@Override
	public boolean continueExecuting() {
		double distanceSq = entityFollowing.getDistanceSqToEntity(entityToFollow);
		return entityToFollow.isEntityAlive() && distanceSq >= 9.0 && distanceSq <= 256.0;
	}

	@Override
	public void resetTask() {
		entityToFollow = null;
	}

	@Override
	public boolean shouldExecute() {
		List<TESEntityNPC> list = entityFollowing.worldObj.getEntitiesWithinAABB(entityClassToFollow, entityFollowing.boundingBox.expand(64.0f, 3.0, 64.0f));
		TESEntityNPC entityNPC = null;
		double distanceSq = Double.MAX_VALUE;
		for (TESEntityNPC npcCandidate : list) {
			double d = entityFollowing.getDistanceSqToEntity(npcCandidate);
			if (d > distanceSq) {
				continue;
			}
			distanceSq = d;
			entityNPC = npcCandidate;
		}
		if (entityNPC == null || entityFollowing.getCurrentAttackMode() != TESEntityNPC.AttackMode.IDLE || distanceSq < 9.0) {
			return false;
		}
		entityToFollow = entityNPC;
		return true;
	}

	@Override
	public void startExecuting() {
		followDelay = 0;
	}

	@Override
	public void updateTask() {
		--followDelay;
		if (followDelay <= 0) {
			followDelay = 10;
			entityFollowing.getNavigator().tryMoveToEntityLiving(entityToFollow, 1.0);
		}
	}
}