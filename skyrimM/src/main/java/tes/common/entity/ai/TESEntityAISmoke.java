package tes.common.entity.ai;

import tes.common.database.TESItems;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.inanimate.TESEntitySmokeRing;
import tes.common.item.other.TESItemPipe;
import net.minecraft.item.ItemStack;

public class TESEntityAISmoke extends TESEntityAIConsumeBase {
	public TESEntityAISmoke(TESEntityNPC entity, int chance) {
		super(entity, null, chance);
	}

	@Override
	public void consume() {
		TESEntitySmokeRing smoke = new TESEntitySmokeRing(theEntity.worldObj, theEntity);
		int color = 0;
		ItemStack itemstack = theEntity.getHeldItem();
		if (itemstack != null && itemstack.getItem() instanceof TESItemPipe) {
			color = TESItemPipe.getSmokeColor(itemstack);
		}
		smoke.setSmokeColour(color);
		theEntity.worldObj.spawnEntityInWorld(smoke);
		theEntity.playSound("tes:item.puff", 1.0f, (rand.nextFloat() - rand.nextFloat()) * 0.2f + 1.0f);
		theEntity.heal(2.0f);
	}

	@Override
	public ItemStack createConsumable() {
		return new ItemStack(TESItems.pipe);
	}

	@Override
	public void updateConsumeTick(int tick) {
	}
}