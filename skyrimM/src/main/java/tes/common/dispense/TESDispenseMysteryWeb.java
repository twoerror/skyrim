package tes.common.dispense;

import tes.common.entity.other.inanimate.TESEntityMysteryWeb;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.world.World;

public class TESDispenseMysteryWeb extends BehaviorProjectileDispense {
	@Override
	public IProjectile getProjectileEntity(World world, IPosition position) {
		return new TESEntityMysteryWeb(world, position.getX(), position.getY(), position.getZ());
	}
}