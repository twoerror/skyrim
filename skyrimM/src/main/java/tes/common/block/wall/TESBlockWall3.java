package tes.common.block.wall;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class TESBlockWall3 extends TESBlockWallBase {
	public TESBlockWall3() {
		super(TESBlocks.brick1, 16);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		switch (j) {
			case 0:
				return TESBlocks.brick3.getIcon(i, 5);
			case 1:
				return TESBlocks.brick3.getIcon(i, 6);
			case 2:
				return TESBlocks.brick3.getIcon(i, 7);
			case 3:
				return TESBlocks.brick3.getIcon(i, 11);
			case 4:
				return TESBlocks.brick3.getIcon(i, 13);
			case 5:
				return TESBlocks.brick3.getIcon(i, 14);
			case 6:
				return TESBlocks.rock.getIcon(i, 5);
			case 7:
				return TESBlocks.brick4.getIcon(i, 15);
			case 8:
				return TESBlocks.brick5.getIcon(i, 0);
			case 9:
				return TESBlocks.brick5.getIcon(i, 1);
			case 10:
				return TESBlocks.brick5.getIcon(i, 2);
			case 11:
				return TESBlocks.brick5.getIcon(i, 4);
			case 12:
				return TESBlocks.brick5.getIcon(i, 5);
			case 13:
				return TESBlocks.brick5.getIcon(i, 7);
			case 14:
				return TESBlocks.whiteSandstone.getIcon(i, 0);
			case 15:
				return TESBlocks.brick5.getIcon(i, 11);
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 5));
		list.add(new ItemStack(item, 1, 6));
		list.add(new ItemStack(item, 1, 7));
		list.add(new ItemStack(item, 1, 8));
		list.add(new ItemStack(item, 1, 14));
		list.add(new ItemStack(item, 1, 15));
	}
}