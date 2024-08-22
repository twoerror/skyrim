package tes.common.inventory;

import tes.common.database.TESTradeEntries;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.entity.other.utils.TESTradeSellResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESContainerTrade extends Container {
	private final IInventory tradeInvBuy = new InventoryBasic("trade", false, 9);
	private final IInventory tradeInvSell = new InventoryBasic("trade", false, 9);
	private final IInventory tradeInvSellOffer = new InventoryBasic("trade", false, 9);
	private final TESTradeable theTrader;
	private final World theWorld;
	private final TESEntityNPC theTraderNPC;

	public TESContainerTrade(InventoryPlayer inv, TESTradeable trader, World world) {
		int i;
		theTrader = trader;
		theTraderNPC = (TESEntityNPC) trader;
		theWorld = world;
		if (!world.isRemote) {
			updateAllTradeSlots();
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new TESSlotTrade(this, tradeInvBuy, i, 8 + i * 18, 40, theTraderNPC, TESTradeEntries.TradeType.WE_CAN_BUY));
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new TESSlotTrade(this, tradeInvSell, i, 8 + i * 18, 92, theTraderNPC, TESTradeEntries.TradeType.WE_CAN_SELL));
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(tradeInvSellOffer, i, 8 + i * 18, 141));
		}
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 188 + i * 18));
			}
		}
		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + i * 18, 246));
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting crafting) {
		super.addCraftingToCrafters(crafting);
		theTraderNPC.getTraderInfo().sendClientPacket((EntityPlayer) crafting);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return theTraderNPC != null && entityplayer.getDistanceToEntity(theTraderNPC) <= 12.0 && theTraderNPC.isEntityAlive() && theTraderNPC.getAttackTarget() == null && theTrader.canTradeWith(entityplayer);
	}

	@Override
	public void onContainerClosed(EntityPlayer entityplayer) {
		super.onContainerClosed(entityplayer);
		if (!theWorld.isRemote) {
			for (int i = 0; i < tradeInvSellOffer.getSizeInventory(); ++i) {
				ItemStack itemstack = tradeInvSellOffer.getStackInSlotOnClosing(i);
				if (itemstack == null) {
					continue;
				}
				entityplayer.dropPlayerItemWithRandomChoice(itemstack, false);
			}
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer entityplayer, int i) {
		ItemStack itemstack = null;
		Slot slot = (Slot) inventorySlots.get(i);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			TESTradeSellResult sellResult = TESTradeEntries.getItemSellResult(itemstack1, theTraderNPC);
			boolean sellable = sellResult != null && sellResult.getTradesMade() > 0;
			if (i < 9) {
				if (!mergeItemStack(itemstack1, 27, 63, true)) {
					return null;
				}
			} else if (i < 18 || (i < 27 ? !mergeItemStack(itemstack1, 27, 63, true) : sellable ? !mergeItemStack(itemstack1, 18, 27, false) : i < 54 ? !mergeItemStack(itemstack1, 54, 63, false) : i < 63 && !mergeItemStack(itemstack1, 27, 54, false))) {
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

	public void updateAllTradeSlots() {
		TESTradeEntry trade;
		int i;
		TESTradeEntry[] buyTrades = theTraderNPC.getTraderInfo().getBuyTrades();
		TESTradeEntry[] sellTrades = theTraderNPC.getTraderInfo().getSellTrades();
		if (buyTrades != null) {
			for (i = 0; i < tradeInvBuy.getSizeInventory(); ++i) {
				trade = null;
				if (i < buyTrades.length) {
					trade = buyTrades[i];
				}
				if (trade != null) {
					tradeInvBuy.setInventorySlotContents(i, trade.createTradeItem());
					continue;
				}
				tradeInvBuy.setInventorySlotContents(i, null);
			}
		}
		if (sellTrades != null) {
			for (i = 0; i < tradeInvSell.getSizeInventory(); ++i) {
				trade = null;
				if (i < sellTrades.length) {
					trade = sellTrades[i];
				}
				if (trade != null) {
					tradeInvSell.setInventorySlotContents(i, trade.createTradeItem());
					continue;
				}
				tradeInvSell.setInventorySlotContents(i, null);
			}
		}
	}

	public TESEntityNPC getTheTraderNPC() {
		return theTraderNPC;
	}

	public IInventory getTradeInvBuy() {
		return tradeInvBuy;
	}

	public IInventory getTradeInvSell() {
		return tradeInvSell;
	}

	public IInventory getTradeInvSellOffer() {
		return tradeInvSellOffer;
	}
}