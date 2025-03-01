package tes.common.item.weapon;

import tes.TES;
import tes.common.entity.dragon.TESEntityDragon;
import tes.common.entity.other.TESEntitySpiderBase;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class TESItemAsshaiShadowbinderStaff extends TESItemSword {
	public TESItemAsshaiShadowbinderStaff() {
		super(ToolMaterial.WOOD);
		setMaxDamage(1500);
		tesWeaponDamage = 8.0f;
	}

	private static ItemStack useStaff(ItemStack itemstack, World world, EntityLivingBase user) {
		user.swingItem();
		world.playSoundAtEntity(user, "mob.ghast.fireball", 2.0f, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2f + 1.0f);
		if (!world.isRemote) {
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, user.boundingBox.expand(12.0, 8.0, 12.0));
			for (EntityLivingBase entity : entities) {
				if (entity == user || entity instanceof EntityHorse && ((EntityHorse) entity).isTame() || entity instanceof EntityTameable && ((EntityTameable) entity).isTamed() || entity instanceof TESEntitySpiderBase && ((TESEntitySpiderBase) entity).isNPCTamed()) {
					continue;
				}
				float strength = 6.0f - user.getDistanceToEntity(entity) * 0.75f;
				if (strength < 1.0f) {
					strength = 1.0f;
				}
				if (user instanceof EntityPlayer) {
					entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) user), 6.0f * strength);
				} else {
					entity.attackEntityFrom(DamageSource.causeMobDamage(user), 6.0f * strength);
				}
				float knockback = strength;
				if (knockback > 4.0f) {
					knockback = 4.0f;
				}
				if (TES.canPlayerAttackEntity((EntityPlayer) user, entity, false)) {
					entity.addVelocity(-MathHelper.sin(user.rotationYaw * 3.1415927f / 180.0f) * 0.7f * knockback, 0.2 + 0.12 * knockback, MathHelper.cos(user.rotationYaw * 3.1415927f / 180.0f) * 0.7f * knockback);
				}
			}
		}
		return itemstack;
	}

	public static void wizardUseStaff(World world, EntityLivingBase user) {
		user.swingItem();
		world.playSoundAtEntity(user, "mob.ghast.fireball", 2.0f, (itemRand.nextFloat() - itemRand.nextFloat()) * 0.2f + 1.0f);
		if (!world.isRemote) {
			List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, user.boundingBox.expand(12.0, 8.0, 12.0));
			for (EntityLivingBase entity : entities) {
				if (entity == user || entity instanceof EntityHorse && ((EntityHorse) entity).isTame() || entity instanceof TESEntityDragon && ((EntityTameable) entity).isTamed() || entity instanceof EntityTameable && ((EntityTameable) entity).isTamed() || entity instanceof TESEntitySpiderBase && ((TESEntitySpiderBase) entity).isNPCTamed()) {
					continue;
				}
				float strength = 6.0f - user.getDistanceToEntity(entity) * 0.75f;
				if (strength < 1.0f) {
					strength = 1.0f;
				}
				if (user instanceof EntityPlayer) {
					entity.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) user), 6.0f * strength);
				} else {
					entity.attackEntityFrom(DamageSource.causeMobDamage(user), 6.0f * strength);
				}
				float knockback = strength;
				if (knockback > 4.0f) {
					knockback = 4.0f;
				}
				if (TES.canNPCAttackEntity((EntityCreature) user, entity, false)) {
					entity.addVelocity(-MathHelper.sin(user.rotationYaw * 3.1415927f / 180.0f) * 0.7f * knockback, 0.2 + 0.12 * knockback, MathHelper.cos(user.rotationYaw * 3.1415927f / 180.0f) * 0.7f * knockback);
				}
			}
		}
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.bow;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 40;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		itemstack.damageItem(2, entityplayer);
		return useStaff(itemstack, world, entityplayer);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		return itemstack;
	}
}