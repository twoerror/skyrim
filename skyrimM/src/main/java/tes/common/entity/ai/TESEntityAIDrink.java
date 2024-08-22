package tes.common.entity.ai;

import tes.TES;
import tes.common.database.TESFoods;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESBartender;
import tes.common.entity.other.utils.TESEntityUtils;
import tes.common.item.other.TESItemMug;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.util.List;

public class TESEntityAIDrink extends TESEntityAIConsumeBase {
	public TESEntityAIDrink(TESEntityNPC entity, TESFoods foods, int chance) {
		super(entity, foods, chance);
	}

	@Override
	public void consume() {
		ItemStack itemstack = theEntity.getHeldItem();
		Item item = itemstack.getItem();
		if (item instanceof TESItemMug) {
			TESItemMug drink = (TESItemMug) item;
			drink.applyToNPC(theEntity, itemstack);
			if (drink.getAlcoholicity() > 0.0f && TESEntityUtils.canSmokeDrink(theEntity) && !theEntity.isDrunkard() && rand.nextInt(3) == 0) {
				double range = 12.0;
				IEntitySelector selectNonEnemyBartenders = new EntitySelectorImpl(theEntity);
				List<TESBartender> nearbyBartenders = theEntity.worldObj.selectEntitiesWithinAABB(TESBartender.class, theEntity.boundingBox.expand(range, range, range), selectNonEnemyBartenders);
				if (!nearbyBartenders.isEmpty()) {
					int drunkTime = MathHelper.getRandomIntegerInRange(rand, 30, 1500);
					theEntity.getFamilyInfo().setDrunkTime(drunkTime * 20);
				}
			}
		}
	}

	@Override
	public ItemStack createConsumable() {
		ItemStack drink = foodPool.getRandomFood(rand);
		Item item = drink.getItem();
		if (item instanceof TESItemMug && ((TESItemMug) item).isBrewable()) {
			TESItemMug.setStrengthMeta(drink, 1 + rand.nextInt(3));
		}
		return drink;
	}

	@Override
	public int getConsumeTime() {
		int time = super.getConsumeTime();
		if (theEntity.isDrunkard()) {
			time *= 1 + rand.nextInt(4);
		}
		return time;
	}

	@Override
	public boolean shouldConsume() {
		return theEntity.isDrunkard() && rand.nextInt(100) == 0 || super.shouldConsume();
	}

	@Override
	public void updateConsumeTick(int tick) {
		if (tick % 4 == 0) {
			theEntity.playSound("random.drink", 0.5f, rand.nextFloat() * 0.1f + 0.9f);
		}
	}

	private static class EntitySelectorImpl implements IEntitySelector {
		private final TESEntityNPC theEntity;

		private EntitySelectorImpl(TESEntityNPC theEntity) {
			this.theEntity = theEntity;
		}

		@Override
		public boolean isEntityApplicable(Entity entity) {
			return entity.isEntityAlive() && !TES.getNPCFaction(entity).isBadRelation(theEntity.getFaction());
		}
	}
}