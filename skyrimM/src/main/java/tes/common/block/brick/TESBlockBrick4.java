package tes.common.block.brick;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public class TESBlockBrick4 extends TESBlockBrickBase {
	public TESBlockBrick4() {
		brickNames = new String[]{"sothoryos", "sothoryos_mossy", "sothoryos_cracked", "sothoryos_gold", "sothoryos_obsidian", "sothoryos", "basalt_westeros_carved", "sandstone_lapis", "sothoryos", "sothoryos", "sothoryos", "sothoryos", "sothoryos", "sothoryos", "sothoryos", "chalk"};
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0));
		list.add(new ItemStack(item, 1, 1));
		list.add(new ItemStack(item, 1, 2));
		list.add(new ItemStack(item, 1, 3));
		list.add(new ItemStack(item, 1, 4));
		list.add(new ItemStack(item, 1, 6));
		list.add(new ItemStack(item, 1, 7));
		list.add(new ItemStack(item, 1, 15));
	}
}