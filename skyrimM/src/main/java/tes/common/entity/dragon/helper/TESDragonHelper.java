package tes.common.entity.dragon.helper;

import tes.common.entity.dragon.TESEntityDragon;
import net.minecraft.entity.DataWatcher;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Random;

public abstract class TESDragonHelper {
	protected final TESEntityDragon dragon;
	protected final DataWatcher dataWatcher;
	protected final Random rand;

	protected TESDragonHelper(TESEntityDragon dragon) {
		this.dragon = dragon;
		dataWatcher = dragon.getDataWatcher();
		rand = dragon.getRNG();
	}

	public abstract void applyEntityAttributes();

	public abstract void onDeath();

	public abstract void onDeathUpdate();

	public abstract void onLivingUpdate();

	public abstract void readFromNBT(NBTTagCompound nbt);

	public abstract void writeToNBT(NBTTagCompound nbt);
}