package tes.common.entity.ai;

import tes.common.TESLevelData;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class TESEntityAINPCFollowPlayer extends EntityAIBase {
	private final TESEntityNPC entityFollowing;
	private final float range;
	private final double speed;

	private EntityPlayer playerToFollow;
	private int followDelay;

	public TESEntityAINPCFollowPlayer(TESEntityNPC npc, float f, double d) {
		entityFollowing = npc;
		range = f;
		speed = d;
	}

	@Override
	public boolean continueExecuting() {
		if (!playerToFollow.isEntityAlive()) {
			return false;
		}
		double distanceSq = entityFollowing.getDistanceSqToEntity(playerToFollow);
		return distanceSq >= 9.0 && distanceSq <= 256.0;
	}

	@Override
	public void resetTask() {
		playerToFollow = null;
	}

	@Override
	public boolean shouldExecute() {
		List<EntityPlayer> list = entityFollowing.worldObj.getEntitiesWithinAABB(EntityPlayer.class, entityFollowing.boundingBox.expand(range, 3.0, range));
		EntityPlayer entityplayer = null;
		double distanceSq = Double.MAX_VALUE;
		for (EntityPlayer playerCandidate : list) {
			double d;
			if (TESLevelData.getData(playerCandidate).getAlignment(entityFollowing.getFaction()) < 100.0f || (d = entityFollowing.getDistanceSqToEntity(playerCandidate)) > distanceSq) {
				continue;
			}
			distanceSq = d;
			entityplayer = playerCandidate;
		}
		if (entityplayer == null || distanceSq < 9.0) {
			return false;
		}
		playerToFollow = entityplayer;
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
			entityFollowing.getNavigator().tryMoveToEntityLiving(playerToFollow, speed);
		}
	}
}