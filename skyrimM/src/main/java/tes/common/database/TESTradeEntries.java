package tes.common.database;

import tes.common.TESConfig;
import tes.common.enchant.TESEnchantmentHelper;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.entity.other.utils.TESTradeEntryBarrel;
import tes.common.entity.other.utils.TESTradeSellResult;
import tes.common.item.other.TESItemMug;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.util.*;

@SuppressWarnings({"ConstantMathCall", "unused"})
public class TESTradeEntries {
	
	/* Зарегулированные пропорциональные цены */
	private static final int VALYRIAN_INTES = 512;
	private static final int GOLD_INTES = 128;
	private static final int SILVER_INTES = 32;
	private static final int DIAMOND = 128;
	private static final int EMERALD = 96;
	private static final int AMBER = 64;
	private static final int OPAL = 64;
	private static final int RUBY = 64;
	private static final int SAPPHIRE = 64;
	private static final int TOPAZ = 64;
	private static final int AMETHYST = 64;

	static {	}

	private final TESTradeEntries.TradeType tradeType;
	private final TESTradeEntry[] tradeEntries;

	private TESItemMug.Vessel[] drinkVessels;

	@SuppressWarnings("WeakerAccess")
	public TESTradeEntries(TESTradeEntries.TradeType t, List<TESTradeEntry> list) {
		TESTradeEntry[] arr = new TESTradeEntry[list.size()];
		arr = list.toArray(arr);
		tradeType = t;
		tradeEntries = arr;
	}

	public static TESTradeSellResult getItemSellResult(ItemStack itemstack, TESEntityNPC trader) {
		TESTradeEntry[] sellTrades = trader.getTraderInfo().getSellTrades();
		if (sellTrades != null) {
			for (int index = 0; index < sellTrades.length; ++index) {
				TESTradeEntry trade = sellTrades[index];
				if (trade != null && trade.matches(itemstack)) {
					return new TESTradeSellResult(index, trade, itemstack);
				}
			}
		}
		return null;
	}

	public TESTradeEntry[] getRandomTrades(Random random) {
		int numTrades = 3 + random.nextInt(3) + random.nextInt(3) + random.nextInt(3);
		if (numTrades > tradeEntries.length) {
			numTrades = tradeEntries.length;
		}
		TESTradeEntry[] tempTrades = new TESTradeEntry[tradeEntries.length];
		System.arraycopy(tradeEntries, 0, tempTrades, 0, tradeEntries.length);
		List<TESTradeEntry> tempTradesAsList = Arrays.asList(tempTrades);
		Collections.shuffle(tempTradesAsList);
		tempTrades = tempTradesAsList.toArray(tempTrades);
		TESTradeEntry[] trades = new TESTradeEntry[numTrades];

		for (int i = 0; i < trades.length; ++i) {
			ItemStack tradeItem = tempTrades[i].createTradeItem();
			int originalCost = tempTrades[i].getCost();
			float tradeCost = originalCost;
			boolean frozenPrice = tempTrades[i].isFrozenPrice();
			int tradeCostI;
			if (tradeItem.getItem() instanceof TESItemMug && ((TESItemMug) tradeItem.getItem()).isBrewable() && tradeItem.getItemDamage() == 9999) {
				tradeCostI = 1 + random.nextInt(3);
				tradeItem.setItemDamage(tradeCostI);
				tradeCost *= TESItemMug.getFoodStrength(tradeItem);
			}
			if (drinkVessels != null && TESItemMug.isItemFullDrink(tradeItem)) {
				TESItemMug.Vessel v = drinkVessels[random.nextInt(drinkVessels.length)];
				TESItemMug.setVessel(tradeItem, v, true);
				tradeCost += v.getExtraPrice();
			}
			if (TESConfig.enchantingTES && tradeType == TESTradeEntries.TradeType.WE_CAN_BUY) {
				boolean skilful = random.nextInt(3) == 0;
				TESEnchantmentHelper.applyRandomEnchantments(tradeItem, random, skilful, false);
				tradeCost *= TESEnchantmentHelper.calcTradeValueFactor(tradeItem);
			}
			tradeCost *= MathHelper.randomFloatClamp(random, 0.75F, 1.25F);
			tradeCost = Math.max(tradeCost, 1.0F);
			tradeCostI = Math.round(tradeCost);
			tradeCostI = Math.max(tradeCostI, 1);
			trades[i] = new TESTradeEntry(tradeItem, frozenPrice ? originalCost : tradeCostI);
		}
		return trades;
	}

	@SuppressWarnings("WeakerAccess")
	public TESTradeEntries setVessels(TESFoods foods) {
		return setVessels(foods.getDrinkVessels());
	}

	@SuppressWarnings("WeakerAccess")
	public TESTradeEntries setVessels(TESItemMug.Vessel... v) {
		if (tradeType != TESTradeEntries.TradeType.WE_CAN_BUY) {
			throw new IllegalArgumentException("Cannot set the vessel types for a sell list");
		}
		drinkVessels = v;
		return this;
	}

	public TESTradeEntry[] getTradeEntries() {
		return tradeEntries;
	}

	public enum TradeType {
		WE_CAN_BUY, WE_CAN_SELL
	}
}