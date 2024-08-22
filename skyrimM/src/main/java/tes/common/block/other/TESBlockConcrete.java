package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import tes.common.util.TESEnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class TESBlockConcrete extends Block {
	private final TESEnumDyeColor color;

	public TESBlockConcrete(TESEnumDyeColor color) {
		super(Material.rock);
		this.color = color;
		setBlockName("concrete_" + this.color);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(1.5f);
		setHarvestLevel("pickaxe", 0);
		setResistance(10.0f);
		setBlockTextureName("tes:concrete_" + this.color.getName());
	}

	public TESEnumDyeColor getColor() {
		return color;
	}
}