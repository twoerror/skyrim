package tes.common.item.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESMaterial;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESItemKaftan extends TESItemRobes {
	@SideOnly(Side.CLIENT)
	private IIcon overlayIcon;

	public TESItemKaftan(int slot) {
		super(TESMaterial.KAFTAN, slot);
	}

	@Override
	public int getColor(ItemStack itemstack) {
		return getRobesColor(itemstack);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int pass) {
		if (pass >= 1) {
			return 16777215;
		}
		return super.getColorFromItemStack(itemstack, pass);
	}

	@Override
	public IIcon getIcon(ItemStack itemstack, int pass) {
		if (pass >= 1) {
			return overlayIcon;
		}
		return super.getIcon(itemstack, pass);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconregister) {
		super.registerIcons(iconregister);
		overlayIcon = iconregister.registerIcon(getIconString() + "_overlay");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}
}