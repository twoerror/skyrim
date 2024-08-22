package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityConker;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class TESDispenseConker extends BehaviorProjectileDispense {
	@Override
	public IProjectile getProjectileEntity(World world, IPosition position) {
		return new TESEntityConker(world, position.getX(), position.getY(), position.getZ());
	}
}