package tes.common.entity.other.inanimate;

import tes.common.database.TESItems;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class TESEntityPlowcart extends TESEntityCart {
	private boolean plowing;

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityPlowcart(World worldIn) {
		super(worldIn);
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityPlowcart(World worldIn, double x, double y, double z) {
		super(worldIn, x, y, z);
	}

	@Override
	public boolean attackEntityFrom(DamageSource damagesource, float f) {
		if (!isDead && !worldObj.isRemote) {
			setBeenAttacked();
			worldObj.playSoundAtEntity(this, Blocks.planks.stepSound.getBreakSound(), (Blocks.planks.stepSound.getVolume() + 1.0f) / 2.0f, Blocks.planks.stepSound.getPitch() * 0.8f);
			boolean drop = !(damagesource.getEntity() instanceof EntityPlayer) || !((EntityPlayer) damagesource.getEntity()).capabilities.isCreativeMode;
			dropAsItem(drop);
		}
		return true;
	}

	private void dropAsItem(boolean drop) {
		setDead();
		if (drop) {
			entityDropItem(new ItemStack(TESItems.plowcart), 0.0f);
		}
	}

	public boolean getPlowing() {
		return plowing;
	}

	@Override
	public boolean interactFirst(EntityPlayer player) {
		plowing = !plowing;
		return true;
	}

	@Override
	public void onUpdate() {
		Block targetblock;
		super.onUpdate();
		double bladeOffset = 1.2;
		if (getPulling() != null && (targetblock = worldObj.getBlock((int) (posX - getLookVec().xCoord * bladeOffset), (int) (posY - 1.0), (int) (posZ - getLookVec().zCoord * bladeOffset))) != null && worldObj.isAirBlock((int) (posX - getLookVec().xCoord * bladeOffset), (int) posY, (int) (posZ - getLookVec().zCoord * bladeOffset)) && (targetblock == Blocks.dirt || targetblock == Blocks.grass) && plowing) {
			worldObj.setBlock((int) (posX - getLookVec().xCoord * bladeOffset), (int) (posY - 1.0), (int) (posZ - getLookVec().zCoord * bladeOffset), Blocks.farmland, 7, 2);
		}
	}
}