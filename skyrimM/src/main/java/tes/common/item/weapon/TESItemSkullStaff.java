package tes.common.item.weapon;

import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;

public class TESItemSkullStaff extends TESItemSword {
	public TESItemSkullStaff() {
		super(ToolMaterial.WOOD);
	}

	@Override
	public boolean getIsRepairable(ItemStack itemstack, ItemStack repairItem) {
		return repairItem.getItem() == Items.skull;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return null;
	}
}