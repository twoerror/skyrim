package tes.common.entity.animal;

import tes.common.database.TESItems;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.ai.TESEntityAIAttackOnCollide;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import tes.common.entity.other.utils.TESEntityUtils;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class TESEntityBison extends EntityCow implements TESRandomSkinEntity, TESBiome.ImmuneToFrost {
	private final EntityAIBase attackAI;
	private final EntityAIBase panicAI;

	private boolean prevIsChild = true;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBison(World world) {
		super(world);
		float bisonWidth = 1.5f;
		float bisonHeight = 1.7f;
		setSize(bisonWidth, bisonHeight);
		EntityAITasks.EntityAITaskEntry panic = TESEntityUtils.removeAITask(this, EntityAIPanic.class);
		tasks.addTask(panic.priority, panic.action);
		panicAI = panic.action;
		attackAI = new TESEntityAIAttackOnCollide(this, 1.4, true);
		targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(30.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
		getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(4.0);
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		float f = (float) getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
		if (flag) {
			float kb = 0.75f;
			entity.addVelocity(-MathHelper.sin(rotationYaw * 3.1415927f / 180.0f) * kb * 0.5f, 0.0, MathHelper.cos(rotationYaw * 3.1415927f / 180.0f) * kb * 0.5f);
		}
		return flag;
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		Entity attacker;
		boolean flag = super.attackEntityFrom(damagesource, f);
		if (flag && isChild() && (attacker = damagesource.getEntity()) instanceof EntityLivingBase) {
			List<? extends Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox.expand(12.0, 12.0, 12.0));
			for (Entity element : list) {
				if (element instanceof TESEntityBison && !((EntityLivingBase) element).isChild()) {
					((EntityLiving) element).setAttackTarget((EntityLivingBase) attacker);
				}
			}
		}
		return flag;
	}

	@Override
	public EntityCow createChild(EntityAgeable entity) {
		return new TESEntityBison(worldObj);
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		int hides = 2 + rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < hides; ++l) {
			dropItem(Items.leather, 1);
		}
		int meats = 2 + rand.nextInt(3) + rand.nextInt(1 + i);
		for (int l = 0; l < meats; ++l) {
			if (isBurning()) {
				dropItem(Items.cooked_beef, 1);
				continue;
			}
			dropItem(Items.beef, 1);
		}
		dropHornItem();
	}

	protected void dropHornItem() {
		dropItem(TESItems.horn, 1);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(20, (byte) 0);
	}

	@Override
	public boolean getCanSpawnHere() {
		if (super.getCanSpawnHere()) {
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(boundingBox.minY);
			int k = MathHelper.floor_double(posZ);
			return j > 62 && j < 140 && worldObj.getBlock(i, j - 1, k) == TESCrashHandler.getBiomeGenForCoords(worldObj, i, k).topBlock;
		}
		return false;
	}

	@Override
	public String getDeathSound() {
		return "tes:bison.hurt";
	}

	@Override
	public String getHurtSound() {
		return "tes:bison.hurt";
	}

	@Override
	public String getLivingSound() {
		return "tes:bison.say";
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(TESItems.spawnEgg, 1, TESEntityRegistry.getEntityID(this));
	}

	@Override
	public float getSoundPitch() {
		return super.getSoundPitch() * 0.75f;
	}

	@Override
	public float getSoundVolume() {
		return 1.0f;
	}

	@Override
	public int getTalkInterval() {
		return 200;
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		return !isBisonEnraged() && super.interact(entityplayer);
	}

	public boolean isBisonEnraged() {
		return dataWatcher.getWatchableObjectByte(20) == 1;
	}

	private void setBisonEnraged(boolean flag) {
		dataWatcher.updateObject(20, flag ? (byte) 1 : 0);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (!worldObj.isRemote) {
			EntityLivingBase target;
			boolean isChild = isChild();
			if (isChild != prevIsChild) {
				EntityAITasks.EntityAITaskEntry taskEntry;
				if (isChild) {
					taskEntry = TESEntityUtils.removeAITask(this, attackAI.getClass());
					tasks.addTask(taskEntry.priority, panicAI);
				} else {
					taskEntry = TESEntityUtils.removeAITask(this, panicAI.getClass());
					tasks.addTask(taskEntry.priority, attackAI);
				}
			}
			if (getAttackTarget() != null && (!(target = getAttackTarget()).isEntityAlive() || target instanceof EntityPlayer && ((EntityPlayer) target).capabilities.isCreativeMode)) {
				setAttackTarget(null);
			}
			if (riddenByEntity instanceof EntityLiving) {
				target = ((EntityLiving) riddenByEntity).getAttackTarget();
				setAttackTarget(target);
			} else if (riddenByEntity instanceof EntityPlayer) {
				setAttackTarget(null);
			}
			setBisonEnraged(getAttackTarget() != null);
		}
		prevIsChild = isChild();
	}

	@Override
	public void setUniqueID(UUID uuid) {
		entityUniqueID = uuid;
	}
}