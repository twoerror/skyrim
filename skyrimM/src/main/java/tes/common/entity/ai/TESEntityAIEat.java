package tes.common.entity.ai;

import tes.common.database.TESFoods;
import tes.common.entity.other.TESEntityNPC;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

public class TESEntityAIEat extends TESEntityAIConsumeBase {
	public TESEntityAIEat(TESEntityNPC entity, TESFoods foods, int chance) {
		super(entity, foods, chance);
	}

	@Override
	public void consume() {
		ItemStack itemstack = theEntity.getHeldItem();
		Item item = itemstack.getItem();
		if (item instanceof ItemFood) {
			ItemFood food = (ItemFood) item;
			theEntity.heal(food.func_150905_g(itemstack));
		}
	}

	@Override
	public ItemStack createConsumable() {
		return foodPool.getRandomFood(rand);
	}

	@Override
	public void updateConsumeTick(int tick) {
		if (tick % 4 == 0) {
			theEntity.spawnFoodParticles();
			theEntity.playSound("random.eat", 0.5f + 0.5f * rand.nextInt(2), (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f);
		}
	}
}