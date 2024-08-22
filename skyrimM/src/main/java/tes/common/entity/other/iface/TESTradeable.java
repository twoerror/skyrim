package tes.common.entity.other.iface;

import tes.common.database.TESTradeEntries;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface TESTradeable {
	boolean canTradeWith(EntityPlayer var1);

	TESTradeEntries getBuyPool();

	TESTradeEntries getSellPool();

	void onPlayerTrade(EntityPlayer var1, TESTradeEntries.TradeType var2, ItemStack var3);

	interface Smith extends TESTradeable {
	}
}