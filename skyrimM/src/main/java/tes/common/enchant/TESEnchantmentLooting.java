package tes.common.enchant;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentLooting extends TESEnchantment {
	private final int lootLevel;

	@SuppressWarnings("unused")
	public TESEnchantmentLooting(String s, int level) {
		super(s, new TESEnchantmentType[]{TESEnchantmentType.TOOL, TESEnchantmentType.MELEE});
		lootLevel = level;
		valueModifier = 1.0F + lootLevel;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.looting.desc", formatAdditiveInt(lootLevel));
	}

	@Override
	public boolean isBeneficial() {
		return true;
	}

	@Override
	public boolean isCompatibleWith(TESEnchantment other) {
		return super.isCompatibleWith(other) && !(other instanceof TESEnchantmentSilkTouch);
	}

	public int getLootLevel() {
		return lootLevel;
	}
}