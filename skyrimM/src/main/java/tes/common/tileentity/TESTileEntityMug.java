package tes.common.tileentity;

import tes.common.database.TESItems;
import tes.common.item.TESPoisonedDrinks;
import tes.common.item.other.TESItemMug;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TESTileEntityMug extends TileEntity {
	private TESItemMug.Vessel mugVessel;
	private ItemStack mugItem;

	public boolean canPoisonMug() {
		ItemStack itemstack = getMugItem();
		return TESPoisonedDrinks.canPoison(itemstack) && !TESPoisonedDrinks.isDrinkPoisoned(itemstack);
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound data = new NBTTagCompound();
		writeToNBT(data);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, data);
	}

	public ItemStack getMugItem() {
		if (mugItem == null) {
			return getVessel().getEmptyVessel();
		}
		ItemStack copy = mugItem.copy();
		if (TESItemMug.isItemFullDrink(copy)) {
			TESItemMug.setVessel(copy, getVessel(), true);
		}
		return copy;
	}

	public void setMugItem(ItemStack itemstack) {
		ItemStack itemstack1 = itemstack;
		if (itemstack1 != null && itemstack1.stackSize <= 0) {
			itemstack1 = null;
		}
		mugItem = itemstack1;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	public ItemStack getMugItemForRender() {
		return TESItemMug.getEquivalentDrink(getMugItem());
	}

	public TESItemMug.Vessel getVessel() {
		if (mugVessel == null) {
			for (TESItemMug.Vessel v : TESItemMug.Vessel.values()) {
				if (!v.isCanPlace() || v.getBlock() != getBlockType()) {
					continue;
				}
				return v;
			}
			return TESItemMug.Vessel.MUG;
		}
		return mugVessel;
	}

	public void setVessel(TESItemMug.Vessel v) {
		mugVessel = v;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	public boolean isEmpty() {
		return !TESItemMug.isItemFullDrink(getMugItem());
	}

	@Override
	public void onDataPacket(NetworkManager manager, S35PacketUpdateTileEntity packet) {
		NBTTagCompound data = packet.func_148857_g();
		readFromNBT(data);
	}

	public void poisonMug(EntityPlayer entityplayer) {
		ItemStack itemstack = getMugItem();
		TESPoisonedDrinks.setDrinkPoisoned(itemstack, true);
		TESPoisonedDrinks.setPoisonerPlayer(itemstack, entityplayer);
		setMugItem(itemstack);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		if (nbt.hasKey("ItemID")) {
			Item item = Item.getItemById(nbt.getInteger("ItemID"));
			if (item != null) {
				int damage = nbt.getInteger("ItemDamage");
				mugItem = new ItemStack(item, 1, damage);
			}
		} else {
			boolean hasItem = nbt.getBoolean("HasMugItem");
			mugItem = hasItem ? ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("MugItem")) : null;
		}
		if (nbt.hasKey("Vessel")) {
			mugVessel = TESItemMug.Vessel.forMeta(nbt.getByte("Vessel"));
		}
	}

	public void setEmpty() {
		setMugItem(null);
	}

	@Override
	public void updateEntity() {
		if (!worldObj.isRemote && isEmpty() && worldObj.canLightningStrikeAt(xCoord, yCoord, zCoord) && worldObj.rand.nextInt(6000) == 0) {
			ItemStack waterItem = new ItemStack(TESItems.mugWater);
			TESItemMug.setVessel(waterItem, getVessel(), false);
			setMugItem(waterItem);
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
			markDirty();
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setBoolean("HasMugItem", mugItem != null);
		if (mugItem != null) {
			nbt.setTag("MugItem", mugItem.writeToNBT(new NBTTagCompound()));
		}
		if (mugVessel != null) {
			nbt.setByte("Vessel", (byte) mugVessel.getId());
		}
	}
}