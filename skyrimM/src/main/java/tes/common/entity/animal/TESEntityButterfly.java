package tes.common.entity.animal;

import tes.common.database.TESItems;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.iface.TESAmbientCreature;
import tes.common.entity.other.iface.TESRandomSkinEntity;
import tes.common.entity.other.utils.TESAmbientSpawnChecks;
import tes.common.util.TESCrashHandler;
import tes.common.world.biome.TESBiome;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.UUID;

public class TESEntityButterfly extends EntityLiving implements TESAmbientCreature, TESRandomSkinEntity, TESBiome.ImmuneToFrost, TESBiome.ImmuneToHeat {
	private ChunkCoordinates currentFlightTarget;
	private int flapTime;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityButterfly(World world) {
		super(world);
		setSize(0.5f, 0.5f);
	}

	@Override
	public boolean allowLeashing() {
		return false;
	}

	@Override
	public void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(2.0);
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(MathHelper.getRandomDoubleInRange(rand, 0.08, 0.12));
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		boolean flag = super.attackEntityFrom(damagesource, f);
		if (flag && !worldObj.isRemote && isButterflyStill()) {
			setButterflyStill(false);
		}
		return flag;
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
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(16, (byte) 0);
		dataWatcher.addObject(17, (byte) 0);
	}

	@Override
	public void fall(float f) {
	}

	public ButterflyType getButterflyType() {
		byte i = dataWatcher.getWatchableObjectByte(16);
		if (i < 0 || i >= ButterflyType.values().length) {
			i = 0;
		}
		return ButterflyType.values()[i];
	}

	private void setButterflyType(ButterflyType type) {
		setButterflyType(type.ordinal());
	}

	private void setButterflyType(int i) {
		dataWatcher.updateObject(16, (byte) i);
	}

	@Override
	public boolean getCanSpawnHere() {
		int i = MathHelper.floor_double(posX);
		int k = MathHelper.floor_double(posZ);
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		return biome.temperature != 0.0f && super.getCanSpawnHere() && TESAmbientSpawnChecks.canSpawn(this, 8, 4, 32, 4, Material.plants, Material.vine);
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return new ItemStack(TESItems.spawnEgg, 1, TESEntityRegistry.getEntityID(this));
	}

	@Override
	public boolean isAIEnabled() {
		return true;
	}

	public boolean isButterflyStill() {
		return dataWatcher.getWatchableObjectByte(17) == 1;
	}

	private void setButterflyStill(boolean flag) {
		dataWatcher.updateObject(17, flag ? (byte) 1 : 0);
	}

	@Override
	public IEntityLivingData onSpawnWithEgg(IEntityLivingData data) {
		IEntityLivingData entityData = super.onSpawnWithEgg(data);
		int i = MathHelper.floor_double(posX);
		int k = MathHelper.floor_double(posZ);
		BiomeGenBase biome = TESCrashHandler.getBiomeGenForCoords(worldObj, i, k);
		setButterflyType(ButterflyType.COMMON);
		return entityData;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		if (isButterflyStill()) {
			motionZ = 0.0;
			motionY = 0.0;
			motionX = 0.0;
			posY = MathHelper.floor_double(posY);
			if (worldObj.isRemote) {
				if (rand.nextInt(200) == 0) {
					flapTime = 40;
				}
				if (flapTime > 0) {
					--flapTime;
				}
			}
		} else {
			motionY *= 0.6;
			if (worldObj.isRemote) {
				flapTime = 0;
			}
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setButterflyType(nbt.getInteger("ButterflyType"));
		setButterflyStill(nbt.getBoolean("ButterflyStill"));
	}

	@Override
	public void setUniqueID(UUID uuid) {
		entityUniqueID = uuid;
	}

	@Override
	public void updateAITasks() {
		super.updateAITasks();
		if (isButterflyStill()) {
			int k;
			int j;
			int i = MathHelper.floor_double(posX);
			if (!worldObj.getBlock(i, j = (int) posY - 1, k = MathHelper.floor_double(posZ)).isSideSolid(worldObj, i, j, k, ForgeDirection.UP) || rand.nextInt(400) == 0 || worldObj.getClosestPlayerToEntity(this, 3.0) != null) {
				setButterflyStill(false);
			}
		} else {
			if (currentFlightTarget != null && (!worldObj.isAirBlock(currentFlightTarget.posX, currentFlightTarget.posY, currentFlightTarget.posZ) || currentFlightTarget.posY < 1)) {
				currentFlightTarget = null;
			}
			if (currentFlightTarget == null || rand.nextInt(30) == 0 || currentFlightTarget.getDistanceSquared((int) posX, (int) posY, (int) posZ) < 4.0f) {
				currentFlightTarget = new ChunkCoordinates((int) posX + rand.nextInt(7) - rand.nextInt(7), (int) posY + rand.nextInt(6) - 2, (int) posZ + rand.nextInt(7) - rand.nextInt(7));
			}
			double speed = getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
			double d0 = currentFlightTarget.posX + 0.5 - posX;
			double d1 = currentFlightTarget.posY + 0.5 - posY;
			double d2 = currentFlightTarget.posZ + 0.5 - posZ;
			motionX += (Math.signum(d0) * 0.5 - motionX) * speed;
			motionY += (Math.signum(d1) * 0.7 - motionY) * speed;
			motionZ += (Math.signum(d2) * 0.5 - motionZ) * speed;
			float f = (float) (Math.atan2(motionZ, motionX) * 180.0 / 3.141592653589793) - 90.0f;
			float f1 = MathHelper.wrapAngleTo180_float(f - rotationYaw);
			moveForward = 0.5f;
			rotationYaw += f1;
			if (rand.nextInt(150) == 0 && worldObj.getBlock(MathHelper.floor_double(posX), (int) posY - 1, MathHelper.floor_double(posZ)).isNormalCube()) {
				setButterflyStill(true);
			}
		}
	}

	@Override
	public void updateFallState(double d, boolean flag) {
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setInteger("ButterflyType", getButterflyType().ordinal());
		nbt.setBoolean("ButterflyStill", isButterflyStill());
	}

	public int getFlapTime() {
		return flapTime;
	}

	public enum ButterflyType {
		ULTHOS("ulthos"), QOHOR("qohor"), COMMON("common"), SOTHORYOS("sothoryos");

		private final String textureDir;

		ButterflyType(String s) {
			textureDir = s;
		}

		public String getTextureDir() {
			return textureDir;
		}
	}
}