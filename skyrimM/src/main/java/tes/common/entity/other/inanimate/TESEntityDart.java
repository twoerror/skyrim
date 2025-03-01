package tes.common.entity.other.inanimate;

import tes.common.item.other.TESItemDart;
import tes.common.item.weapon.TESItemSword;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntityDart extends TESEntityProjectileBase {
	private float dartDamageFactor = 1.0f;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityDart(World world) {
		super(world);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityDart(World world, EntityLivingBase entityliving, EntityLivingBase target, ItemStack item, float charge, float inaccuracy) {
		super(world, entityliving, target, item, charge, inaccuracy);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityDart(World world, EntityLivingBase entityliving, ItemStack item, float charge) {
		super(world, entityliving, item, charge);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityDart(World world, ItemStack item, double d, double d1, double d2) {
		super(world, item, d, d1, d2);
	}

	@Override
	public float getBaseImpactDamage(Entity entity, ItemStack itemstack) {
		float speed = MathHelper.sqrt_double(motionX * motionX + motionY * motionY + motionZ * motionZ);
		return speed * dartDamageFactor;
	}

	@Override
	public float getKnockbackFactor() {
		return 0.5f;
	}

	@Override
	public int maxTicksInGround() {
		return 1200;
	}

	@Override
	public void onCollideWithTarget(Entity entity) {
		Item item;
		ItemStack itemstack;
		if (!worldObj.isRemote && entity instanceof EntityLivingBase && (itemstack = getProjectileItem()) != null && (item = itemstack.getItem()) instanceof TESItemDart && ((TESItemDart) item).isPoisoned()) {
			TESItemSword.applyStandardPoison((EntityLivingBase) entity);
		}
		super.onCollideWithTarget(entity);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		dartDamageFactor = nbt.getFloat("DartDamage");
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setFloat("DartDamage", dartDamageFactor);
	}

	public float getDartDamageFactor() {
		return dartDamageFactor;
	}

	public void setDartDamageFactor(float dartDamageFactor) {
		this.dartDamageFactor = dartDamageFactor;
	}
}