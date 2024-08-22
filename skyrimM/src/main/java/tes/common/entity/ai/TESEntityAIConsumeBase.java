package tes.common.entity.ai;

import tes.common.database.TESFoods;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

import java.util.Random;

public abstract class TESEntityAIConsumeBase extends EntityAIBase {
	protected final TESEntityNPC theEntity;
	protected final Random rand;
	protected final TESFoods foodPool;
	private final int chanceToConsume;
	private int consumeTick;

	protected TESEntityAIConsumeBase(TESEntityNPC entity, TESFoods foods, int chance) {
		theEntity = entity;
		rand = theEntity.getRNG();
		foodPool = foods;
		chanceToConsume = chance;
		setMutexBits(3);
	}

	protected abstract void consume();

	@Override
	public boolean continueExecuting() {
		return consumeTick > 0 && theEntity.getHeldItem() != null && theEntity.getAttackTarget() == null;
	}

	protected abstract ItemStack createConsumable();

	protected int getConsumeTime() {
		return 32;
	}

	@Override
	public void resetTask() {
		theEntity.setCurrentItemOrArmor(0, theEntity.getNpcItemsInv().getEatingBackup());
		theEntity.getNpcItemsInv().setEatingBackup(null);
		theEntity.getNpcItemsInv().setIsEating(false);
		theEntity.refreshCurrentAttackMode();
		consumeTick = 0;
	}

	protected boolean shouldConsume() {
		boolean needsHeal = theEntity.getHealth() < theEntity.getMaxHealth();
		return needsHeal && rand.nextInt(chanceToConsume / 4) == 0 || rand.nextInt(chanceToConsume) == 0;
	}

	@Override
	public boolean shouldExecute() {
		return !theEntity.isChild() && theEntity.getAttackTarget() == null && !theEntity.getNpcItemsInv().getIsEating() && shouldConsume();
	}

	@Override
	public void startExecuting() {
		theEntity.getNpcItemsInv().setEatingBackup(theEntity.getHeldItem());
		theEntity.getNpcItemsInv().setIsEating(true);
		theEntity.setCurrentItemOrArmor(0, createConsumable());
		consumeTick = getConsumeTime();
	}

	protected abstract void updateConsumeTick(int var1);

	@Override
	public void updateTask() {
		--consumeTick;
		updateConsumeTick(consumeTick);
		if (consumeTick == 0) {
			consume();
		}
	}
}