package tes.common.tileentity;

import tes.common.entity.TESEntityRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityChest;

public class TESTileEntitySpawnerChest extends TileEntityChest {
	private String entityClassName = "";

	public Entity createMob() {
		return EntityList.createEntityByName(entityClassName, worldObj);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		entityClassName = nbt.getString("MobID");
	}

	public void setMobID(Class<? extends Entity> entityClass) {
		entityClassName = TESEntityRegistry.getStringFromClass(entityClass);
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setString("MobID", entityClassName);
	}
}