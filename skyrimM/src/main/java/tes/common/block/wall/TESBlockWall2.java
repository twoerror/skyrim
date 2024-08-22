package tes.common.block.wall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class TESBlockWall2 extends TESBlockWallBase {
	public TESBlockWall2() {
		super(TESBlocks.brick1, 16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		switch (j) {
			case 0:
				return TESBlocks.brick2.getIcon(i, 0);
			case 1:
				return TESBlocks.brick2.getIcon(i, 1);
			case 2:
				return TESBlocks.rock.getIcon(i, 4);
			case 3:
				return TESBlocks.brick2.getIcon(i, 2);
			case 4:
				return TESBlocks.brick2.getIcon(i, 3);
			case 5:
				return TESBlocks.brick2.getIcon(i, 4);
			case 6:
				return TESBlocks.brick2.getIcon(i, 5);
			case 7:
				return TESBlocks.brick2.getIcon(i, 7);
			case 8:
				return TESBlocks.brick2.getIcon(i, 8);
			case 9:
				return TESBlocks.brick2.getIcon(i, 9);
			case 10:
				return TESBlocks.brick2.getIcon(i, 11);
			case 11:
				return TESBlocks.brick3.getIcon(i, 2);
			case 12:
				return TESBlocks.brick3.getIcon(i, 3);
			case 13:
				return TESBlocks.brick3.getIcon(i, 4);
			case 14:
				return TESBlocks.brick3.getIcon(i, 9);
			case 15:
				return TESBlocks.brick3.getIcon(i, 10);
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 10));
		list.add(new ItemStack(item, 1, 15));
	}
}