package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.entity.other.TESEntityGiantBase;
import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.utils.TESInventoryHiredReplacedItems;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class TESSlotHiredReplaceItem extends Slot {
	private final TESEntityNPC theNPC;
	private final TESInventoryHiredReplacedItems npcInv;
	private final Slot parentSlot;

	public TESSlotHiredReplaceItem(Slot slot, TESEntityNPC npc) {
		super(slot.inventory, slot.getSlotIndex(), slot.xDisplayPosition, slot.yDisplayPosition);
		int i;
		parentSlot = slot;
		theNPC = npc;
		npcInv = theNPC.getHiredReplacedInv();
		if (!theNPC.worldObj.isRemote && npcInv.hasReplacedEquipment(i = getSlotIndex())) {
			inventory.setInventorySlotContents(i, npcInv.getEquippedReplacement(i));
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getBackgroundIconIndex() {
		return parentSlot.getBackgroundIconIndex();
	}

	@Override
	public int getSlotStackLimit() {
		return parentSlot.getSlotStackLimit();
	}

	@Override
	public boolean isItemValid(ItemStack itemstack) {
		return parentSlot.isItemValid(itemstack) && !(theNPC instanceof TESEntityGiantBase);
	}

	@Override
	public void onSlotChanged() {
		super.onSlotChanged();
		if (!theNPC.worldObj.isRemote) {
			npcInv.onEquipmentChanged(getSlotIndex(), getStack());
		}
	}
}