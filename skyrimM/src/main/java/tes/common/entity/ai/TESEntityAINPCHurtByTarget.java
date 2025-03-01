package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;

public class TESEntityAINPCHurtByTarget extends EntityAIHurtByTarget {
	public TESEntityAINPCHurtByTarget(TESEntityNPC npc, boolean flag) {
		super(npc, flag);
	}

	@Override
	public boolean isSuitableTarget(EntityLivingBase entity, boolean flag) {
		if (entity == taskOwner.ridingEntity || entity == taskOwner.riddenByEntity) {
			return false;
		}
		int homeX = taskOwner.getHomePosition().posX;
		int homeY = taskOwner.getHomePosition().posY;
		int homeZ = taskOwner.getHomePosition().posZ;
		int homeRange = (int) taskOwner.func_110174_bM();
		taskOwner.detachHome();
		boolean superSuitable = super.isSuitableTarget(entity, flag);
		taskOwner.setHomeArea(homeX, homeY, homeZ, homeRange);
		return superSuitable;
	}
}