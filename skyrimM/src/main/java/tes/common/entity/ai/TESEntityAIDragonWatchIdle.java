package tes.common.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAILookIdle;

public class TESEntityAIDragonWatchIdle extends EntityAILookIdle {
	public TESEntityAIDragonWatchIdle(EntityLiving par1EntityLiving) {
		super(par1EntityLiving);
		setMutexBits(2);
	}
}