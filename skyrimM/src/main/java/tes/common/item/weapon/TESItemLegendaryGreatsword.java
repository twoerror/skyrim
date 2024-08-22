package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.Item;

public class TESItemLegendaryGreatsword extends TESItemGreatsword {
	public TESItemLegendaryGreatsword(Item.ToolMaterial material) {
		super(material);
		setMaxDamage(1500);
		tesWeaponDamage = 15.0f;
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}