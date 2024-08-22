package tes.common.entity.dragon.helper;

import tes.common.entity.dragon.TESDragonLifeStage;
import tes.common.entity.dragon.TESDragonScaleModifier;
import tes.common.entity.dragon.TESEntityDragon;
import tes.common.util.TESLog;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TESDragonLifeStageHelper extends TESDragonHelper {
	private final TESDragonScaleModifier scaleModifier = new TESDragonScaleModifier();

	private TESDragonLifeStage lifeStagePrev;
	private int eggWiggleX;
	private int eggWiggleZ;

	public TESDragonLifeStageHelper(TESEntityDragon dragon) {
		super(dragon);
	}

	@Override
	public void applyEntityAttributes() {
		dragon.getEntityAttribute(SharedMonsterAttributes.maxHealth).applyModifier(scaleModifier);
		dragon.getEntityAttribute(SharedMonsterAttributes.attackDamage).applyModifier(scaleModifier);
	}

	public int getEggWiggleX() {
		return eggWiggleX;
	}

	public int getEggWiggleZ() {
		return eggWiggleZ;
	}

	private TESDragonLifeStage getLifeStage() {
		int age = dragon.getGrowingAge();
		if (age >= TESDragonLifeStage.ADULT.getAgeLimit()) {
			return TESDragonLifeStage.ADULT;
		}
		if (age >= TESDragonLifeStage.JUVENILE.getAgeLimit()) {
			return TESDragonLifeStage.JUVENILE;
		}
		if (age >= TESDragonLifeStage.HATCHLING.getAgeLimit()) {
			return TESDragonLifeStage.HATCHLING;
		}
		return TESDragonLifeStage.EGG;
	}

	public void setLifeStage(TESDragonLifeStage lifeStage) {
		TESLog.getLogger().trace("setLifeStage({})", lifeStage);
		dragon.setGrowingAge(lifeStage.getAgeLimit());
		updateLifeStage();
	}

	public float getScale() {
		if (isEgg()) {
			return 0.9f / TESEntityDragon.BASE_WIDTH;
		}
		return 1 - dragon.getGrowingAge() / (float) TESDragonLifeStage.EGG.getAgeLimit();
	}

	public boolean isAdult() {
		return getLifeStage() == TESDragonLifeStage.ADULT;
	}

	public boolean isEgg() {
		return getLifeStage() == TESDragonLifeStage.EGG;
	}

	public boolean isHatchling() {
		return getLifeStage() == TESDragonLifeStage.HATCHLING;
	}

	@Override
	public void onDeath() {
		if (dragon.isClient() && isEgg()) {
			playEggCrackEffect();
		}
	}

	@Override
	public void onDeathUpdate() {
	}

	@Override
	public void onLivingUpdate() {
		updateLifeStage();
		updateEgg();
		updateScale();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
	}

	private void onNewLifeStage(TESDragonLifeStage lifeStage, TESDragonLifeStage prevLifeStage) {
		TESLog.getLogger().trace("onNewLifeStage({},{})", prevLifeStage, lifeStage);
		if (dragon.isClient()) {
			if (prevLifeStage == TESDragonLifeStage.EGG && lifeStage == TESDragonLifeStage.HATCHLING) {
				playEggCrackEffect();
			}
		} else {
			dragon.setCanFly(lifeStage != TESDragonLifeStage.EGG && lifeStage != TESDragonLifeStage.HATCHLING);
			dragon.getNavigator().setEnterDoors(lifeStage == TESDragonLifeStage.HATCHLING);
			if (lifeStage == TESDragonLifeStage.EGG) {
				dragon.setPathToEntity(null);
				dragon.setAttackTarget(null);
			}
			IAttributeInstance healthAttrib = dragon.getEntityAttribute(SharedMonsterAttributes.maxHealth);
			IAttributeInstance damageAttrib = dragon.getEntityAttribute(SharedMonsterAttributes.attackDamage);
			healthAttrib.removeModifier(scaleModifier);
			damageAttrib.removeModifier(scaleModifier);
			healthAttrib.applyModifier(scaleModifier);
			damageAttrib.applyModifier(scaleModifier);
			dragon.setHealth(dragon.getMaxHealth());
		}
	}

	private void playEggCrackEffect() {
		int bx = (int) Math.round(dragon.posX - 0.5);
		int by = (int) Math.round(dragon.posY);
		int bz = (int) Math.round(dragon.posZ - 0.5);
		dragon.worldObj.playAuxSFX(2001, bx, by, bz, Block.getIdFromBlock(Blocks.dragon_egg));
	}

	public void transformToEgg() {
		if (dragon.getHealth() <= 0) {
			return;
		}
		TESLog.getLogger().debug("transforming to egg");
		float volume = 1;
		float pitch = 0.5f + (0.5f - rand.nextFloat()) * 0.1f;
		dragon.worldObj.playSoundAtEntity(dragon, "mob.endermen.portal", volume, pitch);
		if (dragon.isSaddled()) {
			dragon.dropItem(Items.saddle, 1);
		}
		dragon.entityDropItem(new ItemStack(Blocks.dragon_egg), 0);
		dragon.setDead();
	}

	private void updateEgg() {
		if (!isEgg()) {
			return;
		}
		int age = dragon.getGrowingAge();
		int eggAge = TESDragonLifeStage.EGG.getAgeLimit();
		int hatchAge = TESDragonLifeStage.HATCHLING.getAgeLimit();
		float chance = (age - eggAge) / (float) (hatchAge - eggAge);
		if (chance > 0.66f) {
			chance /= 60;
			if (eggWiggleX > 0) {
				eggWiggleX--;
			} else if (rand.nextFloat() < chance) {
				eggWiggleX = rand.nextBoolean() ? 10 : 20;
				playEggCrackEffect();
			}
			if (eggWiggleZ > 0) {
				eggWiggleZ--;
			} else if (rand.nextFloat() < chance) {
				eggWiggleZ = rand.nextBoolean() ? 10 : 20;
				playEggCrackEffect();
			}
		}
		rand.nextDouble();
		rand.nextDouble();
		rand.nextDouble();
		rand.nextDouble();
		rand.nextDouble();
		rand.nextDouble();
	}

	private void updateLifeStage() {
		TESDragonLifeStage lifeStage = getLifeStage();
		if (lifeStagePrev != lifeStage) {
			onNewLifeStage(lifeStage, lifeStagePrev);
			lifeStagePrev = lifeStage;
		}
	}

	private void updateScale() {
		dragon.setScalePublic(getScale());
	}
}