package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.Item;

public class TESItemLegendaryHammer extends TESItemHammer {
	public TESItemLegendaryHammer(Item.ToolMaterial material) {
		super(material);
		setMaxDamage(1500);
		tesWeaponDamage = 12.0f;
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}