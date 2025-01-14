package tes.common.entity.other.inanimate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.database.TESItems;
import tes.common.entity.TESEntityRegistry;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESTradeable;
import tes.common.entity.other.info.TESTraderInfo;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class TESEntityTraderRespawn extends Entity {
	private static final int MAX_SCALE = 40;

	private float spawnerSpin;
	private float prevSpawnerSpin;

	private NBTTagCompound traderData;
	private String traderClassID;

	private boolean traderHasHome;
	private float traderHomeRadius;
	private int timeUntilSpawn;
	private int prevBobbingTime;
	private int bobbingTime;
	private int traderHomeX;
	private int traderHomeY;
	private int traderHomeZ;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityTraderRespawn(World world) {
		super(world);
		setSize(0.75f, 0.75f);
		spawnerSpin = rand.nextFloat() * 360.0f;
	}

	@Override
	public void applyEntityCollision(Entity entity) {
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		Entity entity = damagesource.getEntity();
		if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode) {
			if (!worldObj.isRemote) {
				Block.SoundType sound = Blocks.glass.stepSound;
				worldObj.playSoundAtEntity(this, sound.getBreakSound(), (sound.getVolume() + 1.0f) / 2.0f, sound.getPitch() * 0.8f);
				worldObj.setEntityState(this, (byte) 16);
				setDead();
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return true;
	}

	public void copyTraderDataFrom(TESEntityNPC entity) {
		traderClassID = TESEntityRegistry.getStringFromClass(entity.getClass());
		traderHasHome = entity.hasHome();
		if (traderHasHome) {
			ChunkCoordinates home = entity.getHomePosition();
			traderHomeX = home.posX;
			traderHomeY = home.posY;
			traderHomeZ = home.posZ;
			traderHomeRadius = entity.func_110174_bM();
		}
		if (entity instanceof TESTradeable) {
			TESTraderInfo traderInfo = entity.getTraderInfo();
			traderData = new NBTTagCompound();
			traderInfo.writeToNBT(traderData);
		}
	}

	@Override
	public void entityInit() {
		dataWatcher.addObject(16, 0);
		dataWatcher.addObject(17, (byte) 0);
		dataWatcher.addObject(18, "");
	}

	public float getBobbingOffset(float tick) {
		float f = bobbingTime - prevBobbingTime;
		return MathHelper.sin((prevBobbingTime + f * tick) / 5.0f) * 0.25f;
	}

	private String getClientTraderString() {
		return dataWatcher.getWatchableObjectString(18);
	}

	private void setClientTraderString(String s) {
		dataWatcher.updateObject(18, s);
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		int entityID = TESEntityRegistry.getIDFromString(getClientTraderString());
		if (entityID > 0) {
			return new ItemStack(TESItems.spawnEgg, 1, entityID);
		}
		return null;
	}

	private int getScale() {
		return dataWatcher.getWatchableObjectInt(16);
	}

	private void setScale(int i) {
		dataWatcher.updateObject(16, i);
	}

	public float getScaleFloat(float tick) {
		float scale = getScale();
		if (scale < MAX_SCALE) {
			scale += tick;
		}
		return scale / MAX_SCALE;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleHealthUpdate(byte b) {
		if (b == 16) {
			for (int l = 0; l < 16; ++l) {
				worldObj.spawnParticle("iconcrack_" + Item.getIdFromItem(TESItems.goldRing), posX + (rand.nextDouble() - 0.5) * width, posY + rand.nextDouble() * height, posZ + (rand.nextDouble() - 0.5) * width, 0.0, 0.0, 0.0);
			}
		} else {
			super.handleHealthUpdate(b);
		}
	}

	@Override
	public boolean hitByEntity(Entity entity) {
		return entity instanceof EntityPlayer && attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer) entity), 0.0f);
	}

	private boolean isSpawnImminent() {
		return dataWatcher.getWatchableObjectByte(17) == 1;
	}

	public void onSpawn() {
		motionY = 0.25;
		timeUntilSpawn = MathHelper.getRandomIntegerInRange(rand, 10, 30) * 1200;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		prevSpawnerSpin = spawnerSpin;
		spawnerSpin = spawnerSpin + (isSpawnImminent() ? 24.0f : 6.0f);
		prevSpawnerSpin = MathHelper.wrapAngleTo180_float(prevSpawnerSpin);
		spawnerSpin = MathHelper.wrapAngleTo180_float(spawnerSpin);
		if (getScale() < MAX_SCALE) {
			if (!worldObj.isRemote) {
				setScale(getScale() + 1);
			}
			motionX = 0.0;
			motionY *= 0.9;
		} else {
			motionX = 0.0;
			motionY = 0.0;
		}
		motionZ = 0.0;
		moveEntity(motionX, motionY, motionZ);
		if (!worldObj.isRemote) {
			setClientTraderString(traderClassID);
			if (!isSpawnImminent() && timeUntilSpawn <= 1200) {
				setSpawnImminent();
			}
			if (timeUntilSpawn > 0) {
				--timeUntilSpawn;
			} else {
				boolean flag = false;
				Entity entity = EntityList.createEntityByName(traderClassID, worldObj);
				if (entity instanceof TESEntityNPC) {
					TESEntityNPC trader = (TESEntityNPC) entity;
					trader.setLocationAndAngles(posX, posY, posZ, rand.nextFloat() * 360.0f, 0.0f);
					trader.setSpawnRidingHorse(false);
					trader.setLiftSpawnRestrictions(true);
					boundingBox.offset(0.0, 100.0, 0.0);
					if (trader.getCanSpawnHere()) {
						trader.setLiftSpawnRestrictions(false);
						trader.onSpawnWithEgg(null);
						if (traderHasHome) {
							trader.setHomeArea(traderHomeX, traderHomeY, traderHomeZ, Math.round(traderHomeRadius));
						}
						flag = worldObj.spawnEntityInWorld(trader);
						if (trader instanceof TESTradeable && traderData != null) {
							trader.getTraderInfo().readFromNBT(traderData);
						}
					}
					boundingBox.offset(0.0, -100.0, 0.0);
				}
				if (flag) {
					playSound("random.pop", 1.0f, 0.5f + rand.nextFloat() * 0.5f);
					setDead();
				} else {
					timeUntilSpawn = 60;
					setLocationAndAngles(posX, posY + 1.0, posZ, rotationYaw, rotationPitch);
				}
			}
		} else if (isSpawnImminent()) {
			prevBobbingTime = bobbingTime;
			bobbingTime++;
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		setScale(nbt.getInteger("Scale"));
		timeUntilSpawn = nbt.getInteger("TimeUntilSpawn");
		if (timeUntilSpawn <= 1200) {
			setSpawnImminent();
		}
		traderClassID = nbt.getString("TraderClassID");
		traderHasHome = nbt.getBoolean("TraderHasHome");
		traderHomeX = nbt.getInteger("TraderHomeX");
		traderHomeY = nbt.getInteger("TraderHomeY");
		traderHomeZ = nbt.getInteger("TraderHomeZ");
		traderHomeRadius = nbt.getFloat("TraderHomeRadius");
		if (nbt.hasKey("TraderData")) {
			traderData = nbt.getCompoundTag("TraderData");
		}
	}

	private void setSpawnImminent() {
		dataWatcher.updateObject(17, (byte) 1);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		nbt.setInteger("Scale", getScale());
		nbt.setInteger("TimeUntilSpawn", timeUntilSpawn);
		nbt.setString("TraderClassID", traderClassID);
		nbt.setBoolean("TraderHasHome", traderHasHome);
		nbt.setInteger("TraderHomeX", traderHomeX);
		nbt.setInteger("TraderHomeY", traderHomeY);
		nbt.setInteger("TraderHomeZ", traderHomeZ);
		nbt.setFloat("TraderHomeRadius", traderHomeRadius);
		if (traderData != null) {
			nbt.setTag("TraderData", traderData);
		}
	}

	public float getSpawnerSpin() {
		return spawnerSpin;
	}

	public float getPrevSpawnerSpin() {
		return prevSpawnerSpin;
	}
}