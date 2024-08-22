package tes.common.entity.other.utils;

import tes.TES;
import tes.common.faction.TESFaction;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;

public class TESNPCSelectByFaction implements IEntitySelector {
	private final TESFaction faction;

	public TESNPCSelectByFaction(TESFaction f) {
		faction = f;
	}

	@Override
	public boolean isEntityApplicable(Entity entity) {
		return entity.isEntityAlive() && TES.getNPCFaction(entity) == faction;
	}
}