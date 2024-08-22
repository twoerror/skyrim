package tes.common.item.tool;

import tes.common.database.TESCreativeTabs;
import tes.common.item.TESMaterialFinder;
import net.minecraft.item.ItemHoe;

public class TESItemHoe extends ItemHoe implements TESMaterialFinder {
	private final ToolMaterial tesMaterial;

	public TESItemHoe(ToolMaterial material) {
		super(material);
		setCreativeTab(TESCreativeTabs.TAB_TOOLS);
		tesMaterial = material;
	}

	@Override
	public ToolMaterial getMaterial() {
		return tesMaterial;
	}
}