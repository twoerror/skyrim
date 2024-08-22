package tes.common.block.wall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;

public abstract class TESBlockWallBase extends BlockWall {
	private final int subtypes;

	protected TESBlockWallBase(Block block, int i) {
		super(block);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		subtypes = i;
	}

	@Override
	public boolean canPlaceTorchOnTop(World world, int i, int j, int k) {
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int j = 0; j < subtypes; ++j) {
			list.add(new ItemStack(item, 1, j));
		}
	}
}