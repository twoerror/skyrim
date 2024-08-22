package tes.common.entity.other.utils;

import net.minecraft.item.ItemStack;

public class TESTradeSellResult {
	private final int tradeIndex;
	private final int tradesMade;
	private final int itemsSold;
	private final int totalSellValue;

	public TESTradeSellResult(int index, TESTradeEntry trade, ItemStack sellItem) {
		ItemStack tradeItem = trade.createTradeItem();
		tradeIndex = index;
		int tradeValue = trade.getCost();
		int tradeStackSize = tradeItem.stackSize;
		tradesMade = trade.isAvailable() ? sellItem.stackSize / tradeStackSize : 0;
		itemsSold = tradesMade * tradeItem.stackSize;
		totalSellValue = tradesMade * tradeValue;
	}

	public int getTradeIndex() {
		return tradeIndex;
	}

	public int getTradesMade() {
		return tradesMade;
	}

	public int getItemsSold() {
		return itemsSold;
	}

	public int getTotalSellValue() {
		return totalSellValue;
	}
}