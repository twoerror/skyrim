package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentToolSpeed extends TESEnchantment {
	private final float speedFactor;

	@SuppressWarnings("unused")
	public TESEnchantmentToolSpeed(String s, float speed) {
		super(s, new TESEnchantmentType[]{TESEnchantmentType.TOOL, TESEnchantmentType.SHEARS});
		speedFactor = speed;
		valueModifier = speedFactor;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.toolSpeed.desc", formatMultiplicative(speedFactor));
	}

	@Override
	public boolean isBeneficial() {
		return speedFactor >= 1.0F;
	}

	public float getSpeedFactor() {
		return speedFactor;
	}
}