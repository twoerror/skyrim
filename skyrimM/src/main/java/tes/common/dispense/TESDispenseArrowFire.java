package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityArrowFire;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class TESDispenseArrowFire extends BehaviorProjectileDispense {
	@Override
	public IProjectile getProjectileEntity(World world, IPosition iposition) {
		TESEntityArrowFire arrow = new TESEntityArrowFire(world, iposition.getX(), iposition.getY(), iposition.getZ());
		arrow.canBePickedUp = 1;
		return arrow;
	}
}