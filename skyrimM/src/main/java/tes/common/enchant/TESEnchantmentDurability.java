package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentDurability extends TESEnchantment {
	private final float durabilityFactor;

	@SuppressWarnings("unused")
	public TESEnchantmentDurability(String s, float f) {
		super(s, TESEnchantmentType.BREAKABLE);
		durabilityFactor = f;
		valueModifier = durabilityFactor;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.durable.desc", formatMultiplicative(durabilityFactor));
	}

	@Override
	public boolean isBeneficial() {
		return durabilityFactor >= 1.0F;
	}

	public float getDurabilityFactor() {
		return durabilityFactor;
	}
}