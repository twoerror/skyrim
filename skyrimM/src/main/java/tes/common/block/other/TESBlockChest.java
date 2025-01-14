package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESCreativeTabs;
import tes.common.database.TESGuiId;
import tes.common.tileentity.TESTileEntityChest;
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
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TESBlockChest extends BlockContainer {
	private final Block baseBlock;
	private final int baseMeta;
	private final String chestTextureName;

	public TESBlockChest(Material m, Block b, int i, String s) {
		super(m);
		baseBlock = b;
		baseMeta = i;
		setStepSound(b.stepSound);
		setCreativeTab(TESCreativeTabs.TAB_UTIL);
		setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
		chestTextureName = s;
	}

	public static IInventory getModChestAt(World world, int i, int j, int k) {
		if (world.isSideSolid(i, j + 1, k, ForgeDirection.DOWN)) {
			return null;
		}
		return (IInventory) world.getTileEntity(i, j, k);
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
		IInventory chest = (IInventory) world.getTileEntity(i, j, k);
		if (chest != null) {
			TES.dropContainerItems(chest, world, i, j, k);
			world.func_147453_f(i, j, k, block);
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		TESTileEntityChest chest = new TESTileEntityChest();
		chest.setTextureName(chestTextureName);
		return chest;
	}

	public String getChestTextureName() {
		return chestTextureName;
	}

	@Override
	public int getComparatorInputOverride(World world, int i, int j, int k, int direction) {
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(i, j, k));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int i, int j) {
		return baseBlock.getIcon(i, baseMeta);
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getChestRenderID();
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		if (!world.isRemote && getModChestAt(world, i, j, k) != null) {
			entityplayer.openGui(TES.instance, TESGuiId.CHEST.ordinal(), world, i, j, k);
		}
		return true;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		setDefaultDirection(world, i, j, k);
	}

	@Override
	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack itemstack) {
		int meta = 0;
		int l = MathHelper.floor_double(entity.rotationYaw * 4.0f / 360.0f + 0.5) & 3;
		if (l == 0) {
			meta = 2;
		}
		if (l == 1) {
			meta = 5;
		}
		if (l == 2) {
			meta = 3;
		}
		if (l == 3) {
			meta = 4;
		}
		world.setBlockMetadataWithNotify(i, j, k, meta, 3);
		if (itemstack.hasDisplayName()) {
			((TESTileEntityChest) world.getTileEntity(i, j, k)).setCustomName(itemstack.getDisplayName());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconregister) {
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}