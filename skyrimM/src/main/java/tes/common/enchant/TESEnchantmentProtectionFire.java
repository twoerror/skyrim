package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.StatCollector;

public class TESEnchantmentProtectionFire extends TESEnchantmentProtectionSpecial {
	@SuppressWarnings("unused")
	public TESEnchantmentProtectionFire(String s, int level) {
		super(s, level);
	}

	@Override
	protected int calcIntProtection() {
		return 1 + protectLevel;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.protectFire.desc", formatAdditiveInt(calcIntProtection()));
	}

	@Override
	public boolean protectsAgainst(DamageSource source) {
		return source.isFireDamage();
	}
}