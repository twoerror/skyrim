package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.ai.EntityAIPanic;

public class TESEntityAIDragonPanicChild extends EntityAIPanic {
	private final TESEntityDragon dragon;

	public TESEntityAIDragonPanicChild(TESEntityDragon dragon, double speed) {
		super(dragon, speed);
		this.dragon = dragon;
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && dragon.isHatchling();
	}
}