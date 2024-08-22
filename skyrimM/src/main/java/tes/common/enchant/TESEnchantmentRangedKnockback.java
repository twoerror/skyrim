package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentRangedKnockback extends TESEnchantment {
	private final int knockback;

	@SuppressWarnings("unused")
	public TESEnchantmentRangedKnockback(String s, int i) {
		super(s, TESEnchantmentType.RANGED_LAUNCHER);
		knockback = i;
		valueModifier = (knockback + 2) / 2.0F;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.rangedKnockback.desc", formatAdditiveInt(knockback));
	}

	@Override
	public boolean isBeneficial() {
		return knockback >= 0;
	}

	public int getKnockback() {
		return knockback;
	}
}