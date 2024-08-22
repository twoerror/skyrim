package tes.common.item.other;

import tes.common.TESLevelData;
import tes.common.faction.TESFaction;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class TESItemMugWarlockDraught extends TESItemMug {
	public TESItemMugWarlockDraught() {
		super(0.0f);
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!shouldApplyPotionEffects(entityplayer)) {
			ItemStack result = super.onEaten(itemstack, world, entityplayer);
			if (!world.isRemote) {
				entityplayer.addPotionEffect(new PotionEffect(Potion.poison.id, 100));
			}
			return result;
		}
		return super.onEaten(itemstack, world, entityplayer);
	}

	@Override
	protected boolean shouldApplyPotionEffects(EntityPlayer entityplayer) {
		return TESLevelData.getData(entityplayer).getAlignment(TESFaction.EMPIRE) > 0.0f;
	}
}
