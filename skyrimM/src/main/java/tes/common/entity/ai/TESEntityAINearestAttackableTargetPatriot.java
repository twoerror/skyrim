package tes.common.entity.ai;

import tes.TES;
import tes.common.TESLevelData;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;

public class TESEntityAINearestAttackableTargetPatriot extends TESEntityAINearestAttackableTargetBasic {
	public TESEntityAINearestAttackableTargetPatriot(EntityCreature entity, Class<? extends Entity> targetClass, int chance, boolean flag) {
		super(entity, targetClass, chance, flag);
	}

	public TESEntityAINearestAttackableTargetPatriot(EntityCreature entity, Class<? extends Entity> targetClass, int chance, boolean flag, IEntitySelector selector) {
		super(entity, targetClass, chance, flag, selector);
	}

	@Override
	public boolean isPlayerSuitableAlignmentTarget(EntityPlayer entityplayer) {
		float alignment = TESLevelData.getData(entityplayer).getAlignment(TES.getNPCFaction(taskOwner));
		return alignment < 50.0f;
	}
}