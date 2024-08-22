package tes.common.entity.animal;

import tes.common.database.TESItems;
import tes.common.util.TESCrashHandler;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class TESEntityWhiteBison extends TESEntityBison {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityWhiteBison(World world) {
		super(world);
	}

	@Override
	public EntityCow createChild(EntityAgeable entity) {
		return new TESEntityWhiteBison(worldObj);
	}

	@Override
	public void dropHornItem() {
		dropItem(TESItems.whiteBisonHorn, 1);
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
}