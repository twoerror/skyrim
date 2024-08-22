package tes.common.enchant;

import net.minecraft.util.DamageSource;

public abstract class TESEnchantmentProtectionSpecial extends TESEnchantment {
	protected final int protectLevel;

	protected TESEnchantmentProtectionSpecial(String s, TESEnchantmentType type, int level) {
		super(s, type);
		protectLevel = level;
		valueModifier = (2.0F + protectLevel) / 2.0F;
	}

	protected TESEnchantmentProtectionSpecial(String s, int level) {
		this(s, TESEnchantmentType.ARMOR, level);
	}

	protected abstract int calcIntProtection();

	public int calcSpecialProtection(DamageSource source) {
		if (source.canHarmInCreative()) {
			return 0;
		}
		if (protectsAgainst(source)) {
			return calcIntProtection();
		}
		return 0;
	}

	@Override
	public boolean isBeneficial() {
		return true;
	}

	@Override
	public boolean isCompatibleWith(TESEnchantment other) {
		return super.isCompatibleWith(other) && (!(other instanceof TESEnchantmentProtectionSpecial) || isCompatibleWithOtherProtection() || ((TESEnchantmentProtectionSpecial) other).isCompatibleWithOtherProtection());
	}

	protected boolean isCompatibleWithOtherProtection() {
		return false;
	}

	protected abstract boolean protectsAgainst(DamageSource paramDamageSource);

	public int getProtectLevel() {
		return protectLevel;
	}
}