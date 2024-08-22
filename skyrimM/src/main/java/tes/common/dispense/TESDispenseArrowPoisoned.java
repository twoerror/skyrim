package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityArrowPoisoned;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class TESDispenseArrowPoisoned extends BehaviorProjectileDispense {
	@Override
	public IProjectile getProjectileEntity(World world, IPosition iposition) {
		TESEntityArrowPoisoned arrow = new TESEntityArrowPoisoned(world, iposition.getX(), iposition.getY(), iposition.getZ());
		arrow.canBePickedUp = 1;
		return arrow;
	}
}