package tes.common.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESTradeEntries;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.utils.TESTradeEntry;
import tes.common.entity.other.utils.TESTradeSellResult;
import tes.common.inventory.TESContainerTrade;
import tes.common.item.other.TESItemCoin;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class TESPacketSell implements IMessage {
	@Override
	public void fromBytes(ByteBuf data) {
	}

	@Override
	public void toBytes(ByteBuf data) {
	}

	public static class Handler implements IMessageHandler<TESPacketSell, IMessage> {
		@Override
		public IMessage onMessage(TESPacketSell packet, MessageContext context) {
			EntityPlayerMP entityplayer = context.getServerHandler().playerEntity;
			Container container = entityplayer.openContainer;
			if (container instanceof TESContainerTrade) {
				TESContainerTrade tradeContainer = (TESContainerTrade) container;
				TESEntityNPC trader = tradeContainer.getTheTraderNPC();
				IInventory invSellOffer = tradeContainer.getTradeInvSellOffer();
				Map<TESTradeEntry, Integer> tradesUsed = new HashMap<>();
				int totalCoins = 0;
				for (int i = 0; i < invSellOffer.getSizeInventory(); ++i) {
					TESTradeSellResult sellResult;
					ItemStack itemstack = invSellOffer.getStackInSlot(i);
					if (itemstack == null || (sellResult = TESTradeEntries.getItemSellResult(itemstack, trader)) == null) {
						continue;
					}
					int tradeIndex = sellResult.getTradeIndex();
					int value = sellResult.getTotalSellValue();
					int itemsSold = sellResult.getItemsSold();
					TESTradeEntry[] sellTrades = trader.getTraderInfo().getSellTrades();
					TESTradeEntry trade = null;
					if (sellTrades != null) {
						trade = sellTrades[tradeIndex];
					}
					totalCoins += value;
					if (trade != null) {
						tradesUsed.merge(trade, value, Integer::sum);
					}
					itemstack.stackSize -= itemsSold;
					if (itemstack.stackSize > 0) {
						continue;
					}
					invSellOffer.setInventorySlotContents(i, null);
				}
				if (totalCoins > 0) {
					for (Map.Entry<TESTradeEntry, Integer> e : tradesUsed.entrySet()) {
						TESTradeEntry trade = e.getKey();
						int value = e.getValue();
						trader.getTraderInfo().onTrade(entityplayer, trade, TESTradeEntries.TradeType.WE_CAN_SELL, value);
					}
					TESItemCoin.giveCoins(totalCoins, entityplayer);
					if (totalCoins >= 1000) {
						TESLevelData.getData(entityplayer).addAchievement(TESAchievement.earnManyCoins);
					}
					trader.playTradeSound();
				}
			}
			return null;
		}
	}
}