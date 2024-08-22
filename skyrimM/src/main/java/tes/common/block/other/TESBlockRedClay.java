package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import tes.common.database.TESItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;

import java.util.Random;

public class TESBlockRedClay extends Block {
	public TESBlockRedClay() {
		super(Material.clay);
		setHardness(0.6f);
		setStepSound(soundTypeGravel);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return TESItems.redClayBall;
	}

	@Override
	public int quantityDropped(Random random) {
		return 4;
	}
}