package tes.common.inventory;

import tes.common.database.TESTradeEntries;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.item.other.TESItemCoin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TESSlotTrade extends TESSlotProtected {
	private final TESContainerTrade theContainer;
	private final TESEntityNPC theEntity;
	private final TESTradeEntries.TradeType tradeType;

	public TESSlotTrade(TESContainerTrade container, IInventory inv, int i, int j, int k, TESEntityNPC entity, TESTradeEntries.TradeType type) {
		super(inv, i, j, k);
		theContainer = container;
		theEntity = entity;
		tradeType = type;
	}

	@Override
	public boolean canTakeStack(EntityPlayer entityplayer) {
		if (tradeType == TESTradeEntries.TradeType.WE_CAN_BUY) {
			if (getTrade() != null && !getTrade().isAvailable()) {
				return false;
			}
			int coins = TESItemCoin.getInventoryValue(entityplayer, false);
			if (coins < cost()) {
				return false;
			}
		}
		return tradeType != TESTradeEntries.TradeType.WE_CAN_SELL && super.canTakeStack(entityplayer);
	}

	public int cost() {
		TESTradeEntry trade = getTrade();
		return trade == null ? 0 : trade.getCost();
	}

	public TESTradeEntry getTrade() {
		TESTradeEntry[] trades = null;
		if (tradeType == TESTradeEntries.TradeType.WE_CAN_BUY) {
			trades = theEntity.getTraderInfo().getBuyTrades();
		} else if (tradeType == TESTradeEntries.TradeType.WE_CAN_SELL) {
			trades = theEntity.getTraderInfo().getSellTrades();
		}
		if (trades == null) {
			return null;
		}
		int i = getSlotIndex();
		if (i >= 0 && i < trades.length) {
			return trades[i];
		}
		return null;
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityplayer, ItemStack itemstack) {
		if (tradeType == TESTradeEntries.TradeType.WE_CAN_BUY && !entityplayer.worldObj.isRemote) {
			TESItemCoin.takeCoins(cost(), entityplayer);
		}
		super.onPickupFromSlot(entityplayer, itemstack);
		if (tradeType == TESTradeEntries.TradeType.WE_CAN_BUY) {
			TESTradeEntry trade = getTrade();
			if (!entityplayer.worldObj.isRemote && trade != null) {
				putStack(trade.createTradeItem());
				((EntityPlayerMP) entityplayer).sendContainerToPlayer(theContainer);
				theEntity.getTraderInfo().onTrade(entityplayer, trade, TESTradeEntries.TradeType.WE_CAN_BUY, cost());
				theEntity.playTradeSound();
			}
		}
	}
}