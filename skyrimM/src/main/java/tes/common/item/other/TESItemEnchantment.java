package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import tes.common.enchant.TESEnchantment;
import net.minecraft.item.Item;

public class TESItemEnchantment extends Item {
	private final TESEnchantment theEnchant;

	public TESItemEnchantment(TESEnchantment ench) {
		setCreativeTab(TESCreativeTabs.TAB_MATERIALS);
		theEnchant = ench;
	}

	public TESEnchantment getTheEnchant() {
		return theEnchant;
	}
}