package tes.common.entity.other.inanimate;

import tes.common.database.TESItems;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TESEntityGiraffeRug extends TESEntityRugBase {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityGiraffeRug(World world) {
		super(world);
		setSize(2.0f, 0.3f);
	}

	@Override
	public ItemStack getRugItem() {
		return new ItemStack(TESItems.giraffeRug);
	}
}