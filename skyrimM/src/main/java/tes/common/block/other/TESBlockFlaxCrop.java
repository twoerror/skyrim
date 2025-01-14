package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

public class TESBlockFlaxCrop extends BlockCrops {
	@SideOnly(Side.CLIENT)
	private IIcon[] flaxIcons;

	@Override
	public Item func_149865_P() {
		return TESItems.flax;
	}

	@Override
	public Item func_149866_i() {
		return TESItems.flaxSeeds;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		if (j1 < 7) {
			if (j1 == 6) {
				j1 = 5;
			}
			return flaxIcons[j1 >> 1];
		}
		return TESBlocks.flaxPlant.getIcon(i, j1);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int i, int j, int k) {
		return EnumPlantType.Crop;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		flaxIcons = new IIcon[3];
		for (int i = 0; i < flaxIcons.length; ++i) {
			flaxIcons[i] = iconregister.registerIcon(getTextureName() + '_' + i);
		}
	}
}