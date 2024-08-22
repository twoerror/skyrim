package tes.common.block.slab;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class TESBlockSlabDirt extends TESBlockSlabBase {
	public TESBlockSlabDirt(boolean hidden) {
		super(hidden, Material.ground, 6);
		setHardness(0.5f);
		setStepSound(soundTypeGravel);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		j1 &= 7;
		switch (j1) {
			case 0:
				return Blocks.dirt.getIcon(i, 0);
			case 1:
				return TESBlocks.dirtPath.getIcon(i, 0);
			case 2:
				return TESBlocks.mud.getIcon(i, 0);
			case 3:
				return TESBlocks.asshaiDirt.getIcon(i, 0);
			case 4:
				return TESBlocks.dirtPath.getIcon(i, 1);
			case 5:
				return TESBlocks.dirtPath.getIcon(i, 2);
			default:
				return null;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}
}