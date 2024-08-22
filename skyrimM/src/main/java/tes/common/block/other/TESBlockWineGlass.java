package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class TESBlockWineGlass extends TESBlockMug {
	public TESBlockWineGlass() {
		super(2.5f, 10.0f);
		setStepSound(soundTypeGlass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return Blocks.glass.getIcon(i, 0);
	}
}