package tes.common.tileentity;

import tes.common.block.other.TESBlockBookshelfStorage;
import tes.common.inventory.TESContainerBookshelf;
import tes.common.item.other.TESItemModifierTemplate;
import tes.common.item.other.TESItemQuestBook;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

import java.util.List;

public class TESTileEntityBookshelf extends TileEntity implements IInventory {
	private ItemStack[] chestContents = new ItemStack[getSizeInventory()];
	private int numPlayersUsing;
	private int ticksSinceSync;

	public static boolean isBookItem(ItemStack itemstack) {
		if (itemstack != null) {
			Item item = itemstack.getItem();
			return item instanceof ItemBook || item instanceof ItemWritableBook || item instanceof ItemEditableBook || item instanceof TESItemQuestBook || item instanceof ItemEnchantedBook || item instanceof ItemMapBase || item == Items.paper || item instanceof TESItemModifierTemplate;
		}
		return false;
	}

	@Override
	public void closeInventory() {
		if (getBlockType() instanceof TESBlockBookshelfStorage) {
			--numPlayersUsing;
		}
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (chestContents[i] != null) {
			if (chestContents[i].stackSize <= j) {
				ItemStack itemstack = chestContents[i];
				chestContents[i] = null;
				markDirty();
				return itemstack;
			}
			ItemStack itemstack = chestContents[i].splitStack(j);
			if (chestContents[i].stackSize == 0) {
				chestContents[i] = null;
			}
			markDirty();
			return itemstack;
		}
		return null;
	}

	@Override
	public String getInventoryName() {
		return "tes.container.bookshelf";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public int getSizeInventory() {
		return 27;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return chestContents[i];
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (chestContents[i] != null) {
			ItemStack itemstack = chestContents[i];
			chestContents[i] = null;
			return itemstack;
		}
		return null;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		updateContainingBlockInfo();
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return isBookItem(itemstack);
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this && entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0;
	}

	@Override
	public void openInventory() {
		if (numPlayersUsing < 0) {
			numPlayersUsing = 0;
		}
		++numPlayersUsing;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList itemTags = nbt.getTagList("Items", 10);
		chestContents = new ItemStack[getSizeInventory()];
		for (int i = 0; i < itemTags.tagCount(); ++i) {
			NBTTagCompound slotData = itemTags.getCompoundTagAt(i);
			int slot = slotData.getByte("Slot") & 0xFF;
			if (slot < chestContents.length) {
				chestContents[slot] = ItemStack.loadItemStackFromNBT(slotData);
			}
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		chestContents[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > getInventoryStackLimit()) {
			itemstack.stackSize = getInventoryStackLimit();
		}
		markDirty();
	}

	@Override
	public void updateEntity() {
		super.updateEntity();
		++ticksSinceSync;
		if (!worldObj.isRemote && numPlayersUsing != 0 && (ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0) {
			numPlayersUsing = 0;
			float range = 16.0f;
			List<EntityPlayer> players = worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(xCoord - range, yCoord - range, zCoord - range, xCoord + 1 + range, yCoord + 1 + range, zCoord + 1 + range));
			for (EntityPlayer entityplayer : players) {
				if (!(entityplayer.openContainer instanceof TESContainerBookshelf) || ((TESContainerBookshelf) entityplayer.openContainer).getShelfInv() != this) {
					continue;
				}
				++numPlayersUsing;
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList itemTags = new NBTTagList();
		for (int i = 0; i < chestContents.length; ++i) {
			if (chestContents[i] == null) {
				continue;
			}
			NBTTagCompound slotData = new NBTTagCompound();
			slotData.setByte("Slot", (byte) i);
			chestContents[i].writeToNBT(slotData);
			itemTags.appendTag(slotData);
		}
		nbt.setTag("Items", itemTags);
	}

	public int getNumPlayersUsing() {
		return numPlayersUsing;
	}
}