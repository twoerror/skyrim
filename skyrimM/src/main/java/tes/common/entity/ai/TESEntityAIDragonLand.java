package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.Vec3;

public class TESEntityAIDragonLand extends EntityAIBase {
	private final TESEntityDragon dragon;

	private Vec3 landTarget;

	public TESEntityAIDragonLand(TESEntityDragon dragon) {
		this.dragon = dragon;
	}

	@Override
	public boolean shouldExecute() {
		if (!dragon.isFlying() || dragon.getRidingPlayer() != null) {
			return false;
		}
		landTarget = RandomPositionGenerator.findRandomTarget(dragon, 16, 256);

		return landTarget != null;
	}

	@Override
	public void startExecuting() {
		landTarget.yCoord = 0;
		dragon.getWaypoint().setVector(landTarget);
		dragon.setMoveSpeedAirHoriz(1);
		dragon.setMoveSpeedAirVert(0);
	}
}