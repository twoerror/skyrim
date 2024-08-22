package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockVine;
import net.minecraft.world.IBlockAccess;

public class TESBlockVine extends BlockVine {
	public TESBlockVine() {
		setCreativeTab(TESCreativeTabs.TAB_DECO);
		setHardness(0.2f);
		setStepSound(soundTypeGrass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, int i, int j, int k) {
		return 16777215;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBlockColor() {
		return 16777215;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderColor(int i) {
		return 16777215;
	}
}