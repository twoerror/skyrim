package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESItems;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockDate extends TESBlockHangingFruit {
	@SideOnly(Side.CLIENT)
	@Override
	public Item getItem(World world, int i, int j, int k) {
		return TESItems.date;
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return TESItems.date;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int i, int j, int k) {
		int dir = world.getBlockMetadata(i, j, k);
		switch (dir) {
			case 0:
				setBlockBounds(0.375f, 0.3125f, 0.0f, 0.625f, 0.6875f, 0.25f);
				break;
			case 1:
				setBlockBounds(0.375f, 0.3125f, 0.75f, 0.625f, 0.6875f, 1.0f);
				break;
			case 2:
				setBlockBounds(0.0f, 0.3125f, 0.375f, 0.25f, 0.6875f, 0.625f);
				break;
			case 3:
				setBlockBounds(0.75f, 0.3125f, 0.375f, 1.0f, 0.6875f, 0.625f);
		}
	}
}