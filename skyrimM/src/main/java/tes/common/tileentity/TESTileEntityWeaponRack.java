package tes.common.tileentity;

import tes.common.item.TESWeaponStats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TESTileEntityWeaponRack extends TileEntity {
	private ItemStack weaponItem;
	private EntityLivingBase rackEntity;

	public static boolean canAcceptItem(ItemStack itemstack) {
		if (itemstack != null) {
			Item item = itemstack.getItem();
			return TESWeaponStats.isMeleeWeapon(itemstack) || TESWeaponStats.isRangedWeapon(itemstack) || item instanceof ItemHoe || item instanceof ItemFishingRod;
		}
		return false;
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
	}

	public EntityLivingBase getEntityForRender() {
		if (rackEntity == null) {
			rackEntity = new EntityLiving(worldObj) {
			};
		}
		return rackEntity;
	}

	public ItemStack getWeaponItem() {
		return weaponItem;
	}

	public void setWeaponItem(ItemStack item) {
		ItemStack item1 = item;
		if (item1 != null && item1.stackSize <= 0) {
			item1 = null;
		}
		weaponItem = item1;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	@Override
	public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
		NBTTagCompound data = packet.func_148857_g();
		readFromNBT(data);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		boolean hasWeapon = nbt.getBoolean("HasWeapon");
		weaponItem = hasWeapon ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("WeaponItem")) : null;
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("HasWeapon", weaponItem != null);
		if (weaponItem != null) {
			nbt.setTag("WeaponItem", weaponItem.writeToNBT(new NBTTagCompound()));
		}
	}
}