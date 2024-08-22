package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.item.other.TESItemMug;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESSlotBarrelResult extends Slot {
	public TESSlotBarrelResult(IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
	}

	@Override
	public boolean canTakeStack(EntityPlayer entityplayer) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		if (getSlotIndex() > 5) {
			return TESItemMug.getBarrelGuiEmptyMugSlotIcon();
		}
		return null;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return false;
	}
}