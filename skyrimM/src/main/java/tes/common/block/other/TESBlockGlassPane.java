package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

public class TESBlockGlassPane extends TESBlockPane {
	public TESBlockGlassPane() {
		super("tes:glass", "tes:glass_pane_top", Material.glass, false);
		setHardness(0.3f);
		setStepSound(soundTypeGlass);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
	}

	@Override
	public boolean canPaneConnectTo(IBlockAccess world, int i, int j, int k, ForgeDirection dir) {
		return super.canPaneConnectTo(world, i, j, k, dir) || world.getBlock(i, j, k) instanceof TESBlockGlass;
	}
}