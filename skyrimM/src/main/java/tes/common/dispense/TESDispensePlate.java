package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityPlate;
import net.minecraft.block.Block;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class TESDispensePlate extends BehaviorProjectileDispense {
	private final Block plateBlock;

	public TESDispensePlate(Block block) {
		plateBlock = block;
	}

	@Override
	public IProjectile getProjectileEntity(World world, IPosition position) {
		return new TESEntityPlate(world, plateBlock, position.getX(), position.getY(), position.getZ());
	}
}