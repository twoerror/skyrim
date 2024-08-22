package tes.common.entity.animal;

import tes.common.item.other.TESItemLionRug;
import net.minecraft.world.World;

public class TESEntityLion extends TESEntityLionBase {
	@SuppressWarnings({"WeakerAccess", "unused"})
	public TESEntityLion(World world) {
		super(world);
	}

	@Override
	public TESItemLionRug.LionRugType getLionRugType() {
		return TESItemLionRug.LionRugType.LION;
	}

	@Override
	public boolean isMale() {
		return true;
	}
}