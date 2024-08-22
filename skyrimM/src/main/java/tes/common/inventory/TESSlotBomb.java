package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.block.other.TESBlockBomb;
import tes.common.util.TESCommonIcons;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESSlotBomb extends Slot {
	public TESSlotBomb(IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		return TESCommonIcons.iconBomb;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return itemstack != null && Block.getBlockFromItem(itemstack.getItem()) instanceof TESBlockBomb;
	}
}