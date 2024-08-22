package tes.common.item;

import tes.common.item.other.TESItemCoin;
import tes.common.item.other.TESItemGem;
import tes.common.item.other.TESItemRing;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TESValuableItems {
	private TESValuableItems() {
	}

	public static boolean canMagpieSteal(ItemStack itemstack) {
		Item item = itemstack.getItem();
		return item instanceof TESItemCoin || item instanceof TESItemRing || item instanceof TESItemGem;
	}
}