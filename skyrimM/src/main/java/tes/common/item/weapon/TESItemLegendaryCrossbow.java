package tes.common.item.weapon;

import tes.common.database.TESCreativeTabs;
import tes.common.database.TESMaterial;

public class TESItemLegendaryCrossbow extends TESItemCrossbow {
	public TESItemLegendaryCrossbow() {
		super(TESMaterial.COBALT_TOOL);
		setMaxDamage(1500);
		setCreativeTab(TESCreativeTabs.TAB_STORY);
	}
}