package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.item.TESWeaponStats;
import tes.common.util.TESCommonIcons;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESSlotMeleeWeapon extends Slot {
	public TESSlotMeleeWeapon(IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		return TESCommonIcons.iconMeleeWeapon;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return TESWeaponStats.isMeleeWeapon(itemstack);
	}
}