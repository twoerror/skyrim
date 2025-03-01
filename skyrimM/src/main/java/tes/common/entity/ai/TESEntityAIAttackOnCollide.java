package tes.common.entity.ai;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.inanimate.TESEntitySpear;
import tes.common.item.TESWeaponStats;
import tes.common.item.weapon.TESItemSpear;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntityAIAttackOnCollide extends EntityAIBase {
	private final World worldObj;
	private final EntityCreature theOwner;
	private final double moveSpeed;
	private final boolean sightNotRequired;
	private final boolean avoidsWater;

	private EntityLivingBase attackTarget;
	private PathEntity entityPathEntity;
	private int attackTick;
	private int pathCheckTimer;

	public TESEntityAIAttackOnCollide(EntityCreature entity, double speed, boolean flag) {
		theOwner = entity;
		worldObj = entity.worldObj;
		moveSpeed = speed;
		sightNotRequired = flag;
		avoidsWater = entity.getNavigator().getAvoidsWater();
		setMutexBits(3);
	}

	@Override
	public boolean continueExecuting() {
		if (!theOwner.isEntityAlive()) {
			return false;
		}
		attackTarget = theOwner.getAttackTarget();
		if (attackTarget == null || !attackTarget.isEntityAlive()) {
			return false;
		}
		if (sightNotRequired) {
			return theOwner.isWithinHomeDistance(MathHelper.floor_double(attackTarget.posX), MathHelper.floor_double(attackTarget.posY), MathHelper.floor_double(attackTarget.posZ));
		}
		return !theOwner.getNavigator().noPath();
	}

	private PathEntity getPathEntity() {
		if (theOwner.ridingEntity != null) {
			return worldObj.getPathEntityToEntity(theOwner, attackTarget, theOwner.getNavigator().getPathSearchRange(), true, theOwner.getNavigator().getCanBreakDoors(), theOwner.getNavigator().getAvoidsWater(), false);
		}
		return theOwner.getNavigator().getPathToEntityLiving(attackTarget);
	}

	@Override
	public void resetTask() {
		attackTarget = null;
		theOwner.getNavigator().clearPathEntity();
		theOwner.getNavigator().setAvoidsWater(avoidsWater);
	}

	@Override
	public boolean shouldExecute() {
		if (theOwner instanceof TESEntityNPC && ((TESEntityNPC) theOwner).getFamilyInfo().isChild()) {
			return false;
		}
		EntityLivingBase entity = theOwner.getAttackTarget();
		if (entity == null) {
			return false;
		}
		attackTarget = entity;
		theOwner.getNavigator().setAvoidsWater(false);
		entityPathEntity = getPathEntity();
		if (entityPathEntity != null) {
			return true;
		}
		theOwner.getNavigator().setAvoidsWater(avoidsWater);
		return false;
	}

	@Override
	public void startExecuting() {
		theOwner.getNavigator().setPath(entityPathEntity, moveSpeed);
		pathCheckTimer = 0;
	}

	private void updateLookAndPathing() {
		theOwner.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0f, 30.0f);
		if (theOwner.riddenByEntity instanceof EntityLiving) {
			theOwner.riddenByEntity.rotationYaw = theOwner.rotationYaw;
			((EntityLivingBase) theOwner.riddenByEntity).rotationYawHead = theOwner.rotationYawHead;
		}
		if (sightNotRequired || theOwner.getEntitySenses().canSee(attackTarget)) {
			--pathCheckTimer;
			if (pathCheckTimer <= 0) {
				pathCheckTimer = 10 + theOwner.getRNG().nextInt(10);
				PathEntity path = getPathEntity();
				if (path != null) {
					theOwner.getNavigator().setPath(path, moveSpeed);
				}
			}
		}
	}

	@Override
	public void updateTask() {
		updateLookAndPathing();
		if (attackTick > 0) {
			--attackTick;
		}
		ItemStack weapon = theOwner.getHeldItem();
		if (weapon != null && weapon.getItem() instanceof TESItemSpear && attackTick <= 0 && theOwner instanceof TESEntityNPC) {
			TESEntityNPC theNPC = (TESEntityNPC) theOwner;
			ItemStack spearBackup = theNPC.getNpcItemsInv().getSpearBackup();
			if (spearBackup != null) {
				weapon.getItem();
				double d = theOwner.getDistanceToEntity(attackTarget);
				double range = theOwner.getNavigator().getPathSearchRange();
				if (d > 5.0 && d < range * 0.75) {
					TESEntitySpear spear = new TESEntitySpear(worldObj, theOwner, attackTarget, weapon.copy(), 0.75f + (float) d * 0.025f, 0.5f);
					worldObj.playSoundAtEntity(theOwner, "random.bow", 1.0f, 1.0f / (worldObj.rand.nextFloat() * 0.4f + 1.2f) + 0.25f);
					worldObj.spawnEntityInWorld(spear);
					attackTick = 30 + theOwner.getRNG().nextInt(20);
					if (ItemStack.areItemStacksEqual(theNPC.getNpcItemsInv().getIdleItem(), theNPC.getNpcItemsInv().getMeleeWeapon())) {
						theNPC.getNpcItemsInv().setIdleItem(spearBackup);
					}
					theNPC.getNpcItemsInv().setMeleeWeapon(spearBackup);
					theNPC.getNpcItemsInv().setSpearBackup(null);
					return;
				}
			}
		}
		float weaponReach = 1.0f;
		if (theOwner.ridingEntity != null) {
			weaponReach = TESEntityNPC.MOUNT_RANGE_BONUS;
		}
		float meleeRange = (float) theOwner.boundingBox.getAverageEdgeLength() + weaponReach * TESWeaponStats.getMeleeReachFactor(theOwner.getHeldItem());
		if (theOwner.getDistanceSqToEntity(attackTarget) <= meleeRange * meleeRange && attackTick <= 0) {
			attackTick = TESWeaponStats.getAttackTimeMob(weapon);
			theOwner.attackEntityAsMob(attackTarget);
			theOwner.swingItem();
		}
	}
}