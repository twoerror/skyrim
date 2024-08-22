package tes.common.entity.other.inanimate;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class TESEntityThrownTermite extends EntityThrowable {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityThrownTermite(World world) {
		super(world);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityThrownTermite(World world, double d, double d1, double d2) {
		super(world, d, d1, d2);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityThrownTermite(World world, EntityLivingBase throwingEntity) {
		super(world, throwingEntity);
	}

	@Override
	public void onImpact(MovingObjectPosition movingobjectposition) {
		if (!worldObj.isRemote) {
			worldObj.createExplosion(this, posX, posY, posZ, 2.0f, true);
			setDead();
		}
	}
}