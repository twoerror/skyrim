package tes.common.inventory;

import tes.common.TESLevelData;
import tes.common.database.TESInvasions;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.entity.other.iface.TESUnitTradeable;
import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESContainerUnitTrade extends Container {
	private final TESHireableBase theUnitTrader;
	private final TESEntityNPC theLivingTrader;
	private final int alignmentRewardSlots;

	public TESContainerUnitTrade(EntityPlayer entityplayer, TESHireableBase trader, World world) {
		int i;
		theUnitTrader = trader;
		theLivingTrader = (TESEntityNPC) theUnitTrader;
		TESFaction traderFaction = theLivingTrader.getFaction();
		ItemStack reward = null;
		if (theUnitTrader instanceof TESUnitTradeable) {
			TESInvasions conquestType = ((TESUnitTradeable) theUnitTrader).getWarhorn();
			reward = conquestType == null ? null : conquestType.createConquestHorn();
		}
		boolean hasReward = reward != null;
		alignmentRewardSlots = hasReward ? 1 : 0;
		IInventory alignmentRewardInv = new InventoryBasic("specialItem", false, alignmentRewardSlots);
		if (hasReward) {
			addSlotToContainer(new TESSlotAlignmentReward(this, alignmentRewardInv, 0, 174, 78, theUnitTrader, reward.copy()));
			if (!world.isRemote && TESLevelData.getData(entityplayer).getAlignment(traderFaction) >= 1500.0f) {
				alignmentRewardInv.setInventorySlotContents(0, reward.copy());
			}
		}
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(entityplayer.inventory, j + i * 9 + 9, 30 + j * 18, 174 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(entityplayer.inventory, i, 30 + i * 18, 232));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return theLivingTrader != null && entityplayer.getDistanceToEntity(theLivingTrader) <= 12.0 && theLivingTrader.isEntityAlive() && theLivingTrader.getAttackTarget() == null && theUnitTrader.canTradeWith(entityplayer);
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (i < alignmentRewardSlots ? !mergeItemStack(itemstack1, alignmentRewardSlots, 36 + alignmentRewardSlots, true) : i < 27 + alignmentRewardSlots ? !mergeItemStack(itemstack1, 27 + alignmentRewardSlots, 36 + alignmentRewardSlots, false) : !mergeItemStack(itemstack1, alignmentRewardSlots, 27 + alignmentRewardSlots, false)) {
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

	public TESHireableBase getTheUnitTrader() {
		return theUnitTrader;
	}

	public TESEntityNPC getTheLivingTrader() {
		return theLivingTrader;
	}

	public int getAlignmentRewardSlots() {
		return alignmentRewardSlots;
	}
}