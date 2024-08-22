package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityThrownTermite;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class TESDispenseTermite extends BehaviorProjectileDispense {
	@Override
	public IProjectile getProjectileEntity(World world, IPosition position) {
		return new TESEntityThrownTermite(world, position.getX(), position.getY(), position.getZ());
	}
}