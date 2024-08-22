package tes.common.item.other;

import tes.common.util.TESReflection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.World;

public class TESItemMugPoppyMilk extends TESItemMug {
	public TESItemMugPoppyMilk(float f) {
		super(f);
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		ItemStack result = super.onEaten(itemstack, world, entityplayer);
		if (!world.isRemote) {
			for (Potion potion : Potion.potionTypes) {
				if (potion == null || !TESReflection.isBadEffect(potion)) {
					continue;
				}
				entityplayer.removePotionEffect(potion.id);
			}
		}
		return result;
	}
}