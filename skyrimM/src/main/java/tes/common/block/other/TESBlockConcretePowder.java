package tes.common.block.other;

import tes.common.database.TESBlocks;
import tes.common.database.TESCreativeTabs;
import tes.common.entity.other.inanimate.TESEntityFallingConcrete;
import tes.common.util.TESEnumDyeColor;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

import java.util.Random;

public class TESBlockConcretePowder extends BlockFalling {
	private final TESEnumDyeColor color;

	public TESBlockConcretePowder(TESEnumDyeColor color) {
		this.color = color;
		setBlockName("concrete_powder_" + this.color);
		setStepSound(soundTypeSand);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
		setHardness(0.5f);
		setBlockTextureName("tes:concrete_powder_" + this.color.getName());
	}

	private static TESBlockConcrete getConcreteFromColor(TESEnumDyeColor dye) {
		return TESBlocks.CONCRETE.get(dye);
	}

	public TESEnumDyeColor getColor() {
		return color;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		if (world.getBlock(i + 1, j, k).getMaterial() == Material.water || world.getBlock(i - 1, j, k).getMaterial() == Material.water || world.getBlock(i, j + 1, k).getMaterial() == Material.water || world.getBlock(i, j - 1, k).getMaterial() == Material.water || world.getBlock(i, j, k + 1).getMaterial() == Material.water || world.getBlock(i, j, k - 1).getMaterial() == Material.water) {
			world.setBlock(i, j, k, getConcreteFromColor(color));
		} else {
			world.scheduleBlockUpdate(i, j, k, this, tickRate(world));
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		if (world.getBlock(i + 1, j, k).getMaterial() == Material.water || world.getBlock(i - 1, j, k).getMaterial() == Material.water || world.getBlock(i, j + 1, k).getMaterial() == Material.water || world.getBlock(i, j - 1, k).getMaterial() == Material.water || world.getBlock(i, j, k + 1).getMaterial() == Material.water || world.getBlock(i, j, k - 1).getMaterial() == Material.water) {
			world.setBlock(i, j, k, getConcreteFromColor(color));
		} else {
			world.scheduleBlockUpdate(i, j, k, this, tickRate(world));
		}
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random rand) {
		if (!world.isRemote) {
			int j1 = j;
			if (func_149831_e(world, i, j1 - 1, k) && j1 >= 0) {
				int b0 = 32;
				if (!fallInstantly && world.checkChunksExist(i - b0, j1 - b0, k - b0, i + b0, j1 + b0, k + b0)) {
					if (!world.isRemote) {
						TESEntityFallingConcrete entityfallingblock = new TESEntityFallingConcrete(world, i + 0.5f, j1 + 0.5f, k + 0.5f, this, world.getBlockMetadata(i, j1, k));
						func_149829_a(entityfallingblock);
						world.spawnEntityInWorld(entityfallingblock);
					}
				} else {
					world.setBlockToAir(i, j1, k);
					while (func_149831_e(world, i, j1 - 1, k) && j1 > 0) {
						--j1;
					}
					if (j1 > 0) {
						world.setBlock(i, j1, k, this);
					}
				}
			}
		}
	}
}