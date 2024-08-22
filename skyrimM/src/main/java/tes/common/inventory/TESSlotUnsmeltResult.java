package tes.common.inventory;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class TESSlotUnsmeltResult extends TESSlotProtected {
	public TESSlotUnsmeltResult(IInventory inv, int i, int j, int k) {
		super(inv, i, j, k);
	}

	@Override
	public void onPickupFromSlot(EntityPlayer entityplayer, ItemStack itemstack) {
		super.onPickupFromSlot(entityplayer, itemstack);
		if (!entityplayer.worldObj.isRemote) {
			TESLevelData.getData(entityplayer).addAchievement(TESAchievement.unsmelt);
		}
	}
}