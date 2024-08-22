package tes.common.entity.other;

import tes.TES;
import tes.common.entity.ai.TESEntityAIAttackOnCollide;
import tes.common.entity.ai.TESEntityAIFollowHiringPlayer;
import tes.common.entity.ai.TESEntityAIHiredRemainStill;
import tes.common.faction.TESFaction;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.block.Block;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntityBarrowWight extends TESEntityNPC implements TESBiome.ImmuneToFrost {
	private static final Potion[] ATTACK_EFFECTS = {Potion.moveSlowdown, Potion.digSlowdown, Potion.wither};

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBarrowWight(World world) {
		super(world);
		setSize(0.8f, 2.5f);
		getNavigator().setAvoidsWater(true);
		tasks.addTask(0, new EntityAISwimming(this));
		tasks.addTask(1, new TESEntityAIHiredRemainStill(this));
		tasks.addTask(2, getWightAttackAI());
		tasks.addTask(3, new TESEntityAIFollowHiringPlayer(this));
		tasks.addTask(4, new EntityAIWander(this, 1.0));
		tasks.addTask(5, new EntityAIWatchClosest2(this, EntityPlayer.class, 12.0f, 0.02f));
		tasks.addTask(5, new EntityAIWatchClosest2(this, TESEntityNPC.class, 8.0f, 0.02f));
		tasks.addTask(6, new EntityAIWatchClosest(this, EntityLiving.class, 12.0f, 0.02f));
		tasks.addTask(7, new EntityAILookIdle(this));
		addTargetTasks(true);
		isImmuneToFire = true;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(50.0);
		getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(40.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.22);
		getEntityAttribute(NPC_ATTACK_DAMAGE).setBaseValue(6.0);
	}

	@Override
	public boolean attackEntityAsMob(Entity entity) {
		if (super.attackEntityAsMob(entity)) {
			int difficulty;
			int duration;
			if (entity instanceof EntityLivingBase && (duration = (difficulty = worldObj.difficultySetting.getDifficultyId()) * (difficulty + 5) / 2) > 0) {
				for (Potion effect : ATTACK_EFFECTS) {
					((EntityLivingBase) entity).addPotionEffect(new PotionEffect(effect.id, duration * 20, 0));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void dropFewItems(boolean flag, int i) {
		super.dropFewItems(flag, i);
		int bones = 1 + rand.nextInt(3) + rand.nextInt(i + 1);
		for (int l = 0; l < bones; ++l) {
			dropItem(Items.bone, 1);
		}
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(16, -1);
	}

	@Override
	public void func_145780_a(int i, int j, int k, Block block) {
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
	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.UNDEAD;
	}

	@Override
	public String getDeathSound() {
		return "tes:wight.death";
	}

	@Override
	public TESFaction getFaction() {
		return TESFaction.HOSTILE;
	}

	@Override
	public String getHurtSound() {
		return "tes:wight.hurt";
	}

	public int getTargetEntityID() {
		return dataWatcher.getWatchableObjectInt(16);
	}

	private void setTargetEntityID(Entity entity) {
		dataWatcher.updateObject(16, entity == null ? -1 : entity.getEntityId());
	}

	private EntityAIBase getWightAttackAI() {
		return new TESEntityAIAttackOnCollide(this, 1.4, false);
	}

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (worldObj.isRemote) {
			for (int l = 0; l < 1; ++l) {
				double d = posX + width * MathHelper.getRandomDoubleInRange(rand, -0.5, 0.5);
				double d1 = posY + height * MathHelper.getRandomDoubleInRange(rand, 0.4, 0.8);
				double d2 = posZ + width * MathHelper.getRandomDoubleInRange(rand, -0.5, 0.5);
				double d3 = MathHelper.getRandomDoubleInRange(rand, -0.1, 0.1);
				double d4 = MathHelper.getRandomDoubleInRange(rand, -0.2, -0.05);
				double d5 = MathHelper.getRandomDoubleInRange(rand, -0.1, 0.1);
				if (rand.nextBoolean()) {
					TES.proxy.spawnParticle("asshaiTorch", d, d1, d2, d3, d4, d5);
					continue;
				}
				worldObj.spawnParticle("smoke", d, d1, d2, d3, d4, d5);
			}
		}
	}

	@Override
	public void setAttackTarget(EntityLivingBase target, boolean speak) {
		super.setAttackTarget(target, speak);
		if (!worldObj.isRemote) {
			setTargetEntityID(target);
		}
	}
}