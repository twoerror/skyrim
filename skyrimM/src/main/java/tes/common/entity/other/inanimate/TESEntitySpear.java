package tes.common.entity.other.inanimate;

import tes.common.item.weapon.TESItemSpear;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntitySpear extends TESEntityProjectileBase {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntitySpear(World world) {
		super(world);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntitySpear(World world, EntityLivingBase entityliving, EntityLivingBase target, ItemStack item, float charge, float inaccuracy) {
		super(world, entityliving, target, item, charge, inaccuracy);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntitySpear(World world, EntityLivingBase entityliving, ItemStack item, float charge) {
		super(world, entityliving, item, charge);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntitySpear(World world, ItemStack item, double d, double d1, double d2) {
		super(world, item, d, d1, d2);
	}

	@Override
	public float getBaseImpactDamage(Entity entity, ItemStack itemstack) {
		float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
		float damage = ((TESItemSpear) itemstack.getItem()).getRangedDamageMultiplier(itemstack, shootingEntity, entity);
		return speed * damage;
	}
}