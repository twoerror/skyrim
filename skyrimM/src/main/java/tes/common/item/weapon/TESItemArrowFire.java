package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseArrowFire;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;

public class TESItemArrowFire extends Item {
	public TESItemArrowFire() {
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseArrowFire());
	}
}