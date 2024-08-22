package tes.common;

import cpw.mods.fml.common.IFuelHandler;
import tes.common.block.sapling.TESBlockSaplingBase;
import tes.common.database.TESBlocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class TESFuelHandler implements IFuelHandler {
	public static final TESFuelHandler INSTANCE = new TESFuelHandler();

	private TESFuelHandler() {
	}

	@Override
	public int getBurnTime(ItemStack itemstack) {
		Item item = itemstack.getItem();
		if (item instanceof ItemBlock && ((ItemBlock) item).field_150939_a instanceof TESBlockSaplingBase) {
			return 100;
		}
		if (item == Items.reeds || item == Item.getItemFromBlock(TESBlocks.reeds) || item == Item.getItemFromBlock(TESBlocks.driedReeds) || item == Item.getItemFromBlock(TESBlocks.cornStalk)) {
			return 100;
		}
		return 0;
	}
}