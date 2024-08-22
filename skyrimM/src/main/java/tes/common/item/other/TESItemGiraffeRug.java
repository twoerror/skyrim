package tes.common.item.other;

import tes.common.entity.other.inanimate.TESEntityGiraffeRug;
import tes.common.entity.other.inanimate.TESEntityRugBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESItemGiraffeRug extends TESItemRugBase {
	public TESItemGiraffeRug() {
		super("giraffe");
	}

	@Override
	public TESEntityRugBase createRug(World world, ItemStack itemstack) {
		return new TESEntityGiraffeRug(world);
	}
}