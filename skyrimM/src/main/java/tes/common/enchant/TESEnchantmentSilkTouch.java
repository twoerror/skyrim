package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentSilkTouch extends TESEnchantment {
	@SuppressWarnings("unused")
	public TESEnchantmentSilkTouch(String s) {
		super(s, TESEnchantmentType.TOOL);
		valueModifier = 3.0F;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant." + enchantName + ".desc");
	}

	@Override
	public boolean isBeneficial() {
		return true;
	}

	@Override
	public boolean isCompatibleWith(TESEnchantment other) {
		return super.isCompatibleWith(other) && !(other instanceof TESEnchantmentLooting);
	}
}