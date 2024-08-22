package tes.client.gui;

import tes.common.entity.animal.TESEntityHorse;
import tes.common.inventory.TESContainerMountInventory;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.inventory.IInventory;

public class TESGuiMountInventory extends GuiScreenHorseInventory {
	public TESGuiMountInventory(IInventory playerInv, IInventory horseInv, TESEntityHorse horse) {
		super(playerInv, horseInv, horse);
		inventorySlots = new TESContainerMountInventory(playerInv, horseInv, horse);
	}
}