package tes.common.entity.animal;

import tes.common.item.other.TESItemLionRug;
import net.minecraft.world.World;

public class TESEntityLioness extends TESEntityLionBase {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityLioness(World world) {
		super(world);
	}

	@Override
	public TESItemLionRug.LionRugType getLionRugType() {
		return TESItemLionRug.LionRugType.LIONESS;
	}

	@Override
	public boolean isMale() {
		return false;
	}
}