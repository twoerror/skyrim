package tes.common.block.other;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESGuiId;
import tes.common.tileentity.TESTileEntityBookshelf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;

public class TESBlockBookshelfStorage extends BlockContainer {
	public TESBlockBookshelfStorage() {
		super(Material.wood);
		setHardness(1.5f);
		setStepSound(soundTypeWood);
		setCreativeTab(null);
	}

	public static boolean canOpenBookshelf(EntityPlayer entityplayer) {
		ItemStack itemstack = entityplayer.inventory.getCurrentItem();
		return itemstack == null || itemstack.getItem() != Item.getItemFromBlock(Blocks.bookshelf);
	}

	@Override
	public void breakBlock(World world, int i, int j, int k, Block block, int meta) {
		IInventory bookshelf = (IInventory) world.getTileEntity(i, j, k);
		if (bookshelf != null) {
			TES.dropContainerItems(bookshelf, world, i, j, k);
			world.func_147453_f(i, j, k, block);
		}
		super.breakBlock(world, i, j, k, block, meta);
	}

	@Override
	public boolean canSilkHarvest() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityBookshelf();
	}

	@Override
	public ItemStack createStackedBlock(int i) {
		return new ItemStack(Blocks.bookshelf);
	}

	@Override
	public int getComparatorInputOverride(World world, int i, int j, int k, int direction) {
		return Container.calcRedstoneFromInventory((IInventory) world.getTileEntity(i, j, k));
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
		return Blocks.bookshelf.getDrops(world, i, j, k, meta, fortune);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		return Blocks.bookshelf.getIcon(i, j);
	}

	@Override
	public boolean hasComparatorInputOverride() {
		return true;
	}

	@Override
	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int side, float f, float f1, float f2) {
		if (!canOpenBookshelf(entityplayer)) {
			return false;
		}
		if (!world.isRemote) {
			entityplayer.openGui(TES.instance, TESGuiId.BOOKSHELF.ordinal(), world, i, j, k);
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}
}