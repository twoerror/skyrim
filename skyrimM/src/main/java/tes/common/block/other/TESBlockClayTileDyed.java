package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockColored;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public class TESBlockClayTileDyed extends TESBlockClayTile {
	@SideOnly(Side.CLIENT)
	private IIcon[] clayIcons;

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		if (j1 >= 16) {
			j1 = 0;
		}
		return clayIcons[j1];
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < 16; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		clayIcons = new IIcon[16];
		for (int i = 0; i < clayIcons.length; ++i) {
			int dyeMeta = BlockColored.func_150031_c(i);
			if ("lightBlue".equals(ItemDye.field_150923_a[dyeMeta])) {
				clayIcons[i] = iconregister.registerIcon(getTextureName() + "_light_blue");
			} else {
				clayIcons[i] = iconregister.registerIcon(getTextureName() + '_' + ItemDye.field_150923_a[dyeMeta]);
			}
		}
	}
}