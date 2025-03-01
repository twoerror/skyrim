package tes.common.entity.other.inanimate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.TESBannerProtection;
import tes.common.database.TESItems;
import tes.common.item.other.TESItemBanner;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import java.util.List;

public class TESEntityBannerWall extends EntityHanging {
	private NBTTagCompound protectData;
	private boolean updatedClientBB;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBannerWall(World world) {
		super(world);
		setSize(0.0f, 0.0f);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBannerWall(World world, int i, int j, int k, int dir) {
		super(world, i, j, k, dir);
		setSize(0.0f, 0.0f);
		setDirection(dir);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		return (worldObj.isRemote || !(damagesource.getEntity() instanceof EntityPlayer) || !TESBannerProtection.isProtected(worldObj, this, TESBannerProtection.forPlayer((EntityPlayer) damagesource.getEntity()), true)) && super.attackEntityFrom(damagesource, f);
	}

	@Override
	public void entityInit() {
		dataWatcher.addObject(10, 0);
		dataWatcher.addObject(11, 0);
		dataWatcher.addObject(12, 0);
		dataWatcher.addObject(13, (short) 0);
		dataWatcher.addObject(18, (short) 0);
	}

	private ItemStack getBannerItem() {
		ItemStack item = new ItemStack(TESItems.banner, 1, getBannerType().getBannerID());
		if (protectData != null) {
			TESItemBanner.setProtectionData(item, protectData);
		}
		return item;
	}

	public TESItemBanner.BannerType getBannerType() {
		return TESItemBanner.BannerType.forID(getBannerTypeID());
	}

	public void setBannerType(TESItemBanner.BannerType type) {
		setBannerTypeID(type.getBannerID());
	}

	private int getBannerTypeID() {
		return dataWatcher.getWatchableObjectShort(18);
	}

	private void setBannerTypeID(int i) {
		dataWatcher.updateObject(18, (short) i);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int getBrightnessForRender(float f) {
		int k;
		int i;
		if (!updatedClientBB) {
			getWatchedDirection();
			setDirection(hangingDirection);
			updatedClientBB = true;
		}
		if (worldObj.blockExists(i = MathHelper.floor_double(posX), 0, k = MathHelper.floor_double(posZ))) {
			int j = MathHelper.floor_double(posY);
			return worldObj.getLightBrightnessForSkyBlocks(i, j, k, 0);
		}
		return 0;
	}

	@Override
	public int getHeightPixels() {
		return 32;
	}

	@Override
	public ItemStack getPickedResult(MovingObjectPosition target) {
		return getBannerItem();
	}

	private void getWatchedDirection() {
		field_146063_b = dataWatcher.getWatchableObjectInt(10);
		field_146064_c = dataWatcher.getWatchableObjectInt(11);
		field_146062_d = dataWatcher.getWatchableObjectInt(12);
		hangingDirection = dataWatcher.getWatchableObjectShort(13);
	}

	@Override
	public int getWidthPixels() {
		return 16;
	}

	@Override
	public void onBroken(Entity entity) {
		worldObj.playSoundAtEntity(this, Blocks.planks.stepSound.getBreakSound(), (Blocks.planks.stepSound.getVolume() + 1.0f) / 2.0f, Blocks.planks.stepSound.getPitch() * 0.8f);
		boolean flag = !(entity instanceof EntityPlayer) || !((EntityPlayer) entity).capabilities.isCreativeMode;
		if (flag) {
			entityDropItem(getBannerItem(), 0.0f);
		}
	}

	@Override
	public void onUpdate() {
		if (worldObj.isRemote && !updatedClientBB) {
			getWatchedDirection();
			setDirection(hangingDirection);
			updatedClientBB = true;
		}
		super.onUpdate();
	}

	@Override
	public boolean onValidSurface() {
		if (!worldObj.getCollidingBoundingBoxes(this, boundingBox).isEmpty()) {
			return false;
		}
		int i = field_146063_b;
		int j = field_146064_c;
		int k = field_146062_d;
		Block block = worldObj.getBlock(i, j, k);
		if (!block.getMaterial().isSolid()) {
			return false;
		}
		List<? extends Entity> list = worldObj.getEntitiesWithinAABBExcludingEntity(this, boundingBox);
		for (Entity obj : list) {
			if (obj instanceof EntityHanging) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setBannerTypeID(nbt.getShort("BannerType"));
		if (nbt.hasKey("ProtectData")) {
			protectData = nbt.getCompoundTag("ProtectData");
		}
	}

	@Override
	public void setDirection(int dir) {
		int dir1 = dir;
		float edge;
		float zSize;
		float xSize;
		float zEdge;
		float xEdge;
		if (dir1 < 0 || dir1 >= Direction.directions.length) {
			dir1 = 0;
		}
		hangingDirection = dir1;
		prevRotationYaw = rotationYaw = Direction.rotateOpposite[dir1] * 90.0f;
		float width = 1.0f;
		float thickness = 0.0625f;
		float yEdge = edge = 0.01f;
		if (dir1 == 0 || dir1 == 2) {
			xSize = width;
			zSize = thickness;
			xEdge = thickness + edge;
			zEdge = edge;
		} else {
			xSize = thickness;
			zSize = width;
			xEdge = edge;
			zEdge = thickness + edge;
		}
		float f = field_146063_b + 0.5f;
		float f1 = field_146064_c + 0.5f;
		float f2 = field_146062_d + 0.5f;
		float f3 = 0.5f + thickness / 2.0f;
		setPosition(f += Direction.offsetX[dir1] * f3, f1, f2 += Direction.offsetZ[dir1] * f3);
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		boundingBox.setBounds(f - xSize / 2.0f, f1 - 1.5f, f2 - zSize / 2.0f, f + xSize / 2.0f, f1 + 0.5f, f2 + zSize / 2.0f);
		boundingBox.setBB(boundingBox.contract(xEdge, yEdge, zEdge));
		if (!worldObj.isRemote) {
			updateWatchedDirection();
		}
	}

	public void setProtectData(NBTTagCompound nbt) {
		protectData = nbt;
	}

	private void updateWatchedDirection() {
		dataWatcher.updateObject(10, field_146063_b);
		dataWatcher.updateObject(11, field_146064_c);
		dataWatcher.updateObject(12, field_146062_d);
		dataWatcher.updateObject(13, (short) hangingDirection);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setShort("BannerType", (short) getBannerTypeID());
		if (protectData != null) {
			nbt.setTag("ProtectData", protectData);
		}
	}
}