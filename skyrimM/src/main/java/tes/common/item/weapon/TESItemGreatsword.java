package tes.common.item.weapon;

import net.minecraft.item.Item;

public class TESItemGreatsword extends TESItemSword {
	public TESItemGreatsword(Item.ToolMaterial material) {
		super(material);
		tesWeaponDamage += 3;
	}
}