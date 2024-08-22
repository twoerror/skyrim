package tes.common.entity.other.utils;

import tes.common.database.TESBlocks;
import tes.common.item.other.TESItemBarrel;
import tes.common.recipe.TESRecipeBrewing;
import tes.common.tileentity.TESTileEntityBarrel;
import net.minecraft.item.ItemStack;

public class TESTradeEntryBarrel extends TESTradeEntry {
	public TESTradeEntryBarrel(ItemStack itemstack, int cost) {
		super(itemstack, cost);
	}

	@Override
	public ItemStack createTradeItem() {
		ItemStack drinkItem = super.createTradeItem();
		ItemStack barrelItem = new ItemStack(TESBlocks.barrel);
		TESTileEntityBarrel barrel = new TESTileEntityBarrel();
		barrel.setInventorySlotContents(9, new ItemStack(drinkItem.getItem(), TESRecipeBrewing.BARREL_CAPACITY, drinkItem.getItemDamage()));
		barrel.setBarrelMode(2);
		TESItemBarrel.setBarrelDataFromTE(barrelItem, barrel);
		return barrelItem;
	}
}