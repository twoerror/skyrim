package tes.common.inventory;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import tes.common.entity.animal.TESEntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class TESContainerMountInventory extends ContainerHorseInventory {
	public TESContainerMountInventory(IInventory playerInv, IInventory horseInv, TESEntityHorse horse) {
		super(playerInv, horseInv, horse);
		ArrayList<Slot> slots = new ArrayList<Slot>(inventorySlots);
		inventorySlots.clear();
		inventoryItemStacks.clear();
		addSlotToContainer(slots.get(0));
		Slot armorSlot = slots.get(1);
		addSlotToContainer(new MySlot(armorSlot, horse));
		for (int i = 2; i < slots.size(); ++i) {
			addSlotToContainer(slots.get(i));
		}
	}

	private static class MySlot extends Slot {
		private final TESEntityHorse horse;

		private MySlot(Slot armorSlot, TESEntityHorse horse) {
			super(armorSlot.inventory, armorSlot.slotNumber, armorSlot.xDisplayPosition, armorSlot.yDisplayPosition);
			this.horse = horse;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean func_111238_b() {
			return horse.func_110259_cr();
		}

		@Override
		public boolean isItemValid(ItemStack itemstack) {
			return super.isItemValid(itemstack) && horse.func_110259_cr() && horse.isMountArmorValid(itemstack);
		}
	}
}