package tes.common.inventory;

import tes.common.TESLevelData;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.faction.TESFaction;
import tes.common.item.other.TESItemCoin;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TESSlotAlignmentReward extends TESSlotProtected {
	public static final int REWARD_COST = 1000;

	private final TESContainerUnitTrade theContainer;
	private final TESHireableBase theTrader;
	private final TESEntityNPC theLivingTrader;
	private final ItemStack alignmentReward;

	public TESSlotAlignmentReward(TESContainerUnitTrade container, IInventory inv, int i, int j, int k, TESHireableBase entity, ItemStack item) {
		super(inv, i, j, k);
		theContainer = container;
		theTrader = entity;
		theLivingTrader = (TESEntityNPC) theTrader;
		alignmentReward = item.copy();
	}

	@Override
	public boolean canTakeStack(EntityPlayer entityplayer) {
		if (TESLevelData.getData(entityplayer).getAlignment(theTrader.getFaction()) < 1500.0f) {
			return false;
		}
		int coins = TESItemCoin.getInventoryValue(entityplayer, false);
		return coins >= REWARD_COST && super.canTakeStack(entityplayer);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityplayer, ItemStack itemstack) {
		TESFaction faction = theLivingTrader.getFaction();
		if (!entityplayer.worldObj.isRemote) {
			TESItemCoin.takeCoins(REWARD_COST, entityplayer);
			TESLevelData.getData(entityplayer).getFactionData(faction).takeConquestHorn();
			theLivingTrader.playTradeSound();
		}
		super.onPickupFromSlot(entityplayer, itemstack);
		if (!entityplayer.worldObj.isRemote) {
			ItemStack reward = alignmentReward.copy();
			putStack(reward);
			((EntityPlayerMP) entityplayer).sendContainerToPlayer(theContainer);
		}
	}
}