package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import net.minecraft.item.Item;

public class TESItemLegendaryBattleaxe extends TESItemBattleaxe {
	public TESItemLegendaryBattleaxe(Item.ToolMaterial material) {
		super(material);
		setMaxDamage(1500);
		tesWeaponDamage = 12.0f;
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}