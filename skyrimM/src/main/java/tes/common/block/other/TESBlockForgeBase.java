package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import tes.common.tileentity.TESTileEntityAlloyForge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public abstract class TESBlockForgeBase extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private IIcon[] forgeIcons;

	protected TESBlockForgeBase() {
		super(Material.rock);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setHardness(4.0f);
		setStepSound(soundTypeStone);
	}

	public static boolean isForgeActive(IBlockAccess world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		return (meta & 8) != 0;
	}

	public static void toggleForgeActive(World world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		world.setBlockMetadataWithNotify(i, j, k, meta ^ 8, 2);
		world.updateLightByType(EnumSkyBlock.Block, i, j, k);
	}

	private static void setDefaultDirection(World world, int i, int j, int k) {
		if (!world.isRemote) {
			Block i1 = world.getBlock(i, j, k - 1);
			Block j1 = world.getBlock(i, j, k + 1);
			Block k1 = world.getBlock(i - 1, j, k);
			Block l1 = world.getBlock(i + 1, j, k);
			int meta = 3;
			if (j1.isOpaqueCube() && !i1.isOpaqueCube()) {
				meta = 2;
			}
			if (k1.isOpaqueCube() && !l1.isOpaqueCube()) {
				meta = 5;
			}
			if (l1.isOpaqueCube() && !k1.isOpaqueCube()) {
				meta = 4;
			}
			world.setBlockMetadataWithNotify(i, j, k, meta, 2);
		}
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		IInventory forge = (IInventory) world.getTileEntity(i, j, k);
		if (forge != null) {
			TES.dropContainerItems(forge, world, i, j, k);
			world.func_147453_f(i, j, k, block);
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public int getComparatorInputOverride(World world, int i, int j, int k, int direction) {
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(i, j, k));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int i, int j, int k, int side) {
		if (side == 1 || side == 0) {
			return forgeIcons[1];
		}
		int meta = world.getBlockMetadata(i, j, k) & 7;
		return forgeIcons[side == meta ? isForgeActive(world, i, j, k) ? 3 : 2 : 0];
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return forgeIcons[i == 1 || i == 0 ? 1 : i == 3 ? 2 : 0];
	}

	@Override
	public int getLightValue(IBlockAccess world, int i, int j, int k) {
		return isForgeActive(world, i, j, k) ? 13 : 0;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		setDefaultDirection(world, i, j, k);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack itemstack) {
		int rotation = MathHelper.floor_double(entity.rotationYaw * 4.0f / 360.0f + 0.5) & 3;

		switch (rotation) {
			case 0:
				world.setBlockMetadataWithNotify(i, j, k, 2, 2);
				break;
			case 1:
				world.setBlockMetadataWithNotify(i, j, k, 5, 2);
				break;
			case 2:
				world.setBlockMetadataWithNotify(i, j, k, 3, 2);
				break;
			case 3:
				world.setBlockMetadataWithNotify(i, j, k, 4, 2);
				break;
		}
		if (itemstack.hasDisplayName()) {
			((TESTileEntityAlloyForge) world.getTileEntity(i, j, k)).setSpecialForgeName(itemstack.getDisplayName());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (isForgeActive(world, i, j, k)) {
			int meta = world.getBlockMetadata(i, j, k) & 7;
			float f = i + 0.5f;
			float f1 = j + 0.0f + random.nextFloat() * 6.0f / 16.0f;
			float f2 = k + 0.5f;
			float f3 = 0.52f;
			float f4 = random.nextFloat() * 0.6f - 0.3f;
			switch (meta) {
				case 4:
					world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0, 0.0, 0.0);
					world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0, 0.0, 0.0);
					break;
				case 5:
					world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0, 0.0, 0.0);
					world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0, 0.0, 0.0);
					break;
				case 2:
					world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0, 0.0, 0.0);
					world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0, 0.0, 0.0);
					break;
				case 3:
					world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0, 0.0, 0.0);
					world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0, 0.0, 0.0);
					break;
			}
			if (useLargeSmoke()) {
				for (int l = 0; l < 6; ++l) {
					float f10 = random.nextBoolean() ? 0.0f : 1.0f;
					float f11 = random.nextBoolean() ? 0.0f : 1.0f;
					float f12 = 0.5f;
					f10 += -0.1f + random.nextFloat() * 0.2f;
					f11 += -0.1f + random.nextFloat() * 0.2f;
					if (random.nextInt(3) > 0) {
						world.spawnParticle("largesmoke", i + f10, j + f12, k + f11, 0.0, 0.0, 0.0);
						continue;
					}
					world.spawnParticle("smoke", i + f10, j + f12, k + f11, 0.0, 0.0, 0.0);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		forgeIcons = new IIcon[4];
		forgeIcons[0] = iconregister.registerIcon(getTextureName() + "_side");
		forgeIcons[1] = iconregister.registerIcon(getTextureName() + "_top");
		forgeIcons[2] = iconregister.registerIcon(getTextureName() + "_front");
		forgeIcons[3] = iconregister.registerIcon(getTextureName() + "_active");
	}

	protected abstract boolean useLargeSmoke();
}