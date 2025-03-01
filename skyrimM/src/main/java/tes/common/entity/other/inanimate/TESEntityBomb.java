package tes.common.entity.other.inanimate;

import tes.TES;
import tes.common.block.other.TESBlockBomb;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TESEntityBomb extends EntityTNTPrimed {
	private boolean droppedByPlayer;
	private boolean droppedByHiredUnit;
	private boolean droppedTargetingPlayer;
	private int bombFuse;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBomb(World world) {
		super(world);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBomb(World world, double d, double d1, double d2, EntityLivingBase entity) {
		super(world, d, d1, d2, entity);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(16, (byte) 0);
	}

	private void explodeBomb() {
		boolean doTerrainDamage = false;
		if (droppedByPlayer) {
			doTerrainDamage = true;
		} else if (droppedByHiredUnit || droppedTargetingPlayer) {
			doTerrainDamage = TES.canGrief(worldObj);
		}
		int meta = getBombStrengthLevel();
		int strength = TESBlockBomb.getBombStrengthLevel(meta);
		boolean fire = TESBlockBomb.isFireBomb(meta);
		worldObj.newExplosion(this, posX, posY, posZ, (strength + 1) * 4.0f, fire, doTerrainDamage);
	}

	public int getBombStrengthLevel() {
		return dataWatcher.getWatchableObjectByte(16);
	}

	public void setBombStrengthLevel(int i) {
		dataWatcher.updateObject(16, (byte) i);
		bombFuse = 40 + TESBlockBomb.getBombStrengthLevel(i) * 20;
	}

	@Override
	public void onUpdate() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		motionY -= 0.04;
		moveEntity(motionX, motionY, motionZ);
		motionX *= 0.98;
		motionY *= 0.98;
		motionZ *= 0.98;
		if (onGround) {
			motionX *= 0.7;
			motionZ *= 0.7;
			motionY *= -0.5;
		}
		--bombFuse;
		if (bombFuse <= 0 && !worldObj.isRemote) {
			setDead();
			explodeBomb();
		} else {
			worldObj.spawnParticle("smoke", posX, posY + 0.7, posZ, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		droppedByPlayer = nbt.getBoolean("DroppedByPlayer");
		droppedByHiredUnit = nbt.getBoolean("DroppedByHiredUnit");
		droppedTargetingPlayer = nbt.getBoolean("DroppedTargetingPlayer");
		setBombStrengthLevel(nbt.getInteger("BombStrengthLevel"));
		bombFuse = nbt.getInteger("BombFuse");
	}

	public void setFuseFromExplosion() {
		bombFuse = worldObj.rand.nextInt(bombFuse / 4) + bombFuse / 8;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setBoolean("DroppedByPlayer", droppedByPlayer);
		nbt.setBoolean("DroppedByHiredUnit", droppedByHiredUnit);
		nbt.setBoolean("DroppedTargetingPlayer", droppedTargetingPlayer);
		nbt.setInteger("BombStrengthLevel", getBombStrengthLevel());
		nbt.setInteger("BombFuse", bombFuse);
	}

	public void setDroppedTargetingPlayer(boolean droppedTargetingPlayer) {
		this.droppedTargetingPlayer = droppedTargetingPlayer;
	}

	public void setDroppedByHiredUnit(boolean droppedByHiredUnit) {
		this.droppedByHiredUnit = droppedByHiredUnit;
	}

	public void setDroppedByPlayer(boolean droppedByPlayer) {
		this.droppedByPlayer = droppedByPlayer;
	}

	public int getBombFuse() {
		return bombFuse;
	}
}