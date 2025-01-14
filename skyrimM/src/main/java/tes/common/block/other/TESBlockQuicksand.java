package tes.common.block.other;

import tes.common.database.TESCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class TESBlockQuicksand extends Block {
	public TESBlockQuicksand() {
		super(Material.ground);
		setCreativeTab(TESCreativeTabs.TAB_BLOCK);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int i, int j, int k, ForgeDirection direction, IPlantable plantable) {
		return Blocks.dirt.canSustainPlant(world, i, j, k, direction, plantable) || plantable instanceof BlockStem;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return null;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		entity.setInWeb();
	}
}