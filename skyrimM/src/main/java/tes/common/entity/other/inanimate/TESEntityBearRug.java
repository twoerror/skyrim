package tes.common.entity.other.inanimate;

import tes.common.database.TESItems;
import tes.common.entity.animal.TESEntityBear;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TESEntityBearRug extends TESEntityRugBase {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityBearRug(World world) {
		super(world);
		setSize(1.8f, 0.3f);
	}

	@Override
	public void entityInit() {
		super.entityInit();
		dataWatcher.addObject(18, (byte) 0);
	}

	@Override
	public ItemStack getRugItem() {
		return new ItemStack(TESItems.bearRug, 1, getRugType().getBearID());
	}

	public TESEntityBear.BearType getRugType() {
		byte i = dataWatcher.getWatchableObjectByte(18);
		return TESEntityBear.BearType.forID(i);
	}

	public void setRugType(TESEntityBear.BearType t) {
		dataWatcher.updateObject(18, (byte) t.getBearID());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setRugType(TESEntityBear.BearType.forID(nbt.getByte("RugType")));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("RugType", (byte) getRugType().getBearID());
	}
}