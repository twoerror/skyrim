package tes.common.block.slab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class TESBlockSlab12 extends TESBlockSlabBase {
	public TESBlockSlab12(boolean hidden) {
		super(hidden, Material.rock, 8);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		j1 &= 7;
		switch (j1) {
			case 0:
				return TESBlocks.brick5.getIcon(i, 11);
			case 1:
				return TESBlocks.brick5.getIcon(i, 13);
			case 2:
				return TESBlocks.brick5.getIcon(i, 14);
			case 3:
				return TESBlocks.brick5.getIcon(i, 15);
			case 4:
				return TESBlocks.pillar2.getIcon(i, 8);
			case 5:
				return TESBlocks.brick6.getIcon(i, 1);
			case 6:
				return TESBlocks.pillar2.getIcon(i, 9);
			case 7:
				return TESBlocks.brick6.getIcon(i, 10);
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 5));
		list.add(new ItemStack(item, 1, 6));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}
}