package tes.common.item.weapon;

import net.minecraft.item.Item;

public class TESItemDagger extends TESItemSword {
	public TESItemDagger(Item.ToolMaterial material) {
		this(material, HitEffect.NONE);
	}

	public TESItemDagger(Item.ToolMaterial material, HitEffect e) {
		super(material, e);
		tesWeaponDamage -= 3.0f;
		effect = e;
	}
}