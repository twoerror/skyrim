package tes.common.item.tool;

import tes.common.database.TESCreativeTabs;
import tes.common.item.TESMaterialFinder;
import net.minecraft.item.ItemAxe;

public class TESItemAxe extends ItemAxe implements TESMaterialFinder {
	private final ToolMaterial tesMaterial;

	public TESItemAxe(ToolMaterial material) {
		super(material);
		setCreativeTab(TESCreativeTabs.TAB_TOOLS);
		setHarvestLevel("axe", material.getHarvestLevel());
		tesMaterial = material;
	}

	@Override
	public ToolMaterial getMaterial() {
		return tesMaterial;
	}
}