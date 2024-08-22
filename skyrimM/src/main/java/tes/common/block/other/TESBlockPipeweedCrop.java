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
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.Random;

public class TESBlockPipeweedCrop extends BlockCrops {
	@SideOnly(Side.CLIENT)
	private IIcon[] pipeweedIcons;

	@Override
	public Item func_149865_P() {
		return TESItems.pipeweedLeaf;
	}

	@Override
	public Item func_149866_i() {
		return TESItems.pipeweedSeeds;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		if (j1 < 7) {
			if (j1 == 6) {
				j1 = 5;
			}
			return pipeweedIcons[j1 >> 1];
		}
		return pipeweedIcons[3];
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int i, int j, int k) {
		return EnumPlantType.Crop;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (world.getBlockMetadata(i, j, k) == 7) {
			TESBlocks.pipeweedPlant.randomDisplayTick(world, i, j, k, random);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		pipeweedIcons = new IIcon[4];
		for (int i = 0; i < pipeweedIcons.length; ++i) {
			pipeweedIcons[i] = iconregister.registerIcon(getTextureName() + '_' + i);
		}
	}
}