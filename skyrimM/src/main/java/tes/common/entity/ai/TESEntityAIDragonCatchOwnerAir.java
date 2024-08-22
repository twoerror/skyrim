package tes.common.entity.ai;

import tes.client.model.TESModelDragonAnimaton;
import tes.common.entity.dragon.TESDragonFlightWaypoint;
import tes.common.entity.dragon.TESEntityDragon;

public class TESEntityAIDragonCatchOwnerAir extends TESEntityAIDragonCatchOwner {
	public TESEntityAIDragonCatchOwnerAir(TESEntityDragon dragon) {
		super(dragon);
	}

	@Override
	public void updateTask() {
		TESDragonFlightWaypoint wp = dragon.getWaypoint();
		wp.setEntity(owner);

		double dist = wp.getDistance();
		double yOfs = TESModelDragonAnimaton.clamp(dist, 0, 64);

		wp.setPosY(wp.getPosY() - (int) yOfs);

		if (wp.isNear()) {
			owner.mountEntity(dragon);
		}
		dragon.setMoveSpeedAirHoriz(1);
	}
}