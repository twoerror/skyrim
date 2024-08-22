package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.Item;

public class TESItemLegendaryDagger extends TESItemDagger {
	public TESItemLegendaryDagger(Item.ToolMaterial material) {
		this(material, HitEffect.NONE);
	}

	public TESItemLegendaryDagger(Item.ToolMaterial material, HitEffect e) {
		super(material, e);
		setMaxDamage(1500);
		tesWeaponDamage = 6.0f;
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}