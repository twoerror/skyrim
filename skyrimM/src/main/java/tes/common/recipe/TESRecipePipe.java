package tes.common.recipe;

import tes.common.database.TESItems;
import tes.common.item.other.TESItemDye;
import tes.common.item.other.TESItemPipe;
import net.minecraft.block.BlockColored;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class TESRecipePipe implements IRecipe {
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack pipe = null;
		ItemStack dye = null;
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			if (itemstack.getItem() == TESItems.pipe) {
				if (pipe != null) {
					return null;
				}
				pipe = itemstack;
				continue;
			}
			if (itemstack.getItem() == Items.iron_ingot) {
				dye = itemstack;
				continue;
			}
			if (TESItemDye.isItemDye(itemstack) == -1) {
				return null;
			}
			dye = itemstack;
		}
		if (pipe != null && dye != null) {
			int itemDamage = pipe.getItemDamage();
			int smokeType = dye.getItem() == Items.iron_ingot ? 16 : BlockColored.func_150031_c(TESItemDye.isItemDye(dye));
			ItemStack result = new ItemStack(TESItems.pipe);
			result.setItemDamage(itemDamage);
			TESItemPipe.setSmokeColor(result, smokeType);
			return result;
		}
		return null;
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
		ItemStack pipe = null;
		ItemStack dye = null;
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			if (itemstack.getItem() == TESItems.pipe) {
				if (pipe != null) {
					return false;
				}
				pipe = itemstack;
				continue;
			}
			if (itemstack.getItem() == Items.iron_ingot) {
				dye = itemstack;
				continue;
			}
			if (TESItemDye.isItemDye(itemstack) == -1) {
				return false;
			}
			dye = itemstack;
		}
		return pipe != null && dye != null;
	}
}