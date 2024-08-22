package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.dispense.TESDispenseCrossbowBolt;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;

public class TESItemCrossbowBolt extends Item {
	private boolean isPoisoned;

	public TESItemCrossbowBolt() {
		setCreativeTab(TESCreativeTabs.TAB_COMBAT);
		BlockDispenser.dispenseBehaviorRegistry.putObject(this, new TESDispenseCrossbowBolt(this));
	}

	public TESItemCrossbowBolt setPoisoned() {
		isPoisoned = true;
		return this;
	}

	public boolean isPoisoned() {
		return isPoisoned;
	}
}