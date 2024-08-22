package tes.common.item.other;

import tes.common.entity.animal.TESEntityBear;
import tes.common.entity.other.inanimate.TESEntityBearRug;
import tes.common.entity.other.inanimate.TESEntityRugBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemBearRug extends TESItemRugBase {
	public TESItemBearRug() {
		super(TESEntityBear.BearType.bearTypeNames());
	}

	@Override
	public TESEntityRugBase createRug(World world, ItemStack itemstack) {
		TESEntityBearRug rug = new TESEntityBearRug(world);
		rug.setRugType(TESEntityBear.BearType.forID(itemstack.getItemDamage()));
		return rug;
	}
}