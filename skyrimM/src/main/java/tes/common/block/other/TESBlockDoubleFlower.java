package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockDoubleFlower extends BlockDoublePlant {
	private static final String[] FLOWER_NAMES = {"black_iris", "yellow_iris", "pink", "red"};

	@SideOnly(Side.CLIENT)
	private IIcon[] doublePlantBottomIcons;

	@SideOnly(Side.CLIENT)
	private IIcon[] doublePlantTopIcons;

	public TESBlockDoubleFlower() {
		setCreativeTab(TESCreativeTabs.TAB_DECO);
	}

	private static int getFlowerMeta(int i) {
		return i & 7;
	}

	private static boolean isTop(int i) {
		return (i & 8) != 0;
	}

	@Override
	public boolean canBlockStay(World world, int i, int j, int k) {
		if (world.getBlock(i, j, k) != this) {
			return super.canBlockStay(world, i, j, k);
		}
		int l = world.getBlockMetadata(i, j, k);
		return isTop(l) ? world.getBlock(i, j - 1, k) == this : world.getBlock(i, j + 1, k) == this && super.canBlockStay(world, i, j, k);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return super.canPlaceBlockAt(world, i, j, k) && world.isAirBlock(i, j + 1, k);
	}

	@Override
	public void checkAndDropBlock(World world, int i, int j, int k) {
		if (!canBlockStay(world, i, j, k)) {
			int l = world.getBlockMetadata(i, j, k);
			if (!isTop(l)) {
				dropBlockAsItem(world, i, j, k, l, 0);
				if (world.getBlock(i, j + 1, k) == this) {
					world.setBlock(i, j + 1, k, Blocks.air, 0, 2);
				}
			}
			world.setBlock(i, j, k, Blocks.air, 0, 2);
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess world, int i, int j, int k) {
		return 16777215;
	}

	@Override
	public int damageDropped(int i) {
		return isTop(i) ? 0 : i & 7;
	}

	@Override
	public boolean func_149851_a(World world, int i, int j, int k, boolean flag) {
		return true;
	}

	@Override
	public void func_149853_b(World world, Random random, int i, int j, int k) {
		int meta = func_149885_e(world, i, j, k);
		dropBlockAsItem(world, i, j, k, new ItemStack(this, 1, meta));
	}

	@Override
	public int func_149885_e(IBlockAccess world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		return (isTop(l) ? world.getBlockMetadata(i, j - 1, k) : l) & 7;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon func_149888_a(boolean isTop, int i) {
		int i1 = i;
		if (isTop) {
			if (i1 >= doublePlantTopIcons.length) {
				i1 = 0;
			}
			return doublePlantTopIcons[i1];
		}
		if (i1 >= doublePlantBottomIcons.length) {
			i1 = 0;
		}
		return doublePlantBottomIcons[i1];
	}

	@Override
	public int getDamageValue(World world, int i, int j, int k) {
		int l = world.getBlockMetadata(i, j, k);
		return isTop(l) ? getFlowerMeta(world.getBlockMetadata(i, j - 1, k)) : getFlowerMeta(l);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (isTop(j)) {
			return doublePlantBottomIcons[1];
		}
		int k = j & 7;
		if (k >= doublePlantBottomIcons.length) {
			k = 0;
		}
		return doublePlantBottomIcons[k];
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		if (isTop(i)) {
			return null;
		}
		return Item.getItemFromBlock(this);
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getDoublePlantRenderID();
	}

	@SideOnly(Side.CLIENT)
	@Override
	@SuppressWarnings("rawtypes")
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int i = 0; i < doublePlantBottomIcons.length; ++i) {
			list.add(new ItemStack(item, 1, i));
		}
	}

	@Override
	public void onBlockHarvested(World world, int i, int j, int k, int meta, EntityPlayer entityplayer) {
		if (isTop(meta)) {
			if (world.getBlock(i, j - 1, k) == this) {
				if (entityplayer.capabilities.isCreativeMode) {
					world.setBlockToAir(i, j - 1, k);
				} else {
					world.func_147480_a(i, j - 1, k, true);
				}
			}
		} else if (entityplayer.capabilities.isCreativeMode && world.getBlock(i, j + 1, k) == this) {
			world.setBlock(i, j + 1, k, Blocks.air, 0, 2);
		}
		super.onBlockHarvested(world, i, j, k, meta, entityplayer);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack itemstack) {
		int l = ((MathHelper.floor_double(entity.rotationYaw * 4.0f / 360.0f + 0.5) & 3) + 2) % 4;
		world.setBlock(i, j + 1, k, this, 8 | l, 2);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		doublePlantBottomIcons = new IIcon[FLOWER_NAMES.length];
		doublePlantTopIcons = new IIcon[FLOWER_NAMES.length];
		for (int i = 0; i < doublePlantBottomIcons.length; ++i) {
			doublePlantBottomIcons[i] = iconregister.registerIcon(getTextureName() + '_' + FLOWER_NAMES[i] + "_bottom");
			doublePlantTopIcons[i] = iconregister.registerIcon(getTextureName() + '_' + FLOWER_NAMES[i] + "_top");
		}
	}
}