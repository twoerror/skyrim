package tes.common.block.other;

import tes.TES;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.Random;

public class TESBlockGrass extends BlockBush implements IShearable {
	private boolean isSandy;

	public TESBlockGrass() {
		super(Material.vine);
		setBlockBounds(0.1f, 0.0f, 0.1f, 0.9f, 0.8f, 0.9f);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
		setHardness(0.0f);
		setStepSound(soundTypeGrass);
	}

	@Override
	public boolean canBlockStay(World world, int i, int j, int k) {
		Block below = world.getBlock(i, j - 1, k);
		return isSandy && below.getMaterial() == Material.sand && below.isSideSolid(world, i, j - 1, k, ForgeDirection.UP) || below.canSustainPlant(world, i, j, k, ForgeDirection.UP, this);
	}

	@Override
	public int getDamageValue(World world, int i, int j, int k) {
		return world.getBlockMetadata(i, j, k);
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
		return Blocks.tallgrass.getDrops(world, i, j, k, meta, fortune);
	}

	@Override
	public int getRenderType() {
		return TES.proxy.getGrassRenderID();
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int i, int j, int k) {
		return true;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int i, int j, int k) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int i, int j, int k, int fortune) {
		ArrayList<ItemStack> list = new ArrayList<>();
		list.add(new ItemStack(this, 1, world.getBlockMetadata(i, j, k)));
		return list;
	}

	@Override
	public int quantityDroppedWithBonus(int i, Random random) {
		return Blocks.tallgrass.quantityDroppedWithBonus(i, random);
	}

	public TESBlockGrass setSandy() {
		isSandy = true;
		return this;
	}
}