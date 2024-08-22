package tes.common.block.slab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class TESBlockSlabV extends TESBlockSlabBase {
	public TESBlockSlabV(boolean hidden) {
		super(hidden, Material.rock, 6);
		setCreativeTab(hidden ? null : CreativeTabs.tabBlock);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		j1 &= 7;
		switch (j1) {
			case 0:
				return Blocks.stonebrick.getIcon(i, 1);
			case 1:
				return Blocks.stonebrick.getIcon(i, 2);
			case 2:
				return TESBlocks.redBrick.getIcon(i, 0);
			case 3:
				return TESBlocks.redBrick.getIcon(i, 1);
			case 4:
				return Blocks.mossy_cobblestone.getIcon(i, 0);
			case 5:
				return Blocks.stone.getIcon(i, 0);
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}
}