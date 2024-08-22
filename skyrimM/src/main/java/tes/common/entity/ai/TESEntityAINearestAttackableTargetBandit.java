package tes.common.entity.ai;

import tes.common.entity.other.TESEntityBanditBase;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class TESEntityAINearestAttackableTargetBandit extends TESEntityAINearestAttackableTargetBasic {
	private final TESEntityBanditBase taskOwnerAsBandit;

	public TESEntityAINearestAttackableTargetBandit(EntityCreature entity, Class<? extends Entity> targetClass, int chance, boolean flag) {
		super(entity, targetClass, chance, flag);
		taskOwnerAsBandit = (TESEntityBanditBase) entity;
	}

	public TESEntityAINearestAttackableTargetBandit(EntityCreature entity, Class<? extends Entity> targetClass, int chance, boolean flag, IEntitySelector selector) {
		super(entity, targetClass, chance, flag, selector);
		taskOwnerAsBandit = (TESEntityBanditBase) entity;
	}

	@Override
	public boolean isPlayerSuitableTarget(EntityPlayer entityplayer) {
		return !TESEntityBanditBase.canStealFromPlayerInv(entityplayer) && super.isPlayerSuitableTarget(entityplayer);
	}

	@Override
	public boolean isSuitableTarget(EntityLivingBase entity, boolean flag) {
		return entity instanceof EntityPlayer && super.isSuitableTarget(entity, flag);
	}

	@Override
	public boolean shouldExecute() {
		return taskOwnerAsBandit.getBanditInventory().isEmpty() && super.shouldExecute();
	}
}