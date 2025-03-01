package tes.common.block.leaves;

import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TESBlockLeaves4 extends TESBlockLeavesBase {
	public TESBlockLeaves4() {
		leafNames = new String[]{"chestnut", "baobab", "cedar", "fir"};
	}

	@Override
	public void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
		if ((meta & 3) == 0 && world.rand.nextInt(calcFortuneModifiedDropChance(20, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.chestnut));
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int i, int j, int k, int meta, int fortune) {
		return super.getDrops(world, i, j, k, meta, fortune);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling4);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		super.updateTick(world, i, j, k, random);
		if (!world.isRemote && world.getBlock(i, j, k) == this) {
			int meta = world.getBlockMetadata(i, j, k);
			int leafType = meta & 3;
			boolean playerPlaced = (meta & 4) != 0;
			if (leafType == 0 && !playerPlaced && world.isAirBlock(i, j - 1, k) && random.nextInt(300) == 0) {
				double d = i + random.nextDouble();
				double d1 = j - 0.2;
				double d2 = k + random.nextDouble();
				EntityItem conker = new EntityItem(world, d, d1, d2, new ItemStack(TESItems.chestnut));
				conker.delayBeforeCanPickup = 10;
				conker.motionZ = 0.0;
				conker.motionY = 0.0;
				conker.motionX = 0.0;
				world.spawnEntityInWorld(conker);
			}
		}
	}
}