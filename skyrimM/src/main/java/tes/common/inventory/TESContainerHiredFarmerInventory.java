package tes.common.inventory;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.info.TESHireableInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class TESContainerHiredFarmerInventory extends Container {
	private final TESEntityNPC theNPC;

	public TESContainerHiredFarmerInventory(InventoryPlayer inv, TESEntityNPC entity) {
		int i;
		theNPC = entity;
		addSlotToContainer(new TESSlotSeeds(theNPC.getHireableInfo().getHiredInventory(), 0, 80, 21, theNPC.worldObj));
		for (i = 0; i < 2; ++i) {
			addSlotToContainer(new TESSlotProtected(theNPC.getHireableInfo().getHiredInventory(), i + 1, 71 + i * 18, 47));
		}
		addSlotToContainer(new TESSlotBonemeal(theNPC.getHireableInfo().getHiredInventory(), 3, 123, 34));
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 79 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + i * 18, 137));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return theNPC != null && theNPC.isEntityAlive() && theNPC.getHireableInfo().isActive() && theNPC.getHireableInfo().getHiringPlayer() == entityplayer && theNPC.getHireableInfo().getHiredTask() == TESHireableInfo.Task.FARMER && entityplayer.getDistanceSqToEntity(theNPC) <= 144.0;
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		super.onContainerClosed(entityplayer);
		if (!theNPC.worldObj.isRemote) {
			theNPC.getHireableInfo().sendClientPacket(true);
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < 4) {
				if (!mergeItemStack(itemstack1, 4, 40, true)) {
					return null;
				}
			} else if (((Slot) inventorySlots.get(0)).isItemValid(itemstack1) && !mergeItemStack(itemstack1, 0, 1, false) || ((Slot) inventorySlots.get(3)).isItemValid(itemstack1) && !mergeItemStack(itemstack1, 3, 4, false)) {
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
}