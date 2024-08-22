package tes.common.entity.other.inanimate;

import tes.common.database.TESItems;
import tes.common.item.other.TESItemLionRug;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TESEntityLionRug extends TESEntityRugBase {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityLionRug(World world) {
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
		return new ItemStack(TESItems.lionRug, 1, getRugType().getLionID());
	}

	public TESItemLionRug.LionRugType getRugType() {
		byte i = dataWatcher.getWatchableObjectByte(18);
		return TESItemLionRug.LionRugType.forID(i);
	}

	public void setRugType(TESItemLionRug.LionRugType t) {
		dataWatcher.updateObject(18, (byte) t.getLionID());
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		setRugType(TESItemLionRug.LionRugType.forID(nbt.getByte("RugType")));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		nbt.setByte("RugType", (byte) getRugType().getLionID());
	}
}