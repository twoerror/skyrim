package tes.common.entity.other.utils;

import tes.common.entity.other.TESEntityNPC;
import tes.common.entity.other.iface.TESHireableBase;
import tes.common.entity.other.iface.TESMercenary;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class TESMercenaryTradeEntry extends TESUnitTradeEntry {
	private final TESMercenary theMerc;

	private TESMercenaryTradeEntry(TESMercenary merc) {
		super((Class<? extends Entity>) merc.getClass(), merc.getMercBaseCost(), merc.getMercAlignmentRequired());
		theMerc = merc;
	}

	public static TESMercenaryTradeEntry createFor(TESMercenary merc) {
		return new TESMercenaryTradeEntry(merc);
	}

	@Override
	public TESEntityNPC getOrCreateHiredNPC(World world) {
		return (TESEntityNPC) theMerc;
	}

	@Override
	public boolean hasRequiredCostAndAlignment(EntityPlayer entityplayer, TESHireableBase trader) {
		return !((TESEntityNPC) theMerc).getHireableInfo().isActive() && super.hasRequiredCostAndAlignment(entityplayer, trader);
	}
}