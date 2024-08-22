package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.Item;

public class TESItemLegendarySword extends TESItemSword {
	public TESItemLegendarySword(Item.ToolMaterial material) {
		this(material, HitEffect.NONE);
	}

	public TESItemLegendarySword(Item.ToolMaterial material, HitEffect e) {
		super(material, e);
		setMaxDamage(1500);
		tesWeaponDamage = 9.0f;
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}