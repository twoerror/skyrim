package tes.common.recipe;

import tes.common.item.other.TESItemDye;
import tes.common.item.other.TESItemRobes;
import net.minecraft.block.BlockColored;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;

public class TESRecipeRobesDye implements IRecipe {
	private final ItemArmor.ArmorMaterial robeMaterial;

	public TESRecipeRobesDye(ItemArmor.ArmorMaterial material) {
		robeMaterial = material;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack robes = null;
		int[] rgb = new int[3];
		int totalColor = 0;
		int coloredItems = 0;
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			if (itemstack.getItem() instanceof TESItemRobes && ((ItemArmor) itemstack.getItem()).getArmorMaterial() == robeMaterial) {
				if (robes != null) {
					return null;
				}
				robes = itemstack.copy();
				robes.stackSize = 1;
				if (!TESItemRobes.areRobesDyed(robes)) {
					continue;
				}
				int robeColor = TESItemRobes.getRobesColor(robes);
				float r = (robeColor >> 16 & 0xFF) / 255.0f;
				float g = (robeColor >> 8 & 0xFF) / 255.0f;
				float b = (robeColor & 0xFF) / 255.0f;
				totalColor = (int) (totalColor + Math.max(r, Math.max(g, b)) * 255.0f);
				rgb[0] = (int) (rgb[0] + r * 255.0f);
				rgb[1] = (int) (rgb[1] + g * 255.0f);
				rgb[2] = (int) (rgb[2] + b * 255.0f);
				++coloredItems;
				continue;
			}
			int dye = TESItemDye.isItemDye(itemstack);
			if (dye == -1) {
				return null;
			}
			float[] dyeColors = EntitySheep.fleeceColorTable[BlockColored.func_150031_c(dye)];
			int r = (int) (dyeColors[0] * 255.0f);
			int g = (int) (dyeColors[1] * 255.0f);
			int b = (int) (dyeColors[2] * 255.0f);
			totalColor += Math.max(r, Math.max(g, b));
			rgb[0] = rgb[0] + r;
			rgb[1] = rgb[1] + g;
			rgb[2] = rgb[2] + b;
			++coloredItems;
		}
		if (robes == null) {
			return null;
		}
		int r = rgb[0] / coloredItems;
		int g = rgb[1] / coloredItems;
		int b = rgb[2] / coloredItems;
		float averageColor = (float) totalColor / coloredItems;
		float maxColor = Math.max(r, Math.max(g, b));
		r = (int) (r * averageColor / maxColor);
		g = (int) (g * averageColor / maxColor);
		b = (int) (b * averageColor / maxColor);
		int color = (r << 16) + (g << 8) + b;
		TESItemRobes.setRobesColor(robes, color);
		return robes;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 10;
	}

	@Override
	public boolean matches(InventoryCrafting inv, World world) {
		ItemStack robes = null;
		Collection<ItemStack> dyes = new ArrayList<>();
		for (int i = 0; i < inv.getSizeInventory(); ++i) {
			ItemStack itemstack = inv.getStackInSlot(i);
			if (itemstack == null) {
				continue;
			}
			if (itemstack.getItem() instanceof TESItemRobes && ((ItemArmor) itemstack.getItem()).getArmorMaterial() == robeMaterial) {
				if (robes != null) {
					return false;
				}
				robes = itemstack;
				continue;
			}
			if (TESItemDye.isItemDye(itemstack) == -1) {
				return false;
			}
			dyes.add(itemstack);
		}
		return robes != null && !dyes.isEmpty();
	}
}