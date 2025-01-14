package tes.common.block.sapling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.block.other.TESBlockFlower;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.List;
import java.util.Random;

public abstract class TESBlockSaplingBase extends TESBlockFlower {
	protected String[] saplingNames;

	@SideOnly(Side.CLIENT)
	private IIcon[] saplingIcons;

	protected TESBlockSaplingBase() {
		float f = 0.4f;
		setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, f * 2.0f, 0.5f + f);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
	}

	public static int[] findPartyTree(IBlockAccess world, int i, int j, int k, Block block, int meta) {
		return findSaplingSquare(world, i, j, k, block, meta, -1, 1, -2, 2);
	}

	protected static int[] findSaplingSquare(IBlockAccess world, int i, int j, int k, Block block, int meta, int squareMin, int squareMax, int searchMin, int searchMax) {
		for (int i1 = searchMin; i1 <= searchMax; ++i1) {
			for (int k1 = searchMin; k1 <= searchMax; ++k1) {
				boolean canGenerate = true;
				block2:
				for (int i2 = squareMin; i2 <= squareMax; ++i2) {
					for (int k2 = squareMin; k2 <= squareMax; ++k2) {
						int i3 = i + i1 + i2;
						int k3 = k + k1 + k2;
						if (isSameSapling(world, i3, j, k3, block, meta)) {
							continue;
						}
						canGenerate = false;
						break block2;
					}
				}
				if (!canGenerate) {
					continue;
				}
				return new int[]{i1, k1};
			}
		}
		return null;
	}

	public static boolean isSameSapling(IBlockAccess world, int i, int j, int k, Block block, int meta) {
		if (world.getBlock(i, j, k) == block) {
			int blockMeta = world.getBlockMetadata(i, j, k);
			return (blockMeta & 7) == meta;
		}
		return false;
	}

	@Override
	public int damageDropped(int i) {
		return i & 7;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		int j1 = j;
		j1 &= 7;
		if (j1 >= saplingNames.length) {
			j1 = 0;
		}
		return saplingIcons[j1];
	}

	@Override
	public int getRenderType() {
		return 1;
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < saplingNames.length; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	protected abstract void growTree(World var1, int var2, int var3, int var4, Random var5);

	public void incrementGrowth(World world, int i, int j, int k, Random random) {
		int meta = world.getBlockMetadata(i, j, k);
		if ((meta & 8) == 0) {
			world.setBlockMetadataWithNotify(i, j, k, meta | 8, 4);
		} else {
			if (!TerrainGen.saplingGrowTree(world, random, i, j, k)) {
				return;
			}
			growTree(world, i, j, k, random);
		}
	}

	protected boolean isSameSapling(IBlockAccess world, int i, int j, int k, int meta) {
		return isSameSapling(world, i, j, k, this, meta);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		saplingIcons = new IIcon[saplingNames.length];
		for (int i = 0; i < saplingNames.length; ++i) {
			saplingIcons[i] = iconregister.registerIcon(getTextureName() + '_' + saplingNames[i]);
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		if (!world.isRemote) {
			super.updateTick(world, i, j, k, random);
			if (world.getBlockLightValue(i, j + 1, k) >= 9 && random.nextInt(7) == 0) {
				incrementGrowth(world, i, j, k, random);
			}
		}
	}
}