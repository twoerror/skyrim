package tes.common.block.slab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class TESBlockSlabGravel extends TESBlockSlabFalling {
	public TESBlockSlabGravel(boolean hidden) {
		super(hidden, Material.sand, 3);
		setHardness(0.6f);
		setStepSound(soundTypeGravel);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		j1 &= 7;
		switch (j1) {
			case 0:
				return Blocks.gravel.getIcon(i, 0);
			case 1:
				return TESBlocks.basaltGravel.getIcon(i, 0);
			case 2:
				return TESBlocks.obsidianGravel.getIcon(i, 0);
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}
}