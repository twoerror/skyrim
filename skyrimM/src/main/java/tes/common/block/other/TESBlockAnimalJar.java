package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import tes.common.item.other.TESItemAnimalJar;
import tes.common.tileentity.TESTileEntityAnimalJar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public abstract class TESBlockAnimalJar extends BlockContainer {
	protected TESBlockAnimalJar(Material material) {
		super(material);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
	}

	@Override
	public boolean canBlockStay(World world, int i, int j, int k) {
		return world.getBlock(i, j - 1, k).isSideSolid(world, i, j - 1, k, ForgeDirection.UP);
	}

	public abstract boolean canCapture(Entity var1);

	@Override
	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		return canBlockStay(world, i, j, k);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TESTileEntityAnimalJar();
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		if ((meta & 8) == 0) {
			ItemStack itemstack = getJarDrop(world, i, j, k, meta);
			TESTileEntityAnimalJar jar = (TESTileEntityAnimalJar) world.getTileEntity(i, j, k);
			if (jar != null) {
				drops.add(itemstack);
			}
		}
		return drops;
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return null;
	}

	private ItemStack getJarDrop(World world, int i, int j, int k, int metadata) {
		ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this), 1, damageDropped(metadata));
		TESTileEntityAnimalJar jar = (TESTileEntityAnimalJar) world.getTileEntity(i, j, k);
		if (jar != null) {
			TESItemAnimalJar.setEntityData(itemstack, jar.getEntityData());
		}
		return itemstack;
	}

	public abstract float getJarEntityHeight();

	@Override
	public int getLightValue(IBlockAccess world, int i, int j, int k) {
		int light;
		TileEntity te = world.getTileEntity(i, j, k);
		if (te instanceof TESTileEntityAnimalJar && (light = ((TESTileEntityAnimalJar) te).getLightValue()) > 0) {
			return light;
		}
		return super.getLightValue(world, i, j, k);
	}

	@Override
	@SuppressWarnings("deprecation")
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int i, int j, int k) {
		world.markBlockForUpdate(i, j, k);
		return getJarDrop(world, i, j, k, world.getBlockMetadata(i, j, k));
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onBlockHarvested(World world, int i, int j, int k, int meta, EntityPlayer entityplayer) {
		int meta1 = meta;
		if (entityplayer.capabilities.isCreativeMode) {
			world.setBlockMetadataWithNotify(i, j, k, meta1 |= 8, 4);
		}
		dropBlockAsItem(world, i, j, k, meta1, 0);
		super.onBlockHarvested(world, i, j, k, meta1, entityplayer);
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		if (!canBlockStay(world, i, j, k)) {
			int meta = world.getBlockMetadata(i, j, k);
			dropBlockAsItem(world, i, j, k, meta, 0);
			world.setBlockToAir(i, j, k);
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
}