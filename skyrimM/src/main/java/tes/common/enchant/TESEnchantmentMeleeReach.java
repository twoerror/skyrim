package tes.common.enchant;

import tes.common.item.TESWeaponStats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentMeleeReach extends TESEnchantment {
	private final float reachFactor;

	@SuppressWarnings("unused")
	public TESEnchantmentMeleeReach(String s, float reach) {
		super(s, TESEnchantmentType.MELEE);
		reachFactor = reach;
		valueModifier = reachFactor;
	}

	@Override
	public boolean canApply(ItemStack itemstack, boolean considering) {
		if (super.canApply(itemstack, considering)) {
			float reach = TESWeaponStats.getMeleeReachFactor(itemstack);
			reach *= reachFactor;
			return reach <= TESWeaponStats.MAX_MODIFIABLE_REACH;
		}
		return false;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.meleeReach.desc", formatMultiplicative(reachFactor));
	}

	@Override
	public boolean isBeneficial() {
		return reachFactor >= 1.0F;
	}

	public float getReachFactor() {
		return reachFactor;
	}
}