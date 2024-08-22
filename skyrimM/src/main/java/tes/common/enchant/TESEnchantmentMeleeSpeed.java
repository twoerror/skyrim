package tes.common.enchant;

import tes.common.item.TESWeaponStats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentMeleeSpeed extends TESEnchantment {
	private final float speedFactor;

	@SuppressWarnings("unused")
	public TESEnchantmentMeleeSpeed(String s, float speed) {
		super(s, TESEnchantmentType.MELEE);
		speedFactor = speed;
		valueModifier = speedFactor;
	}

	@Override
	public boolean canApply(ItemStack itemstack, boolean considering) {
		if (super.canApply(itemstack, considering)) {
			float speed = TESWeaponStats.getMeleeSpeed(itemstack);
			speed *= speedFactor;
			return speed <= TESWeaponStats.MAX_MODIFIABLE_SPEED;
		}
		return false;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.meleeSpeed.desc", formatMultiplicative(speedFactor));
	}

	@Override
	public boolean isBeneficial() {
		return speedFactor >= 1.0F;
	}

	public float getSpeedFactor() {
		return speedFactor;
	}
}