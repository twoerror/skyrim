package tes.common.block.other;

import tes.common.database.TESItems;
import net.minecraft.item.Item;

public class TESBlockGrapevineWhite extends TESBlockGrapevine {
	public TESBlockGrapevineWhite() {
		super(true);
	}

	@Override
	public Item getGrapeItem() {
		return TESItems.grapeWhite;
	}

	@Override
	public Item getGrapeSeedsItem() {
		return TESItems.seedsGrapeWhite;
	}
}