package tes.common.enchant;

import tes.common.item.TESWeaponStats;
import tes.common.item.weapon.TESItemCommandSword;
import tes.common.item.weapon.TESItemSarbacane;
import tes.common.item.weapon.TESItemThrowingAxe;
import net.minecraft.item.*;

public enum TESEnchantmentType {
	BREAKABLE, ARMOR, ARMOR_FEET, ARMOR_LEGS, ARMOR_BODY, ARMOR_HEAD, MELEE, TOOL, SHEARS, RANGED, RANGED_LAUNCHER, THROWING_AXE, FISHING;

	public boolean canApply(ItemStack itemstack) {
		Item item = itemstack.getItem();

		if (this == BREAKABLE && item.isDamageable()) {
			return true;
		}
		if (item instanceof ItemArmor && ((ItemArmor) item).damageReduceAmount > 0) {
			if (this == ARMOR) {
				return true;
			}
			ItemArmor itemarmor = (ItemArmor) item;
			int armorType = itemarmor.armorType;
			switch (armorType) {
				case 0:
					return this == ARMOR_HEAD;
				case 1:
					return this == ARMOR_BODY;
				case 2:
					return this == ARMOR_LEGS;
				case 3:
					return this == ARMOR_FEET;
				default:
					break;
			}
		}
		return this == MELEE && TESWeaponStats.isMeleeWeapon(itemstack) && !(item instanceof TESItemCommandSword) || this == TOOL && !item.getToolClasses(itemstack).isEmpty() || this == SHEARS && item instanceof ItemShears || this == RANGED && TESWeaponStats.isRangedWeapon(itemstack) || this == RANGED_LAUNCHER && (item instanceof ItemBow || item instanceof TESItemSarbacane) || this == THROWING_AXE && item instanceof TESItemThrowingAxe || this == FISHING && item instanceof ItemFishingRod;
	}
}