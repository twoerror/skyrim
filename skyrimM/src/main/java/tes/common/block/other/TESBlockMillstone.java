package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESGuiId;
import tes.common.tileentity.TESTileEntityMillstone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockMillstone extends BlockContainer {
	@SideOnly(Side.CLIENT)
	private IIcon iconSide;

	@SideOnly(Side.CLIENT)
	private IIcon iconTop;

	@SideOnly(Side.CLIENT)
	private IIcon iconSideActive;

	@SideOnly(Side.CLIENT)
	private IIcon iconTopActive;

	public TESBlockMillstone() {
		super(Material.rock);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setHardness(4.0f);
		setStepSound(soundTypeStone);
	}

	private static boolean isMillstoneActive(IBlockAccess world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		return (meta & 8) != 0;
	}

	public static void toggleMillstoneActive(World world, int i, int j, int k) {
		int meta = world.getBlockMetadata(i, j, k);
		world.setBlockMetadataWithNotify(i, j, k, meta ^ 8, 2);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		IInventory millstone = (IInventory) world.getTileEntity(i, j, k);
		if (millstone != null) {
			TES.dropContainerItems(millstone, world, i, j, k);
			world.func_147453_f(i, j, k, block);
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityMillstone();
	}

	@Override
	public int getComparatorInputOverride(World world, int i, int j, int k, int direction) {
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(i, j, k));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int i, int j, int k, int side) {
		boolean active = isMillstoneActive(world, i, j, k);
		if (side == 1 || side == 0) {
			return active ? iconTopActive : iconTop;
		}
		return active ? iconSideActive : iconSide;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return i == 1 || i == 0 ? iconTop : iconSide;
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		if (!world.isRemote) {
			entityplayer.openGui(TES.instance, TESGuiId.MILLSTONE.ordinal(), world, i, j, k);
		}
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack itemstack) {
		if (itemstack.hasDisplayName()) {
			((TESTileEntityMillstone) world.getTileEntity(i, j, k)).setSpecialMillstoneName(itemstack.getDisplayName());
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		if (isMillstoneActive(world, i, j, k)) {
			for (int l = 0; l < 6; ++l) {
				float f10 = 0.5f + MathHelper.randomFloatClamp(random, -0.2f, 0.2f);
				float f11 = 0.5f + MathHelper.randomFloatClamp(random, -0.2f, 0.2f);
				float f12 = 0.9f + random.nextFloat() * 0.2f;
				world.spawnParticle("smoke", i + f10, j + f12, k + f11, 0.0, 0.0, 0.0);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		iconSide = iconregister.registerIcon(getTextureName() + "_side");
		iconTop = iconregister.registerIcon(getTextureName() + "_top");
		iconSideActive = iconregister.registerIcon(getTextureName() + "_side_active");
		iconTopActive = iconregister.registerIcon(getTextureName() + "_top_active");
	}
}