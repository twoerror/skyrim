package tes.common.entity.other;

import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.database.TESMaterial;
import tes.common.entity.ai.TESEntityAIAttackOnCollide;
import tes.common.faction.TESFaction;
import tes.common.item.TESMaterialFinder;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntityWerewolf extends TESEntityNPC implements TESBiome.ImmuneToFrost {
	public TESEntityWerewolf(World world) {
		super(world);
		setSize(0.6f, 1.8f);
		getNavigator().setAvoidsWater(true);
		getNavigator().setBreakDoors(true);
		addTargetTasks(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(2, new TESEntityAIAttackOnCollide(this, 1.4, true));
		tasks.addTask(5, new EntityAIWander(this, 1.0));
		tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 8.0f, 0.02f));
		tasks.addTask(7, new EntityAIWatchClosest2(this, TESEntityNPC.class, 5.0f, 0.02f));
		tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0f, 0.02f));
		tasks.addTask(9, new EntityAILookIdle(this));
		spawnsInDarkness = true;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(30.0);
		getEntityAttribute(NPC_ATTACK_DAMAGE).setBaseValue(5.0);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		ItemStack itemstack;
		Entity entity = damagesource.getEntity();
		if (entity instanceof EntityLivingBase && entity == damagesource.getSourceOfDamage() && (itemstack = ((EntityLivingBase) entity).getHeldItem()) != null && ((EntityLivingBase) entity).getHeldItem().getItem() instanceof TESMaterialFinder && (((TESMaterialFinder) itemstack.getItem()).getMaterial() == TESMaterial.SILVER_TOOL || itemstack.getItem() == TESItems.crowbar)) {
			return super.attackEntityFrom(damagesource, f);
		}
		return super.attackEntityFrom(damagesource, 1);
	}

	@Override
	public float getAlignmentBonus() {
		return 2.0f;
	}

	@Override
	public boolean getCanSpawnHere() {
		if (super.getCanSpawnHere()) {
			if (liftSpawnRestrictions) {
				return true;
			}
			int i = MathHelper.floor_double(posX);
			int j = MathHelper.floor_double(boundingBox.minY);
			int k = MathHelper.floor_double(posZ);
			return j > 62 && j < 140 && worldObj.getBlock(i, j - 1, k) == TESCrashHandler.getBiomeGenForCoords(worldObj, i, k).topBlock;
		}
		return false;
	}

	@Override
	public String getDeathSound() {
		return "tes:direwolf.death";
	}

	@Override
	public TESFaction getFaction() {
		return TESFaction.HOSTILE;
	}

	@Override
	public String getHurtSound() {
		return "tes:direwolf.hurt";
	}

	@Override
	public TESAchievement getKillAchievement() {
		return TESAchievement.killWerewolf;
	}

	@Override
	public String getLivingSound() {
		return "tes:direwolf.say";
	}

	@Override
	public float getSoundVolume() {
		return 0.5f;
	}
}