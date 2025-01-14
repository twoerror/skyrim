package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESItems;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.util.ForgeDirection;

public class TESBlockYamCrop extends BlockCrops {
	@SideOnly(Side.CLIENT)
	private IIcon[] yamIcons;

	@Override
	public boolean canBlockStay(World world, int i, int j, int k) {
		if (world.getBlockMetadata(i, j, k) == 8) {
			return world.getBlock(i, j - 1, k).canSustainPlant(world, i, j - 1, k, ForgeDirection.UP, Blocks.tallgrass);
		}
		return super.canBlockStay(world, i, j, k);
	}

	@Override
	public Item func_149865_P() {
		return TESItems.yam;
	}

	@Override
	public Item func_149866_i() {
		return TESItems.yam;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		if (j1 < 7) {
			if (j1 == 6) {
				j1 = 5;
			}
			return yamIcons[j1 >> 1];
		}
		return yamIcons[3];
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int i, int j, int k) {
		return EnumPlantType.Crop;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		yamIcons = new IIcon[4];
		for (int i = 0; i < yamIcons.length; ++i) {
			yamIcons[i] = iconregister.registerIcon(getTextureName() + '_' + i);
		}
	}
}