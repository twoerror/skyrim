package tes.common.block.planks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.List;

public abstract class TESBlockPlanksBase extends Block {
	protected String[] plankTypes;

	@SideOnly(Side.CLIENT)
	private IIcon[] plankIcons;

	protected TESBlockPlanksBase() {
		super(Material.wood);
		setHardness(2.0f);
		setResistance(5.0f);
		setStepSound(soundTypeWood);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		if (j1 >= plankTypes.length) {
			j1 = 0;
		}
		return plankIcons[j1];
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int j = 0; j < plankTypes.length; ++j) {
			list.add(new ItemStack(item, 1, j));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		plankIcons = new IIcon[plankTypes.length];
		for (int i = 0; i < plankTypes.length; ++i) {
			plankIcons[i] = iconregister.registerIcon(getTextureName() + '_' + plankTypes[i]);
		}
	}
}