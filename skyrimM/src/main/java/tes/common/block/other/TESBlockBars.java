package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class TESBlockBars extends TESBlockPane {
	public TESBlockBars() {
		super("", "", Material.iron, true);
		setHardness(5.0f);
		setResistance(10.0f);
		setStepSound(soundTypeMetal);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon func_150097_e() {
		return blockIcon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		blockIcon = iconregister.registerIcon(getTextureName());
	}
}