package tes.common.inventory;

import tes.common.entity.other.TESEntityNPC;
import net.minecraft.item.ItemStack;

public class TESInventoryNPC extends TESEntityInventory {
	protected final TESEntityNPC theNPC;

	public TESInventoryNPC(String s, TESEntityNPC npc, int i) {
		super(s, npc, i);
		theNPC = npc;
	}

	@Override
	public void dropItem(ItemStack itemstack) {
		theNPC.npcDropItem(itemstack, 0.0f, false, true);
	}
}