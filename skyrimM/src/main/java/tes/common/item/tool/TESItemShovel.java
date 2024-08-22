package tes.common.item.tool;

import tes.common.database.TESCreativeTabs;
import tes.common.item.TESMaterialFinder;
import net.minecraft.item.ItemSpade;

public class TESItemShovel extends ItemSpade implements TESMaterialFinder {
	private final ToolMaterial tesMaterial;

	public TESItemShovel(ToolMaterial material) {
		super(material);
		setCreativeTab(TESCreativeTabs.TAB_TOOLS);
		tesMaterial = material;
	}

	@Override
	public ToolMaterial getMaterial() {
		return tesMaterial;
	}
}