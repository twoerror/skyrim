package tes.common.inventory;

import tes.common.item.other.TESItemPouch;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TESSlotPouch extends Slot {
	public TESSlotPouch(IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return !(itemstack.getItem() instanceof TESItemPouch) && super.isItemValid(itemstack);
	}
}