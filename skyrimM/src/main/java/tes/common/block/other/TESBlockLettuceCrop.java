package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESItems;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.EnumPlantType;

public class TESBlockLettuceCrop extends BlockCrops {
	@SideOnly(Side.CLIENT)
	private IIcon[] lettuceIcons;

	@Override
	public Item func_149865_P() {
		return TESItems.lettuce;
	}

	@Override
	public Item func_149866_i() {
		return TESItems.lettuce;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		if (j1 < 7) {
			if (j1 == 6) {
				j1 = 5;
			}
			return lettuceIcons[j1 >> 1];
		}
		return lettuceIcons[3];
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int i, int j, int k) {
		return EnumPlantType.Crop;
	}

	@Override
	public int getRenderType() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		lettuceIcons = new IIcon[4];
		for (int i = 0; i < lettuceIcons.length; ++i) {
			lettuceIcons[i] = iconregister.registerIcon(getTextureName() + '_' + i);
		}
	}
}