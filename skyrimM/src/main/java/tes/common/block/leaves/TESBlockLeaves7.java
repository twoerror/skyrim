package tes.common.block.leaves;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.TES;
import tes.common.database.TESBlocks;
import tes.common.database.TESItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class TESBlockLeaves7 extends TESBlockLeavesBase {
	public TESBlockLeaves7() {
		leafNames = new String[]{"aspen", "green_oak", "fotinia", "almond"};
	}

	@Override
	public void addSpecialLeafDrops(List<ItemStack> drops, World world, int meta, int fortune) {
		if ((meta & 3) == 3 && world.rand.nextInt(calcFortuneModifiedDropChance(12, fortune)) == 0) {
			drops.add(new ItemStack(TESItems.almond));
		}
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(TESBlocks.sapling7);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, int i, int j, int k, Random random) {
		super.randomDisplayTick(world, i, j, k, random);
		String s = null;
		int metadata = world.getBlockMetadata(i, j, k) & 3;
		if (metadata == 1 && random.nextInt(150) == 0) {
			s = "leafGreen";
		}
		if (s != null) {
			double d = i + random.nextFloat();
			double d1 = j - 0.05;
			double d2 = k + random.nextFloat();
			double d3 = -0.1 + random.nextFloat() * 0.2f;
			double d4 = -0.03 - random.nextFloat() * 0.02f;
			double d5 = -0.1 + random.nextFloat() * 0.2f;
			TES.proxy.spawnParticle(s, d, d1, d2, d3, d4, d5);
		}
	}
}