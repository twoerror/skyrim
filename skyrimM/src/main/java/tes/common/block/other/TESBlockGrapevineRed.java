package tes.common.block.other;

import tes.common.database.TESItems;
import net.minecraft.item.Item;

public class TESBlockGrapevineRed extends TESBlockGrapevine {
	public TESBlockGrapevineRed() {
		super(true);
	}

	@Override
	public Item getGrapeItem() {
		return TESItems.grapeRed;
	}

	@Override
	public Item getGrapeSeedsItem() {
		return TESItems.seedsGrapeRed;
	}
}