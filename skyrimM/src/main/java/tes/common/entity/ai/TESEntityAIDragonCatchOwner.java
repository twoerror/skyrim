package tes.common.entity.ai;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class TESEntityAIDragonCatchOwner extends EntityAIBase {
	protected final TESEntityDragon dragon;

	protected EntityPlayer owner;

	protected TESEntityAIDragonCatchOwner(TESEntityDragon dragon) {
		this.dragon = dragon;
	}

	@Override
	public boolean shouldExecute() {
		owner = (EntityPlayer) dragon.getOwner();
		return owner != null && !owner.capabilities.isCreativeMode && dragon.riddenByEntity == null && owner.fallDistance > 4;
	}
}