package tes.common.item.other;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemPotion extends ItemPotion {
	@Override
	public boolean onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, int i, int j, int k, int side, float f, float f1, float f2) {
		return TESItemMug.tryPlaceMug(itemstack, entityplayer, world, i, j, k, side);
	}
}