package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.ai.EntityAITargetNonTamed;

public class TESEntityAIDragonHunt extends EntityAITargetNonTamed {
	private final TESEntityDragon dragon;

	public TESEntityAIDragonHunt(TESEntityDragon dragon, Class<?> clazz, int par3, boolean par4) {
		super(dragon, clazz, par3, par4);
		this.dragon = dragon;
	}

	@Override
	public boolean shouldExecute() {
		return dragon.isAdult() && super.shouldExecute();
	}
}