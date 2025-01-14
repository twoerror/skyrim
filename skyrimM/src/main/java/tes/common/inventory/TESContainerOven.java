package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.tileentity.TESTileEntityOven;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;

public class TESContainerOven extends Container {
	private final TESTileEntityOven theOven;

	private int currentCookTime;
	private int ovenCookTime;
	private int currentItemFuelValue;

	public TESContainerOven(InventoryPlayer inv, TESTileEntityOven oven) {
		int i;
		theOven = oven;
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(oven, i, 8 + i * 18, 21));
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new SlotFurnace(inv.player, oven, i + 9, 8 + i * 18, 67));
		}
		addSlotToContainer(new Slot(oven, 18, 80, 111));
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 133 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + i * 18, 191));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		crafting.sendProgressBarUpdate(this, 0, theOven.getCurrentCookTime());
		crafting.sendProgressBarUpdate(this, 1, theOven.getOvenCookTime());
		crafting.sendProgressBarUpdate(this, 2, theOven.getCurrentItemFuelValue());
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return theOven.isUseableByPlayer(entityplayer);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (Object element : crafters) {
			ICrafting crafting = (ICrafting) element;
			if (currentCookTime != theOven.getCurrentCookTime()) {
				crafting.sendProgressBarUpdate(this, 0, theOven.getCurrentCookTime());
			}
			if (ovenCookTime != theOven.getOvenCookTime()) {
				crafting.sendProgressBarUpdate(this, 1, theOven.getOvenCookTime());
			}
			if (currentItemFuelValue == theOven.getCurrentItemFuelValue()) {
				continue;
			}
			crafting.sendProgressBarUpdate(this, 2, theOven.getCurrentItemFuelValue());
		}
		currentCookTime = theOven.getCurrentCookTime();
		ovenCookTime = theOven.getOvenCookTime();
		currentItemFuelValue = theOven.getCurrentItemFuelValue();
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i >= 9 && i < 18) {
				if (!mergeItemStack(itemstack1, 19, 55, true)) {
					return null;
				}
				slot.onSlotChange(itemstack1, itemstack);
			} else if (i >= 9 && i != 18 ? TESTileEntityOven.isCookResultAcceptable(FurnaceRecipes.smelting().getSmeltingResult(itemstack1)) ? !mergeItemStack(itemstack1, 0, 9, false) : TileEntityFurnace.isItemFuel(itemstack1) ? !mergeItemStack(itemstack1, 18, 19, false) : i < 46 ? !mergeItemStack(itemstack1, 46, 55, false) : i < 55 && !mergeItemStack(itemstack1, 19, 46, false) : !mergeItemStack(itemstack1, 19, 55, false)) {
				return null;
			}
			if (itemstack1.stackSize == 0) {
				slot.putStack(null);
			} else {
				slot.onSlotChanged();
			}
			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}
			slot.onPickupFromSlot(entityplayer, itemstack1);
		}
		return itemstack;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int i, int j) {
		if (i == 0) {
			theOven.setCurrentCookTime(j);
		}
		if (i == 1) {
			theOven.setOvenCookTime(j);
		}
		if (i == 2) {
			theOven.setCurrentItemFuelValue(j);
		}
	}
}