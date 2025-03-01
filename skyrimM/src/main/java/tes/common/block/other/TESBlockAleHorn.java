package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;

public class TESBlockAleHorn extends TESBlockMug {
	public TESBlockAleHorn() {
		super(5.0f, 12.0f);
		setStepSound(soundTypeStone);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return Blocks.stained_hardened_clay.getIcon(i, 0);
	}
}