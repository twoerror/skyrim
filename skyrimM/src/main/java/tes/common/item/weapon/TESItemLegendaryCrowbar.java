package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.Item;

public class TESItemLegendaryCrowbar extends TESItemSword {
	public TESItemLegendaryCrowbar(Item.ToolMaterial material) {
		super(material);
		setMaxDamage(1500);
		tesWeaponDamage = 999.0f;
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}