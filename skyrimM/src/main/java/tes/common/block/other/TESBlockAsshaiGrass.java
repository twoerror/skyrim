package tes.common.block.other;

import tes.TES;
import tes.common.database.TESCreativeTabs;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import java.util.ArrayList;
import java.util.Random;

public class TESBlockAsshaiGrass extends BlockBush implements IShearable {
	public TESBlockAsshaiGrass() {
		super(Material.vine);
		setBlockBounds(0.1f, 0.0f, 0.1f, 0.9f, 0.8f, 0.9f);
		setCreativeTab(TESCreativeTabs.TAB_DECO);
		setHardness(0.0f);
		setStepSound(soundTypeGrass);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return null;
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
}