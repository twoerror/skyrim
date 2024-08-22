package tes.common.item.weapon;

import net.minecraft.item.Item;

public class TESItemLongsword extends TESItemSword {
	public TESItemLongsword(Item.ToolMaterial material) {
		super(material);
		tesWeaponDamage += 1.5F;
	}
}