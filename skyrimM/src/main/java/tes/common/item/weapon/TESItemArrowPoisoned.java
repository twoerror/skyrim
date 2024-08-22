package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseArrowPoisoned;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;

public class TESItemArrowPoisoned extends Item {
	public TESItemArrowPoisoned() {
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseArrowPoisoned());
	}
}