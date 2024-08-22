package tes.common.enchant;

import tes.common.item.TESWeaponStats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentKnockback extends TESEnchantment {
	private final int knockback;

	@SuppressWarnings("unused")
	public TESEnchantmentKnockback(String s, int i) {
		super(s, new TESEnchantmentType[]{TESEnchantmentType.MELEE, TESEnchantmentType.THROWING_AXE});
		knockback = i;
		valueModifier = (knockback + 2) / 2.0F;
	}

	@Override
	public boolean canApply(ItemStack itemstack, boolean considering) {
		return super.canApply(itemstack, considering) && TESWeaponStats.getBaseExtraKnockback(itemstack) + knockback <= TESWeaponStats.MAX_MODIFIABLE_KNOCKBACK;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.knockback.desc", formatAdditiveInt(knockback));
	}

	@Override
	public boolean isBeneficial() {
		return knockback >= 0;
	}

	public int getKnockback() {
		return knockback;
	}
}