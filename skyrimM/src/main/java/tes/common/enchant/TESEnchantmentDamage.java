package tes.common.enchant;

import tes.common.item.TESWeaponStats;
import tes.common.item.weapon.TESItemThrowingAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentDamage extends TESEnchantment {
	private final float baseDamageBoost;

	@SuppressWarnings("unused")
	public TESEnchantmentDamage(String s, float boost) {
		super(s, new TESEnchantmentType[]{TESEnchantmentType.MELEE, TESEnchantmentType.THROWING_AXE});
		baseDamageBoost = boost;
		if (baseDamageBoost >= 0.0F) {
			valueModifier = (7.0F + baseDamageBoost * 5.0F) / 7.0F;
		} else {
			valueModifier = (7.0F + baseDamageBoost) / 7.0F;
		}
	}

	@Override
	public boolean canApply(ItemStack itemstack, boolean considering) {
		if (super.canApply(itemstack, considering)) {
			float dmg = TESWeaponStats.getMeleeDamageBonus(itemstack);
			dmg += baseDamageBoost;
			return dmg > 0.0F;
		}
		return false;
	}

	public float getBaseDamageBoost() {
		return baseDamageBoost;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		if (itemstack != null && itemstack.getItem() instanceof TESItemThrowingAxe) {
			return StatCollector.translateToLocalFormatted("tes.enchant.damage.desc.throw", formatAdditive(baseDamageBoost));
		}
		return StatCollector.translateToLocalFormatted("tes.enchant.damage.desc", formatAdditive(baseDamageBoost));
	}

	@Override
	public boolean isBeneficial() {
		return baseDamageBoost >= 0.0F;
	}
}