package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;

public class TESEntityAIDragonCatchOwnerGround extends TESEntityAIDragonCatchOwner {
	public TESEntityAIDragonCatchOwnerGround(TESEntityDragon dragon) {
		super(dragon);
		setMutexBits(0xffffffff);
	}

	@Override
	public void updateTask() {
		dragon.liftOff();
	}
}