package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockButton;
import net.minecraft.client.renderer.texture.IIconRegister;

public class TESBlockButton extends BlockButton {
	private final String iconPath;

	public TESBlockButton(boolean flag, String s) {
		super(flag);
		iconPath = s;
		setCreativeTab(TESCreativeTabs.TAB_MISC);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		blockIcon = iconregister.registerIcon(iconPath);
	}
}