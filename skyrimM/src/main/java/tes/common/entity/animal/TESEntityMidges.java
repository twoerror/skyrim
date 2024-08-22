package tes.common.entity.animal;

import tes.common.TESLevelData;
import tes.common.database.TESAchievement;
import tes.common.database.TESItems;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESAmbientCreature;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import java.util.List;

public class TESEntityMidges extends EntityLiving implements TESAmbientCreature, TESBiome.ImmuneToFrost, TESBiome.ImmuneToHeat {
	private final Midge[] midges;

	private ChunkCoordinates currentFlightTarget;
	private EntityPlayer playerTarget;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityMidges(World world) {
		super(world);
		setSize(2.0f, 2.0f);
		renderDistanceWeight = 0.5;
		midges = new Midge[3 + rand.nextInt(6)];
		for (int l = 0; l < midges.length; ++l) {
			midges[l] = new Midge();
		}
	}

	@Override
	public boolean allowLeashing() {
		return false;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(2.0);
	}

	@Override
	public boolean canBePushed() {
		return false;
	}

	@Override
	public boolean canDespawn() {
		return true;
	}

	@Override
	public boolean canTriggerWalking() {
		return false;
	}

	@Override
	public void collideWithEntity(Entity entity) {
	}

	@Override
	public void collideWithNearbyEntities() {
	}

	@Override
	public boolean doesEntityNotTriggerPressurePlate() {
		return true;
	}

	@Override
	public void fall(float f) {
	}

	@Override
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(posX);
		int j = MathHelper.floor_double(posY);
		int k = MathHelper.floor_double(posZ);
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		return biome.temperature != 0.0f && j >= 62 && worldObj.getBlock(i, j - 1, k) == biome.topBlock && super.getCanSpawnHere();
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		int id = TESEntityRegistry.getEntityID(this);
		if (id > 0 && TESEntityRegistry.SPAWN_EGGS.containsKey(id)) {
			return new ItemStack(TESItems.spawnEgg, 1, id);
		}
		return null;
	}

	@Override
	public boolean interact(EntityPlayer entityplayer) {
		return false;
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	@Override
	public void onDeath(DamageSource damagesource) {
		Entity attacker;
		super.onDeath(damagesource);
		if (!worldObj.isRemote && damagesource instanceof EntityDamageSourceIndirect && (attacker = damagesource.getEntity()) instanceof TESEntityNPC) {
			TESEntityNPC npc = (TESEntityNPC) attacker;
			if (npc.getHireableInfo().isActive() && npc.getHireableInfo().getHiringPlayer() != null) {
				EntityPlayer entityplayer = npc.getHireableInfo().getHiringPlayer();
				TESLevelData.getData(entityplayer).addAchievement(TESAchievement.shootDownMidges);
			}
		}
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		motionY *= 0.6;
		for (Midge midge : midges) {
			midge.update();
		}
		if (rand.nextInt(5) == 0) {
			playSound("tes:midges.swarm", getSoundVolume(), getSoundPitch());
		}
		
	}

	@Override
	public void updateAITasks() {
		super.updateAITasks();
		if (currentFlightTarget != null && !worldObj.isAirBlock(currentFlightTarget.posX, currentFlightTarget.posY, currentFlightTarget.posZ)) {
			currentFlightTarget = null;
		}
		if (playerTarget != null && (!playerTarget.isEntityAlive() || getDistanceSqToEntity(playerTarget) > 256.0)) {
			playerTarget = null;
		}
		if (playerTarget != null) {
			if (rand.nextInt(400) == 0) {
				playerTarget = null;
			} else {
				currentFlightTarget = new ChunkCoordinates((int) playerTarget.posX, (int) playerTarget.posY + 3, (int) playerTarget.posZ);
			}
		} else if (rand.nextInt(100) == 0) {
			EntityPlayer closestPlayer = worldObj.getClosestPlayerToEntity(this, 12.0);
			if (closestPlayer != null && rand.nextInt(7) == 0) {
				playerTarget = closestPlayer;
			} else {
				int i = (int) posX + rand.nextInt(7) - rand.nextInt(7);
				int j = (int) posY + rand.nextInt(4) - rand.nextInt(3);
				int k = (int) posZ + rand.nextInt(7) - rand.nextInt(7);
				if (j < 1) {
					j = 1;
				}
				int height = worldObj.getTopSolidOrLiquidBlock(i, k);
				if (j > height + 8) {
					j = height + 8;
				}
				currentFlightTarget = new ChunkCoordinates(i, j, k);
			}
		}
		if (currentFlightTarget != null) {
			double dx = currentFlightTarget.posX + 0.5 - posX;
			double dy = currentFlightTarget.posY + 0.5 - posY;
			double dz = currentFlightTarget.posZ + 0.5 - posZ;
			motionX += (Math.signum(dx) * 0.5 - motionX) * 0.1;
			motionY += (Math.signum(dy) * 0.7 - motionY) * 0.1;
			motionZ += (Math.signum(dz) * 0.5 - motionZ) * 0.1;
			moveForward = 0.2f;
		} else {
			motionZ = 0.0;
			motionY = 0.0;
			motionX = 0.0;
		}
	}

	@Override
	public void updateFallState(double d, boolean flag) {
	}

	public Midge[] getMidges() {
		return midges;
	}

	public class Midge {
		private static final int MAX_MIDGE_TICK = 80;
		private final float midgeInitialPosY;
		private final float midgeRotation;
		private final float midgePosX;
		private final float midgePosZ;

		private float midgePrevPosX;
		private float midgePrevPosY;
		private float midgePrevPosZ;
		private float midgePosY;
		private int midgeTick;

		protected Midge() {
			midgePosX = -1.0F + rand.nextFloat() * 2.0F;
			midgePosY = rand.nextFloat() * 2.0F;
			midgePosZ = -1.0F + rand.nextFloat() * 2.0F;
			midgeInitialPosY = midgePosY = rand.nextFloat() * 2.0f;
			midgeRotation = rand.nextFloat() * 360.0f;
			midgeTick = rand.nextInt(MAX_MIDGE_TICK);
		}

		protected void update() {
			midgePrevPosX = midgePosX;
			midgePrevPosY = midgePosY;
			midgePrevPosZ = midgePosZ;
			++midgeTick;
			if (midgeTick > MAX_MIDGE_TICK) {
				midgeTick = 0;
			}
			midgePosY = midgeInitialPosY + 0.5f * MathHelper.sin(midgeTick / 6.2831855f);
		}

		public float getMidgePosX() {
			return midgePosX;
		}

		public float getMidgePosY() {
			return midgePosY;
		}

		public float getMidgePosZ() {
			return midgePosZ;
		}

		public float getMidgePrevPosX() {
			return midgePrevPosX;
		}

		public float getMidgePrevPosY() {
			return midgePrevPosY;
		}

		public float getMidgePrevPosZ() {
			return midgePrevPosZ;
		}

		public float getMidgeRotation() {
			return midgeRotation;
		}
	}
}