package tes.common.enchant;

import tes.common.database.TESMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class TESEnchantmentProtection extends TESEnchantment {
	private final int protectLevel;

	@SuppressWarnings("unused")
	public TESEnchantmentProtection(String s, int level) {
		this(s, TESEnchantmentType.ARMOR, level);
	}

	private TESEnchantmentProtection(String s, TESEnchantmentType type, int level) {
		super(s, type);
		protectLevel = level;
		if (protectLevel >= 0) {
			valueModifier = (2.0F + protectLevel) / 2.0F;
		} else {
			valueModifier = (4.0F + protectLevel) / 4.0F;
		}
	}

	@Override
	public boolean canApply(ItemStack itemstack, boolean considering) {
		if (super.canApply(itemstack, considering)) {
			Item item = itemstack.getItem();
			if (item instanceof ItemArmor) {
				ItemArmor armor = (ItemArmor) item;
				if (armor.getArmorMaterial() == TESMaterial.ROYCE) {
					return false;
				}
				int prot = armor.damageReduceAmount;
				int total = prot + protectLevel;
				return total > 0 && (considering || total <= TESMaterial.VALYRIAN.getDamageReductionAmount(armor.armorType));
			}
			return true;
		}
		return false;
	}

	@Override
	public String getDescription(ItemStack itemstack) {
		return StatCollector.translateToLocalFormatted("tes.enchant.protect.desc", formatAdditiveInt(protectLevel));
	}

	@Override
	public boolean isBeneficial() {
		return protectLevel >= 0;
	}

	public int getProtectLevel() {
		return protectLevel;
	}
}