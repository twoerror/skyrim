package tes.common.recipe;

import tes.TES;
import tes.common.database.TESItems;
import tes.common.item.other.TESItemFeatherDyed;
import tes.common.item.other.TESItemLeatherHat;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class TESRecipeLeatherHatFeather implements IRecipe {
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack hat = null;
		ItemStack feather = null;
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			if (itemstack.getItem() == TESItems.leatherHat && !TESItemLeatherHat.hasFeather(itemstack)) {
				if (hat != null) {
					return null;
				}
				hat = itemstack.copy();
				continue;
			}
			if (TES.isOreNameEqual(itemstack, "feather") || itemstack.getItem() == TESItems.featherDyed) {
				if (feather != null) {
					return null;
				}
				feather = itemstack.copy();
				continue;
			}
			return null;
		}
		if (hat == null || feather == null) {
			return null;
		}
		int featherColor = 16777215;
		if (feather.getItem() == TESItems.featherDyed) {
			featherColor = TESItemFeatherDyed.getFeatherColor(feather);
		}
		TESItemLeatherHat.setFeatherColor(hat, featherColor);
		return hat;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack hat = null;
		ItemStack feather = null;
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			if (itemstack.getItem() == TESItems.leatherHat && !TESItemLeatherHat.hasFeather(itemstack)) {
				if (hat != null) {
					return false;
				}
				hat = itemstack;
				continue;
			}
			if (TES.isOreNameEqual(itemstack, "feather") || itemstack.getItem() == TESItems.featherDyed) {
				if (feather != null) {
					return false;
				}
				feather = itemstack;
				continue;
			}
			return false;
		}
		return hat != null && feather != null;
	}
}