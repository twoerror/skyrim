package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.item.other.TESItemMug;
import tes.common.recipe.TESRecipeBrewing;
import tes.common.tileentity.TESTileEntityBarrel;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESSlotBarrel extends Slot {
	private final TESTileEntityBarrel theBarrel;
	private boolean isWater;

	public TESSlotBarrel(TESTileEntityBarrel inv, int i, int j, int k) {
		super(inv, i, j, k);
		theBarrel = inv;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		if (getSlotIndex() > 5) {
			return TESItemMug.getBarrelGuiEmptyBucketSlotIcon();
		}
		return null;
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return theBarrel.getBarrelMode() == 0 && (!isWater || TESRecipeBrewing.isWaterSource(itemstack));
	}

	public void setWaterSource() {
		isWater = true;
	}
}