package tes.common.item.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.ItemFood;

public class TESItemFood extends ItemFood {
	public TESItemFood(int healAmount, float saturation, boolean canWolfEat) {
		super(healAmount, saturation, canWolfEat);
		setCreativeTab(TESCreativeTabs.TAB_FOOD);
	}
}